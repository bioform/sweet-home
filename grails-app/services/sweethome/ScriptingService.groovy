package sweethome

import grails.transaction.Transactional
import sweethome.scripting.JsHome
import sweethome.scripting.JsLocation
import sweethome.scripting.JsLog

import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@Transactional
class ScriptingService {

    def brokerMessagingTemplate

    def exec(Script script) {
        ScriptEngineManager engineManager = new ScriptEngineManager()
        ScriptEngine engine = engineManager.getEngineByName("nashorn")
        Bindings binding = engine.getBindings(ScriptContext.ENGINE_SCOPE)

        JsHome jsHome = new JsHome()
        Location.list().each {
            jsHome.put it.name, new JsLocation(it)
        }
        binding.put "home", jsHome
        binding.put "log", new JsLog(brokerMessagingTemplate, script.id)

        engine.eval(script.code)
    }
}
