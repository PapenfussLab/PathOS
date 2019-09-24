package org.petermac.pathos.api

import org.yaml.snakeyaml.Yaml

class PathosImportTester {
    static main(args) {
        def yaml = new Yaml()

        def valid = yaml.load(new FileInputStream(new File(args[0])))
        ImportReceiver checker = new TestImportReceiver(valid)

        def conf = yaml.load(new FileInputStream(new File(args[1])))
        def imp = ApiUtils.loadImporter(conf)

        imp.importData(checker)
    }
}

