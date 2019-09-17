package org.petermac.yaml

class YamlConfigSpockSpec extends spock.lang.Specification {
    def "parse YAML integer"() {
        when:
            def v = YamlConfig.parse("1")
        
        then:
            v instanceof Integer
    }

    def "parse YAML float"() {
        when:
            def v = YamlConfig.parse("1.2")
        
        then:
            v instanceof Number
    }

    def "parse YAML string 1"() {
        when:
            def v = YamlConfig.parse("abc")
        
        then:
            v == 'abc'
    }

    def "parse YAML string 2"() {
        when:
            def v = YamlConfig.parse("'abc'")
        
        then:
            v == 'abc'
    }

    def "parse YAML string 3"() {
        when:
            def v = YamlConfig.parse('"abc"')
        
        then:
            v == 'abc'
    }

    def "parse YAML list"() {
        when:
            def v = YamlConfig.parse('[a, b, c]')
        
        then:
            v instanceof List
            v == ['a', 'b', 'c']
    }

    def "parse YAML map"() {
        when:
            def v = YamlConfig.parse('{a: 1, b: 2, c: 3}')
        
        then:
            v instanceof Map
            v == [a: 1, b: 2, c: 3]
    }

    def "check params 1"() {
        when:
            Map config = [:]
            Set mustHave = []
            Set mayHave = []
            YamlConfig.checkParams(config, mustHave, mayHave)

        then:
            true
    }

    def "check params 2"() {
        when:
            Map config = ['a':1]
            Set mustHave = ['a']
            Set mayHave = []
            YamlConfig.checkParams(config, mustHave, mayHave)

        then:
            true
    }

    def "check params 3"() {
        when:
            Map config = ['a':1]
            Set mustHave = []
            Set mayHave = ['a']
            YamlConfig.checkParams(config, mustHave, mayHave)

        then:
            true
    }

    def "check params 4"() {
        when:
            Map config = ['a':1]
            Set mustHave = ['b']
            Set mayHave = []
            YamlConfig.checkParams(config, mustHave, mayHave)

        then:
            thrown(UnknownParameterName)
    }

    def "check params 5"() {
        when:
            Map config = [:]
            Set mustHave = ['b']
            Set mayHave = []
            YamlConfig.checkParams(config, mustHave, mayHave)

        then:
            thrown(RequiredParameterMissing)
    }

    def "check expanand 1"() {
        when:
            List args = ['a', 'b', 'c']
            String s = '0{1}2'
        then:
            YamlConfig.expand(s, args) == '0b2'
    }

    def "check expanand 2"() {
        when:
            List args = ['a', 'b', 'c']
            String s = '{0}{1}{2}'
        then:
            YamlConfig.expand(s, args) == 'abc'
    }

    def "check expanand 3"() {
        when:
            List args = ['a', 'b', 'c']
            String s = '{0}{1}{3}'
            String r = YamlConfig.expand(s, args)

        then:
            thrown(InvalidParameterValue)
    }
}  
