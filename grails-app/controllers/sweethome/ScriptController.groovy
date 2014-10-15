package sweethome

import grails.converters.JSON
import grails.transaction.Transactional

class ScriptController {
    def index() {
        def json
        if(params.id != null){
            json = Script.get(params.id) as JSON
        } else {
            json = Script.list() as JSON
        }

        render json
    }

    @Transactional
    def save() {
        def script = params.id ? Script.get(params.id) : new Script()
        script.name = request.JSON.name
        script.code = request.JSON.code
        script.cronExpression = request.JSON.cronExpression

        if(request.JSON.active != null)
            script.active = !!request.JSON.active

        script.save()

        render script as JSON
    }
}
