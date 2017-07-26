grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

grails.tomcat.nio = true
grails.tomcat.scan.enabled = true


grails.project.war.file = "target/${appName}.war"
/*grails.war.copyToWebApp = { args ->
    fileset(dir: "data")
}*/
grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
/*    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],*/
    // configure settings for the run-app JVM
    run: [maxMemory: 2768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the run-war JVM
    war: [maxMemory: 2768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 2768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    def gebVersion = "0.13.1"
    def seleniumVersion = "2.53.1"

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        mavenRepo "http://repo.grails.org/grails/core"
        mavenRepo "http://oss.sonatype.org/content/repositories/snapshots"
        mavenRepo "http://dl.bintray.com/alkemist/maven/"
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        // runtime 'org.postgresql:postgresql:9.3-1101-jdbc41'
        test "org.grails:grails-datastore-test-support:1.0-grails-2.4"
        compile 'commons-beanutils:commons-beanutils:1.8.3'
        //
        compile 'mysql:mysql-connector-java:5.1.29'

        test "org.gebish:geb-spock:$gebVersion"
//        test("org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion")
        test "org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion"
        test "org.seleniumhq.selenium:selenium-support:$seleniumVersion"
        compile 'org.apache.commons:commons-csv:1.2'
        compile 'org.gephi:gephi-toolkit:0.9.1'


    }

    plugins {
        // plugins for the build system only
        build ":tomcat:7.0.55"
        compile ":runtime-datasources:0.2"
        // plugins for the compile step
        compile ":grails-melody:1.54.0"
        compile ":scaffolding:2.1.2"
        compile ':cache:1.1.8'
        compile ':asset-pipeline:2.3.9'
        compile ":spring-security-core:2.0-RC4"
        compile ":spring-security-ui:1.0-RC2"
        compile ":spring-security-rest:1.4.0"
        compile ":csv:0.3.1"
        compile ':platform-core:1.0.0'
        compile ":ckeditor:4.5.4.1"

/*
        compile ":class-domain-uml:0.1.5"
*/
        compile ':rest-client-builder:2.1.1'
        compile ":spring-websocket:1.3.0"
        compile ":export:1.6"
        compile ":geb:$gebVersion"

        // plugins needed at runtime but not for compilation
        runtime ':hibernate4:4.3.8.1' // or ":hibernate:3.6.10.17"
        runtime ":database-migration:1.4.0"
        runtime ":jquery:1.11.1"
/*
        compile ":remote-control:2.0"
*/
        compile ":jquery-ui:1.10.4"

        // Uncomment these to enable additional asset-pipeline capabilities
        //compile ":sass-asset-pipeline:1.9.0"
        //compile ":less-asset-pipeline:1.10.0"
        //compile ":coffee-asset-pipeline:1.8.0"
        //compile ":handlebars-asset-pipeline:1.3.0.3"

        //Testing
    }
}
