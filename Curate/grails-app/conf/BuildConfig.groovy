grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

grails.project.dependency.resolution =
        {
            // inherit Grails' default dependencies
            inherits("global")
                    {
                        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
                        // excludes 'ehcache'
                    }
            log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
            checksums true // Whether to verify checksums on resolve
            legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

            def seleniumVersion = "2.52.0"

            repositories
                    {
                        inherits true // Whether to inherit repository definitions from plugins


                        grailsPlugins()
                        grailsHome()
                        grailsCentral()

                        mavenLocal()
                        mavenCentral()

                        mavenRepo "http://repo.grails.org/grails/core"
                        mavenRepo 'http://repo.spring.io/milestone' //for spring security
                        mavenRepo "http://download.java.net/maven/2/"
                        mavenRepo "http://repo.grails.org/grails/repo/" //needed for Spock

                        grailsRepo "https://grails.org/plugins/"

                        mavenRepo "http://repo.grails.org/grails/core"
                        mavenRepo "http://repo.grails.org/grails/plugins"

                        // Nexus repository
                        mavenRepo "http://localhost:8081/repository/maven-releases"
                    }

            dependencies
                    {
                        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
                        //
                        test "org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion"
                        test "org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion"
                        test "org.seleniumhq.selenium:selenium-support:$seleniumVersion"

                        // dependencies required for easygrid
                        //
                        compile('net.sf.opencsv:opencsv:2.3')
                        compile('com.google.visualization:visualization-datasource:1.1.1') {
                            exclude(group: 'commons-logging', name: 'commons-logging')
                            exclude(group: 'commons-lang', name: 'commons-lang')
                        }

                        compile 'com.esotericsoftware.kryo:kryo:2.24.0'

                        runtime 'mysql:mysql-connector-java:5.1.25'

                        compile( group: 'org.petermac.pathos' , name: 'PathosCore',  version: '1.5.2', classifier: 'all' ) {
                            changing = true
                        }

                        compile 'org.apache.httpcomponents:httpcore:4.3'
                        compile 'org.apache.httpcomponents:httpclient:4.3'
                    }

            plugins
                    {

                        //  Standard plugins for Grails 2.3.7
                        //  =================================
                        // plugins for the build system only
                        //
                        build   ":tomcat:7.0.52.1"

                        // plugins for the compile step
                        //
                        compile ":scaffolding:2.0.2"
                        compile ':cache:1.1.1'

                        // plugins needed at runtime but not for compilation
                        //
                        runtime ":hibernate:3.6.10.9" // or ":hibernate4:4.3.4"
                        runtime ":database-migration:1.3.8"
                        runtime ":jquery:1.11.0.2"
                        compile ":jquery:1.11.0.2"

                        runtime ":resources:1.2.7"

                        //  Additional application plugins
                        //  ==============================

                        //runtime ":database-migration:1.3.2"
                        runtime ":standalone:1.2.3"
                        runtime ":searchable:0.6.8"
                        runtime ":filterpane:2.4.2"
                        compile ":tooltip:0.8"
                        compile ":csv:0.3.1"

                        //  Easygrid: 1.6.6.1 is our custom local copy
                        //
                        compile ":easygrid:1.6.6.1"
                        compile ":google-visualization:0.7"

                        compile ":markdown:1.1.1"
                        compile ":export:1.6"

                        compile ":google-chart:0.5.2"

                        //  dependencies needed for spring security UI:
                        //
                        compile ":mail:1.0.7"
                        compile ":jquery-ui:1.10.4"
                        compile ":famfamfam:1.0.1"

                        //  spring security
                        //
                        compile ":spring-security-core:2.0-RC3"
                        compile ":spring-security-ui:1.0-RC2"

                        compile "org.grails.plugins:spring-security-ldap:2.0-RC2"

                        //  mail testing
                        //
                        compile ":greenmail:1.3.4"

                    }
        }
