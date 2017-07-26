package de.cancersysdb

class ErrorController {


    def notFound() {
        def msg = request.getAttribute('javax.servlet.error.message')

        if( !msg )
            msg ="resource not found"

        render(view:"error",model:[msg:msg, code:404])

    }

    def accessDenied(){
        def msg = request.getAttribute('javax.servlet.error.message')
        if(!msg )
            msg ="permission denied"
        render(view:"error",model:[msg:msg, code:403])

    }

    def crash(Exception e){
        def msg = request.getAttribute('javax.servlet.error.message')

        if(!msg )
            msg ="Fatal Error"

        render(view:"error",model:[msg:"Fatal Error", code:500, exception:e])

    }
    def deniedPleaseLogIn(){
        def msg = request.getAttribute('javax.servlet.error.message')

        if(!msg )
            msg ="permission denied, please login"

        render(view:"error",model:[msg:msg, code:401])

    }



}
