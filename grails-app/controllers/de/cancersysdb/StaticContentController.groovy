package de.cancersysdb

class StaticContentController {

    def about() {
        render view:"/about"
    }
    def contact() {
        render view:"/contact"
    }
    def disclaimer() {
        render view:"/disclaimer"
    }
    def workflow() {
        render view:"/tobeImplemented"
    }
    def workflowOwn() {
        render view:"/tobeImplemented"
    }
    def dataOverview() {
        render view:"/dataOverview"
    }
    def documentation(String docstring) {


        render view:"/documentation", model:[docpart:docstring]
    }
    def showcase(String name) {

        if(!name&& params.name)
            name = params.name
        if(!name)
            response.sendError( 404, "Showcase not found")


            render  view:"/showcase/$name"
    }

}
