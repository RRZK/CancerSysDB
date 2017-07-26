package de.cancersys.pages

import geb.*

class loginPage extends Page {
    static url = "login"

    static content = {
        usernameField{$("input[id=username]") }
        passwordField{$("input[id=password]") }
        loginForm{$("form[id=loginForm]")}
        submitButton{$("input[id=submit]",type: "submit")}

    }

    static at = { submitButton && usernameField && submitButton  && loginForm  }
    void dologin(String Username,String Password) {
        usernameField << Username
        passwordField << Password
        submitButton.click()
    }
    @Override
    def <T> T waitFor(String s, Closure<T> closure) {
        return null
    }

    @Override
    def <T> T waitFor(Map map, String s, Closure<T> closure) {
        return null
    }

    @Override
    def <T> T waitFor(Closure<T> closure) {
        return null
    }

    @Override
    def <T> T waitFor(Map map, Closure<T> closure) {
        return null
    }

    @Override
    def <T> T waitFor(Double aDouble, Closure<T> closure) {
        return null
    }

    @Override
    def <T> T waitFor(Map map, Double aDouble, Closure<T> closure) {
        return null
    }

    @Override
    def <T> T waitFor(Double aDouble, Double aDouble1, Closure<T> closure) {
        return null
    }

    @Override
    def <T> T waitFor(Map map, Double aDouble, Double aDouble1, Closure<T> closure) {
        return null
    }

}