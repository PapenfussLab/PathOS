package org.petermac.yaml

class YamlCodecSpockSpec extends spock.lang.Specification {
    def "leading zero is int"() {
        def y = new YamlCodec()
        def o = null
        when:
            o = y.load('01234567')
        then:
            o instanceof Integer
    }
    def "leading zero is not int"() {
        def y = new YamlCodec()
        def o = null
        when:
            o = y.load('08054322')
        then:
            o instanceof String
    }
}
