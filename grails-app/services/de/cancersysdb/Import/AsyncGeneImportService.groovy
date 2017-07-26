package de.cancersysdb.Import

import de.cancersysdb.Import.GeneImportService
import grails.async.DelegateAsync
import grails.transaction.Transactional

/**
 * Asynchron Version of the GeneImportService
 */
@Transactional
class AsyncGeneImportService {

    @DelegateAsync GeneImportService geneImportService

}
