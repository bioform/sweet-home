package sweethome

import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.context.MessageSource

class ScriptController extends ApplicationController {

    MessageSource messageSource
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

        if( script.save() ) {
            render script as JSON
        } else {
            response.status = 400
            render script.errors.allErrors.collect {
                [
                    field: it.field,
                    rejectedValue: it.rejectedValue,
                    code: (it.codes ? it.codes[it.codes.length-1] : '').tokenize('.').last(),
                    message: message(error:it ,encodeAs:'HTML')
                ]
            } as JSON
        }

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
}
