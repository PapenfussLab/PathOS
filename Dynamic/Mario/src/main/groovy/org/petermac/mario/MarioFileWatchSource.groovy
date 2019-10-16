package org.petermac.babble.mario

import groovy.util.logging.Log4j
import java.io.FileReader
import java.io.Reader
import java.nio.file.DirectoryStream
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.attribute.FileTime
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator

import org.petermac.babble.api.*
import org.petermac.yaml.*

/**
 * A Babble source that watches for files and
 * sends the filename or the content as its message.
 *
 * The class name doesn't reflect the fact that it is a factory
 * because the class is named in the babble script, and calling
 * it a factory there looks weird to users.
 *
 * Author: Tom Conway
 * Date: 2018/02/04
 */
@Log4j
class MarioFileWatch implements BabbleSourceFactory {

    /**
     * Actual babble source implementation.
     */
    static class FWatch implements BabbleSource {
        /**
         * Each rule has a glob and some parameters
         * which govern which files are looked for,
         * and what is done when they are found.
         */
        static class Rule {
            public String glob
            private PathMatcher matcher
            private String mode
            private Set seen
            private Map last
            private BabbleCodec codec

            /**
             * Constructor
             *
             * @param   confator    the configuration support object
             * @param   config      YAML derived map containing configuration
             */
            Rule(BabbleConfigurator confator, Map config) {
                Set mustHave = []
                mustHave << 'glob'

                Set mayHave = []
                mayHave << 'mode'
                mayHave << 'read'

                YamlConfig.checkParams(config, mustHave, mayHave)

                glob = config['glob']
                matcher = FileSystems.getDefault().getPathMatcher("glob:${glob}")
                mode = config['mode'] ?: 'create'
                seen = []
                last = [:]

                if (config['read']) {
                    codec = confator.createCodec(config['read'])
                }
            }

            Boolean satisfies(Path p) {
                if (!matcher.matches(p)) {
                    return false
                }
                def s = p.toString()
                switch (mode) {
                    case 'create':
                        if (!(s in seen)) {
                            seen << s
                            return true
                        }
                        return false
                    case 'modify':
                        FileTime lst = Files.getLastModifiedTime(p)
                        if (last[p] == null) {
                            last[p] = lst
                            return true
                        }
                        if (lst.compareTo(last[p]) > 0) {
                            last[p] = lst
                            return true
                        }
                        return false
                    default:
                        log.error "Unknown mode ${mode}"
                        return false
                }
            }

            void makeMessages(Path p, Closure handler) {
                if (codec) {
                    File f = p.toFile()
                    Reader rdr = new FileReader(f)
                    for (Object msg : codec.decodeAll(rdr)) {
                        handler(msg)
                    }
                } else {
                    handler(p.toString())
                }
            }
        }

        /**
         * Trie class for storing the set of paths needed for the rules.
         */
        static class Trie {
            public Map kids
            public List data

            Trie() {
                kids = [:]
                data = []
            }

            void insert(List ks, Rule val) {
                Trie n = this
                ks.each { k ->
                    if (!n.kids[k]) {
                        n.kids[k] = new Trie()
                    }
                    n = n.kids[k]
                }
                n.data << val
            }
        }

        private Trie paths
        private Integer pollTime
        private Boolean finished
        Boolean aggregate
        Boolean bootstrap
        Map regex

        /**
         * Constructor.
         *
         * @param   confator    the configuration support object
         * @param   config      YAML derived map containing configuration
         */
        FWatch(BabbleConfigurator confator, Map config) {
            Set mustHave = []
            mustHave << 'watches'

            Set mayHave = []
            mayHave << 'aggregate'
            mayHave << 'bootstrap'
            mayHave << 'poll-time'
            mayHave << 'regex'

            YamlConfig.checkParams(config, mustHave, mayHave)

            paths = new Trie()

            for (Map wtchCfg : config['watches']) {
                Rule r = new Rule(confator, wtchCfg)
                List parts = []
                new File(r.glob).toPath().each { p ->
                    parts << (p as String)
                }
                paths.insert(parts, r)
            }

            aggregate = false
            if (config['aggregate']) {
                aggregate = config['aggregate'].toBoolean()
            }

            bootstrap = false
            if (config['bootstrap']) {
                bootstrap = config['bootstrap'].toBoolean()
            }

            // Default poll-time is 10 minutes
            pollTime = 60 * 10
            if (config['poll-time']) {
                pollTime = config['poll-time'].toInteger()
            }
            pollTime *= 1000 // seconds to milliseconds

            if (config['regex']) {
                Set reMustHave = []
                reMustHave << 'pattern'
                reMustHave << 'template'

                Set reMayHave = []

                YamlConfig.checkParams(config['regex'], reMustHave, reMayHave)

                regex = config['regex']
            }

            finished = false
        }

        void visit(Integer scanCount, BabbleDestination dst) {
            BabbleSource src = this
            List items = []
            Integer hitCount = 0
            Closure handler = { item ->
                if (scanCount == 0 && !bootstrap) {
                    return
                }
                hitCount += 1
                Object res = item
                if (regex) {
                    def m = (item =~ regex['pattern'])
                    if (!m) {
                        log.warn "file '${item}' didn't match '${regex['pattern']}'"
                        return
                    }
                    if (m.size() > 1) {
                        log.warn "file '${item}' had multiple matches to '${regex['pattern']}'. Using the first one only."
                    }
                    Map vars = [:]
                    for (int i = 0; i < m[0].size(); i++) {
                        String nm = "group_${i}"
                        vars[nm] = m[0][i]
                    }
                    res = MarioMessageTransformer.transform(regex['template'], vars)
                }
                if (aggregate) {
                    items << res
                } else {
                    dst.deliverMessage(src, res)
                }
            }
            Path root
            FileSystems.getDefault().getRootDirectories().each { r ->
                root = r
            }
            visit(root, paths, handler)
            if (aggregate) {
                dst.deliverMessage(src, items)
            }
            log.info "scan ${scanCount} found ${hitCount} matching files"
        }

        void visit(Path ctx, Trie node, Closure handler) {
            for (Rule r : node.data) {
                if (r.satisfies(ctx)) {
                    log.info "hit ${ctx}"
                    r.makeMessages(ctx, handler)
                }
            }
            if (!Files.isDirectory(ctx)) {
                return
            }
            for (String k : node.kids.keySet()) {
                DirectoryStream<Path> dir = Files.newDirectoryStream(ctx, k)
                List ps = []
                try {
                    ps = dir.toList()
                }
                finally {
                    dir.close()
                }
                ps.each { p ->
                    visit(p, node.kids[k], handler)
                }
            }
        }

        /**
         *
         * An invocation of the stop() method should cause the
         * implementation to obtain and deliver no further messages.
         *
         */
        void stop() {
            finished = true
        }

        /**
         *
         * Translate the maps-and-lists representation of an HL7 message
         * to maps-and-lists for PathOS.
         *
         */
        void slurpMessages(BabbleDestination dest) {
            Integer scanCount = 0
            while (!finished) {
                List items = visit(scanCount, dest)
                scanCount += 1
                sleep(pollTime)
            }
        }
    }

    String name() {
        return 'watch'
    }

    BabbleSource create(BabbleConfigurator confator, Map config) {
        return new FWatch(confator, config)
    }

    String usage() {
        return \
"""
The MarioFileWatch class provides filenames for files that
meet a configured condition: when they are created, modified, removed.

Configuration parameters:

watches:
    [mandatory] A list of watch configurations, which have the following structure:
        glob: <string>
            A Unix glob-style pattern.
        mode: <string>
            [default 'create'] Matching mode: create or modify
        read: <string>
            If specified, the content of the file is included, assuming the named encoding.

bootstrap: <boolean>
    [default: false] If specified as true, emit events for all matching files
    at startup time, otherwise only emit changes after startup.

aggregate: <boolean>
    [default: false] If specified as true, the message emitted will contain
    all the matching file[name]s as a list, rather than the string file[name].

poll-time: <seconds>
    [default: 600] The time between polling for new or changed files.
"""
    }

    static main(args) {
        def fac = new MarioFileWatch()
        println fac.usage()
    }
}
