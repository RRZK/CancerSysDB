package de.cancersysdb

class Role {

    final static String USER = "ROLE_USER"
    final static String MANAGER = "ROLE_MANAGER"
    final static String ADMIN = "ROLE_ADMIN"

    String authority

    static mapping = {
        cache true
    }

    static constraints = {
        authority blank: false, unique: true
    }
}
