package sweethome

import grails.transaction.Transactional
import sweethome.scripting.JsHome
import sweethome.scripting.JsLocation
import sweethome.scripting.JsLog

import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

@Transactional
class ScriptingService {

    def brokerMessagingTemplate

    def exec(Script script) {
        ScriptEngineManager engineManager = new ScriptEngineManager()
        ScriptEngine engine = engineManager.getEngineByName("nashorn")
        Bindings binding = engine.getBindings(ScriptContext.ENGINE_SCOPE)

        JsLog jsLog = new JsLog(brokerMessagingTemplate, script.id)

        JsHome jsHome = new JsHome()
        Location.list().each {
            jsHome.put it.name, new JsLocation(it, jsHome)
        }
        binding.put "home", jsHome
        binding.put "log", jsLog
        binding.put "console", jsLog

        try {
            engine.eval(script.code)
        } catch (ScriptException ex) {
            jsLog.log("error", null, ex.message)
            throw ex
        } catch(Exception e){
            jsLog.log("error", null, ex)
            throw e
        } finally {
            jsHome.close()
        }

    }
}
