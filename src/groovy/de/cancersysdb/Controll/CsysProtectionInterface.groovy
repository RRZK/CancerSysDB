package de.cancersysdb.Controll

import de.cancersysdb.User

/**
 * Created by rkrempel on 14-2-17.
 */
interface CsysProtectionInterface {
    //Is this Sample Annonymous
    Boolean annon
    //If False this Dataset is only Visible to The Owner
    Boolean shared
    //The Owner
    User owner
}
