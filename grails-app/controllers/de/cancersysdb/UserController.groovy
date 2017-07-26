package de.cancersysdb

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.authentication.dao.NullSaltSource

class UserController extends grails.plugin.springsecurity.ui.UserController {
    def springSecurityService

    def create() {
        def user = lookupUserClass().newInstance(params)
        [user: user, authorityList: sortedRoles()]
    }
    @Override
    def save() {
        def user = lookupUserClass().newInstance(params)
        def ud = new UserDetail()

        if (params.password) {
            String salt = saltSource instanceof NullSaltSource ? null : params.username
            user.password = springSecurityUiService.encodePassword(params.password, salt)
        }
        ud.setFirstName(params.userDetail.firstName)
        ud.setLastName(params.userDetail.lastName)
        ud.setUser(user)
        user.setDetails(ud)
        if (!user.save(flush: true)) {
            print user.errors
            render view: 'create', model: [user: user, authorityList: sortedRoles()]
            return
        }

        addRoles(user)
        flash.message = "${message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), user.id])}"
        redirect action: 'edit', id: user.id
    }

    def update() {
        String passwordFieldName = SpringSecurityUtils.securityConfig.userLookup.passwordPropertyName

        def user = findById()
        if (!user) return
        if (!versionCheck('user.label', 'User', user, [user: user])) {
            return
        }

        def oldPassword = user."$passwordFieldName"
        user.properties = params
        if (params.password && !params.password.equals(oldPassword)) {
            String salt = saltSource instanceof NullSaltSource ? null : params.username
            user."$passwordFieldName" = springSecurityUiService.encodePassword(params.password, salt)
        }
        user.details.setFirstName(params.userDetail.firstName)
        user.details.setLastName(params.userDetail.lastName)
        user.details.save()
        if (!user.save(flush: true)) {
            render view: 'edit', model: buildUserModel(user)
            return
        }

        String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName

        lookupUserRoleClass().removeAll user
        addRoles user
        userCache.removeUserFromCache user[usernameFieldName]
        flash.message = "${message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])}"
        redirect action: 'edit', id: user.id
    }


    def passwordExpired() {
        [username: session['SPRING_SECURITY_LAST_USERNAME']]
    }
    def updatePassword() {
        String username = session['SPRING_SECURITY_LAST_USERNAME']
        if (!username) {
            flash.message = 'Sorry, an error has occurred'
            redirect controller: 'login', action: 'auth'
            return
        }
        def passwordEncoder = springSecurityService.passwordEncoder
        String password     = params.password
        String newPassword = params.password_new
        String newPassword2 = params.password_new_2
        if (!password || !newPassword || !newPassword2 ||
                newPassword != newPassword2) {
            flash.message =
                    'Please enter your current password and a valid new password'
            render view: 'passwordExpired',
                    model: [username: session['SPRING_SECURITY_LAST_USERNAME']]
            return
        }

        User user = User.findByUsername(username)
        if (!passwordEncoder.isPasswordValid(user.password,
                password, null /*salt*/)) {
            flash.message = 'Current password is incorrect'
            render view: 'passwordExpired',
                    model: [username: session['SPRING_SECURITY_LAST_USERNAME']]
            return
        }

        if (passwordEncoder.isPasswordValid(user.password, newPassword,
                null /*salt*/)) {
            flash.message =
                    'Please choose a different password from your current one'
            render view: 'passwordExpired',
                    model: [username: session['SPRING_SECURITY_LAST_USERNAME']]
            return
        }

        user.password = newPassword
        user.passwordExpired = false
        user.save() // if you have password constraints check them here

        redirect controller: 'login', action: 'auth'
    }

}
