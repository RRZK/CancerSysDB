package de.cancersysdb.data

import de.cancersysdb.Dataset

/**
 * Created by rkrempel on 01.09.16.
 */
interface DataEntry {

    Dataset dataset

    static belongsTo= [dataset:Dataset]

}
