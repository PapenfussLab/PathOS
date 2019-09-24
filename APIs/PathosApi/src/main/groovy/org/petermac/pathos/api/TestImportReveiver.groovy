package org.petermac.pathos.api

import org.petermac.yaml.*

class TestImportReceiver implements ImportReceiver {
    private Map checker

    TestImportReceiver(Map config) {
        checker = [:]
        config.each {dom, acts ->
            checker[dom] = [:]
            acts.each {act, con ->
                checker[dom][act] = new YamlChecker(con)
            }
        }
    }

    void receive(String domain, String action, Object data) {
        if (!checker.containsKey(domain)) {
            throw new UnsupportedDomain(domain)
        }
        if (!checker[domain].containsKey(action)) {
            throw new UnsupportedAction("${domain}/${action}")
        }
        checker[domain][action].check(data)
    }
}
