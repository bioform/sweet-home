package sweethome

class HomeController {

    def index() { 
        def config = [:]

        String configJson = ( config as grails.converters.JSON ).toString(true)

        return [
                config: configJson
        ]
    }
}
