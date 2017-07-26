package de.cancersysdb

import grails.converters.JSON

/**
 *
 */
class User {

    transient springSecurityService

    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    UserDetail details
    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true
        password blank: false
    }

    static mapping = {
        password column: '`password`'
    }

    static {

        JSON.registerObjectMarshaller(User) {

            def returnArray = [:]
            returnArray['id'] = it.id
            return returnArray

        }
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role }
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }
    static def marshallers = ["exchange": {
        User user ->
            return [
                    id   : user.id,
                    class: user.getClass().name,

            ]
    }
    ]
}
