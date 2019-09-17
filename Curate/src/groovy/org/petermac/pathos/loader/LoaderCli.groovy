package org.petermac.pathos.loader

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import groovy.util.logging.Log4j

import org.petermac.yaml.YamlCodec
import org.petermac.yaml.YamlConfig
import org.petermac.pathos.api.ImportReceiver
import org.petermac.pathos.api.ImporterFactory
import org.petermac.pathos.api.Importer
import org.petermac.pathos.amqp.AMQPImporter
import java.util.zip.GZIPInputStream

@Log4j
class LoaderCli {
    static void main(args) {
        BasicConfigurator.configure()
        Logger.getRootLogger().setLevel(Level.INFO)

        //  Collect and parse command line args
        //
        def cli = new CliBuilder(
                usage:  'LoaderCli [options]',
                header: '\nAvailable options (use -h for help):\n',
                footer: '\nMLLP/AMQP protocol translator\n'
        )

        //  Options to command
        //
        cli.with {
            a(longOpt: 'amqp-config', args:1, 'filename for an AMQP configuration')
            h(longOpt: 'help',               'This help message')
            l(longOpt: 'log-config', args:1, 'log4j properties file')
            L(longOpt: 'log-level',  args:1, 'set the logging level')
            r(longOpt: 'database',   args:1, 'database specifier', required:true)
            y(longOpt: 'yaml-file',  args:1, 'read messages from a YAML format file')
            v(longOpt: 'verbose',            'generate verbose output')
        }

        def opt = cli.parse(args)
        if (!opt) {
            return
        }

        if (opt.help) {
            cli.usage()
            return
        }

        if (opt.l) {
            PropertyConfigurator.configure(opt.l)
        }

        if (opt.L) {
            Logger.getRootLogger().setLevel(Level.toLevel(opt.L))
        }

        ImportReceiver receiver = new PathosImportReceiver(opt.r, opt.verbose)

        if (opt.y) {
            File file = new File(opt.y)
            if(!file.exists()) {
                println "Specified yaml file does not exist. Exiting."
                System.exit(1)
            }

            InputStream inp = new FileInputStream(file)
            if (opt.y.endsWith('.gz')) {
                inp = new GZIPInputStream(inp)
            }

            def yaml = new YamlCodec()
            for (Map m : yaml.loadAll(inp)) {
                receiver.receive(m['domain'], m['action'], m['data'])
            }
            return
        }

        if (opt.a) {
            ImporterFactory fac = new AMQPImporter()
            Map cfg = YamlConfig.load(opt.a)
            Importer imp = fac.create(cfg)
            imp.importData(receiver)
        }
    }
}
