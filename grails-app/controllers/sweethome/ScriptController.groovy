package sweethome

import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.json.JSONObject

class ScriptController {

    def scriptingService

    def index() {
        def json
        if(params.id != null){
            json = Script.get(params.id) as JSON
        } else {
            json = Script.list() as JSON
        }

        render json
    }

    def show(){
        def json = Script.get(params.id) as JSON
        render json
    }

    @Transactional
    def save() {
        def script = params.id ? Script.get(params.id) : new Script()

        assignAttributes(script, ["name", "code", "cronExpression"])

        if(request.JSON.active != null)
            script.active = !!request.JSON.active

        script.save(flush: true)

        render script as JSON
    }

    def delete(){
        def script = Script.get(params.id)
        script.delete(flush: true)
        render script as JSON
    }

    def exec(){
        def script = Script.get(params.scriptId)

        def result = [:]

        try {
            scriptingService.exec(script)
        } catch (Exception e){
            result['error'] = "$e"
            response.status = 422
        }

        render result as JSON
    }

    private void assignAttributes(script, params){
        for(String param:params){
            if(request.JSON.containsKey(param)){
                def val = request.JSON."$param"
                script."$param" = JSONObject.NULL.equals(val) ? null:val
            }
        }
    }
}
