package de.cancersysdb.ImportTools

import de.cancersysdb.Dataset
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class ImportProtocolSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test InternalCounter And Communication"() {
        setup:

        ImportProtocol ip = new ImportProtocol()
        ip.Autoreport =true
        ImportProtocol temp = new ImportProtocol()
        Dataset dstemp = new Dataset()

        when:

        ip.ImportStart()
        for(int i = 0; i < 100;i++)
            ip.ImportedSuccessful(temp)

        for(int i = 0; i < 100;i++)
            ip.ImportedFailed(temp)
        ip.ImportEnd()

        then:
        assert ip.getSuccessfulImportStats().get(temp.class.simpleName) == 100
        assert ip.getFailImportStats().get(temp.class.simpleName) == 100
        assert ip.getSuccessfulImportStats().get(temp.class.simpleName) == ip.successfulCount
        assert ip.getFailImportStats().get(temp.class.simpleName) == ip.failedCount
    }
    void "test InternalCounter And Communication with 2 Classes"() {
        setup:
        ImportProtocol ip = new ImportProtocol()
        ip.Autoreport =true
        ImportProtocol temp = new ImportProtocol()
        Dataset dstemp = new Dataset()
        when:
        ip.ImportStart()
        for(int i = 0; i < 100;i++)
            ip.ImportedSuccessful(temp)

        for(int i = 0; i < 100;i++)
            ip.ImportedFailed(temp)
        for(int i = 0; i < 100;i++)
            ip.ImportedSuccessful(dstemp)

        for(int i = 0; i < 100;i++)
            ip.ImportedFailed(dstemp)

        ip.ImportEnd()
        then:
        assert ip.getSuccessfulImportStats().get(temp.class.simpleName) == 100
        assert ip.getFailImportStats().get(temp.class.simpleName) == 100
        assert ip.getSuccessfulImportStats().get(dstemp.class.simpleName) == 100
        assert ip.getFailImportStats().get(dstemp.class.simpleName) == 100
        assert ip.getSuccessfulImportStats().get(temp.class.simpleName) != ip.successfulCount
        assert ip.getFailImportStats().get(temp.class.simpleName) != ip.failedCount
        assert ip.getSuccessfulImportStats().get(dstemp.class.simpleName) != ip.successfulCount
        assert ip.getFailImportStats().get(dstemp.class.simpleName) != ip.failedCount
        assert ip.getSuccessfulImportStats().get(dstemp.class.simpleName) +ip.getSuccessfulImportStats().get(temp.class.simpleName)== ip.successfulCount
        assert ip.getFailImportStats().get(dstemp.class.simpleName) +ip.getFailImportStats().get(temp.class.simpleName) == ip.failedCount

    }

    void "test Messages"() {
        setup:
        ImportProtocol ip = new ImportProtocol()


        when:
        ip.ImportStart()
        for(int i = 0; i < 100;i++)
            ip.Message("hey" ,false)


        ip.ImportEnd()
        then:

        assert ip.messages.size()>= 100

    }

    void "test Messages and output"() {
        setup:
        ImportProtocol ip = new ImportProtocol()


        when:
        ip.ImportStart()
        for(int i = 0; i < 100;i++)
            ip.Message("hey"+i ,false)


        ip.ImportEnd()
        then:
        for(int i =0; ip.messages.size()>i;i++ ){
            if(i==0 || ip.messages.size()==i+1){
                continue
            }
            assert ip.messages.get(i)=="hey"+(i-1)

        }
        assert ip.messages.size()>= 100

    }

    void "Simple Time Test"() {
        setup:
        ImportProtocol ip = new ImportProtocol()

        int a =0
        when:
        ip.ImportStart()

        for(int i=0;i< 10000;i++)
            a=a+1

        ip.ImportEnd()
        then:
        assert a != 0
        assert ip.Starttime <= ip.Endtime

    }

    void "test AutoreportMessages"() {
        setup:
        ImportProtocol ip = new ImportProtocol()
        ip.Autoreport =true
        ip.AutoreportAferEveryNTHDataset=10
        ImportProtocol temp = new ImportProtocol()

        when:
        ip.ImportStart()
        for(int i = 0; i < 100;i++)
            ip.ImportedSuccessful(temp)

        for(int i = 0; i < 100;i++)
            ip.ImportedFailed(temp)
        ip.ImportEnd()
        then:

        assert ip.messages.size()> 20

    }


}
