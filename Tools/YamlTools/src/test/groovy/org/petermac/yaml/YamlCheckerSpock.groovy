package org.petermac.yaml

class YamlCheckerSpockSpec extends spock.lang.Specification {
    def "number type succeeds"() {
        def cx = new YamlChecker("number")
        when:
            [1, 1.2, -3].each {
                cx.check(it)
            }
        then:
            true
    }

    def "number type fails"() {
        def cx = new YamlChecker("number")
        when:
            cx.check('foo')
        then:
            thrown(TypeError)
    }

    def "string type succeeds"() {
        def cx = new YamlChecker("string")
        when:
            cx.check('foo')
        then:
            true
    }

    def "string type fails"() {
        def cx = new YamlChecker("string")
        when:
            cx.check(1.2)
        then:
            thrown(TypeError)
    }

    def "boolean type succeeds"() {
        def cx = new YamlChecker("boolean")
        when:
            cx.check(true)
            cx.check(false)
            cx.check(1 == 2)
        then:
            true
    }

    def "boolean type fails"() {
        def cx = new YamlChecker("boolean")
        when:
            cx.check(1.2)
        then:
            thrown(TypeError)
    }

    def "any type succeeds"() {
        def cx = new YamlChecker("any")
        when:
            cx.check(true)
            cx.check(1)
            cx.check('qux')
            cx.check(['foo', 'bar', 'baz'])
            cx.check(['foo':1, 'bar':2, 'baz':3])
        then:
            true
    }

    def "list type succeeds"() {
        def cx = new YamlChecker("{list: string}")
        when:
            cx.check([])
            cx.check(['foo'])
            cx.check(['foo', 'bar'])
            cx.check(['foo', 'bar', 'baz'])
        then:
            true
    }

    def "list type fails"() {
        def cx = new YamlChecker("{list: string}")
        when:
            cx.check(['foo', 2, 'baz'])
        then:
            thrown(TypeError)
    }

    def "tuple type succeeds"() {
        def cx = new YamlChecker("{tuple: [string, number, string]}")
        when:
            cx.check(['foo', 2, 'baz'])
        then:
            true
    }

    def "tuple type fails"() {
        def cx = new YamlChecker("{tuple: [string, number, string]}")
        when:
            cx.check(['foo', 'bar', 'baz'])
        then:
            thrown(TypeError)
    }

    def "oneof type succeeds"() {
        def cx = new YamlChecker("{oneof: {foo: string, bar: number, baz: boolean}}")
        when:
            cx.check(['foo':'qux'])
            cx.check(['bar':1.2])
            cx.check(['baz':true])
        then:
            true
    }

    def "oneof type fails 1"() {
        def cx = new YamlChecker("{oneof: {foo: string, bar: number, baz: boolean}}")
        when:
            cx.check([:])
        then:
            thrown(TypeError)
    }

    def "oneof type fails 2"() {
        def cx = new YamlChecker("{oneof: {foo: string, bar: number, baz: boolean}}")
        when:
            cx.check(['qux': 'wombat'])
        then:
            thrown(TypeError)
    }

    def "oneof type fails 3"() {
        def cx = new YamlChecker("{oneof: {foo: string, bar: number, baz: boolean}}")
        when:
            cx.check(['foo': 'qux', 'bar':1.2])
        then:
            thrown(TypeError)
    }

    def "oneof type fails 4"() {
        def cx = new YamlChecker("{oneof: {foo: string, bar: number, baz: boolean}}")
        when:
            cx.check(['foo': 1])
        then:
            thrown(TypeError)
    }

    def "map type succeeds"() {
        def cx = new YamlChecker("{map: {foo: string, bar: number, 'baz?': boolean}}")
        when:
            cx.check(['foo':'qux', 'bar':1.2, 'baz':true])
            cx.check(['foo':'qux', 'bar':1.2])
        then:
            true
    }

    def "map type fails 1"() {
        def cx = new YamlChecker("{map: {foo: string, bar: number, 'baz?': boolean}}")
        when:
            cx.check([:])
        then:
            thrown(TypeError)
    }

    def "map type fails 2"() {
        def cx = new YamlChecker("{map: {foo: string, bar: number, 'baz?': boolean}}")
        when:
            cx.check(['bar':1.2])
        then:
            thrown(TypeError)
    }

    def "map type fails 3"() {
        def cx = new YamlChecker("{map: {foo: string, bar: number, 'baz?': boolean}}")
        when:
            cx.check(['bar':1.2, 'baz':true])
        then:
            thrown(TypeError)
    }

    def "map type fails 4"() {
        def cx = new YamlChecker("{map: {foo: string, bar: number, 'baz?': boolean}}")
        when:
            cx.check(['foo':'qux', 'bar':'wombat'])
        then:
            thrown(TypeError)
    }

    def "map type fails 5"() {
        def cx = new YamlChecker("{map: {foo: string, bar: number, 'baz?': boolean}}")
        when:
            cx.check(['foo':'qux', 'bar':1.2, 'wombat':'tribble'])
        then:
            thrown(TypeError)
    }

    def "map* type succeeds"() {
        def cx = new YamlChecker("{map: {foo: string, bar: number, 'baz?': boolean, '*': string}}")
        when:
            cx.check(['foo':'qux', 'bar':1.2, 'baz':true])
            cx.check(['foo':'qux', 'bar':1.2])
            cx.check(['foo':'qux', 'bar':1.2, 'wombat':'tribble'])
        then:
            true
    }

    def "map* type fails 1"() {
        def cx = new YamlChecker("{map: {foo: string, bar: number, 'baz?': boolean, '*': string}}")
        when:
            cx.check(['foo':'qux', 'bar':1.2, 'wombat':1.2])
        then:
            thrown(TypeError)
    }

}  
