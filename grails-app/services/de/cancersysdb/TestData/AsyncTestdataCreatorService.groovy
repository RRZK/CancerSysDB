package de.cancersysdb.TestData

import grails.async.DelegateAsync
import grails.transaction.Transactional

@Transactional
class AsyncTestdataCreatorService {

    @DelegateAsync TestdataCreatorService testdataCreatorService

}
