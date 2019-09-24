package org.petermac.pathos.api

import org.petermac.yaml.YamlConfig
import org.yaml.snakeyaml.Yaml

class PathosExportTester {
    static main(args) {
        def yaml = new Yaml()
        def conf = yaml.load(new FileInputStream(new File(args[0])))
        def exp = ApiUtils.loadExporter(conf)

        try {
            Set testMustHave = []

            testMustHave << "domain"
            testMustHave << "action"
            testMustHave << "data"

            Set testMayHave = []
            testMayHave << "name"
            testMayHave << "fails"

            def n = 0
            def succ = 0
            def errs = 0
            for (Map tst : yaml.loadAll(new FileInputStream(new File(args[1])))) {
                YamlConfig.checkParams(tst, testMustHave, testMayHave)
                n += 1
                def nm = tst['name'] ?: "test-${n}"
                def shouldFail = tst['fails'] = tst['fails'] ? tst['fails'].toBoolean() : false
                try {
                    exp.receive(tst['domain'], tst['action'], tst['data'])
                    if (shouldFail) {
                        println "failing-pass: ${nm}"
                        errs += 1
                    } else {
                        succ += 1
                    }
                } catch (Exception e) {
                    if (shouldFail) {
                        succ += 1
                    } else {
                        println "failing-fail: ${nm}"
                        errs += 1
                    }
                }
            }
            println "tests run:\t${succ+errs}"
            println "tests passed:\t${succ}"
            println "tests failed:\t${errs}"
        } finally {
            exp.close()
        }
    }
}
