package sweethome

import grails.converters.JSON
import grails.transaction.Transactional

class ScriptController {
    def index() {
        def json = Script.list() as JSON
        render json
    }

    @Transactional
    def save() {
        def script = params.id ? Script.get(params.id) : new Script()
        location.name = request.JSON.name
        location.code = request.JSON.code
        location.cronExpression = request.JSON.cronExpression
        location.active = request.JSON.active
        location.save()

        render location as JSON
    }
}
