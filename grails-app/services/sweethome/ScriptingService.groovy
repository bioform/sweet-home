package sweethome

import grails.compiler.GrailsCompileStatic
import grails.plugins.quartz.TriggerDescriptor
import grails.plugins.quartz.TriggerUtils
import grails.transaction.Transactional
import org.quartz.CronTrigger
import org.quartz.SimpleTrigger
import org.quartz.Trigger
import org.quartz.impl.triggers.AbstractTrigger
import sweethome.scripting.JsHome
import sweethome.scripting.JsLocation
import sweethome.scripting.JsLog
import sweethome.scripting.JsResources
import sweethome.scripting.JsScript

import javax.annotation.PostConstruct
import javax.script.Bindings
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import javax.script.CompiledScript;
import javax.script.Compilable;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

@Transactional
class ScriptingService {

    private ScriptEngine engine

    def quartzScheduler
    def jobManagerService
    def brokerMessagingTemplate

    @PostConstruct
    def init() {
        //ScriptEngineManager engineManager = new ScriptEngineManager()
        //ScriptEngine engine = engineManager.getEngineByName("nashorn")

        NashornScriptEngineFactory factory = new NashornScriptEngineFactory()
        //ScriptEngine engine = factory.getScriptEngine("-strict", "--no-java", "--no-syntax-extensions");
        engine = factory.getScriptEngine()
    }

    def exec(Script script) {

        //Bindings binding = engine.getBindings(ScriptContext.ENGINE_SCOPE)
        Bindings binding = engine.createBindings()

        JsLog jsLog = new JsLog(brokerMessagingTemplate, script.id)

        JsHome jsHome = new JsHome()
        Location.list().each {
            jsHome.put it.name, new JsLocation(it, jsHome)
        }
        binding.put "home", jsHome
        binding.put "log", jsLog
        binding.put "console", jsLog
        binding.put "script", new JsScript()
        binding.put "resources", new JsResources()

        try {
            //engine.eval(script.code)

            // instead of eval - just compile and eval
            final CompiledScript compiledScript = ((Compilable)engine).compile(script.code)
            compiledScript.eval(binding)
        } catch (ScriptException e) {
            jsLog.log("error", null, e.message)
            throw e
        } catch(Exception e){
            jsLog.log("error", null, e)
            throw e
        } finally {
            jsHome.close()
        }

    }

    /***********************************************/
    /* Scheduling                                  */
    /***********************************************/
    def unschedule(Script script){
        int count = 0

        Map<String, TriggerDescriptor> triggersMap = getScriptingJobTriggers()

        TriggerDescriptor triggerDescriptor = triggersMap.get(script.id.toString())
        if(triggerDescriptor){
            // remove device tracking
            if( quartzScheduler.unscheduleJob(triggerDescriptor.trigger.key) ){
                count++
            }
        }

        if(count > 0){
            String msg = "Script \"${script.name}\" was deactivated"
            log.info msg
            brokerMessagingTemplate.convertAndSend "/topic/logs", [date: new Date(), level: 'danger', msg: msg]
        }
    }

    void schedule(Script script){
        int count = 0
        int rescheduledCount = 0

        Map<String, TriggerDescriptor> triggersMap = getScriptingJobTriggers()

        if(script.active && script.cronExpression) {
            TriggerDescriptor triggerDescriptor = triggersMap.get(script.id.toString())

            // check repeat interval
            boolean rescheduled = false
            if( triggerDescriptor ){
                String cronExpression = ((CronTrigger)triggerDescriptor.trigger).cronExpression;
                if( cronExpression != script.cronExpression ){
                    quartzScheduler.unscheduleJob(triggerDescriptor.trigger.key)
                    triggerDescriptor = null
                    rescheduled = true
                }
            }

            if( triggerDescriptor ){
                log.warn("Script \"${script.name}\" is already scheduled")
            } else {

                String jobName = "xxx" //but it should be TrackingJob.class.name, but doesn't work with a proper value. It looks like a but in the plugin
                String jobGroup = "scripting"

                AbstractTrigger trigger = TriggerUtils.buildCronTrigger(jobName, jobGroup, script.cronExpression)
                trigger.description = script.id.toString() // set device ID to associate this trigger with device

                ScriptingJob.schedule(trigger, [script: script])

                if(!rescheduled){
                    count++
                } else {
                    rescheduledCount++
                }
            }
        }


        if(count > 0){
            String msg = "Script \"${script.name}\" was added to scheduler"
            log.info msg
            brokerMessagingTemplate.convertAndSend "/topic/logs", [date: new Date(), level: 'info', msg: msg]
        }


        if(rescheduledCount > 0){
            String msg = "Script \"${script.name}\" was rescheduled"
            log.info msg
            brokerMessagingTemplate.convertAndSend "/topic/logs", [date: new Date(), level: 'info', msg: msg]
        }
    }

    private Map<String, TriggerDescriptor> getScriptingJobTriggers(){
        Map<String, TriggerDescriptor> triggersMap = new HashMap<>()
        jobManagerService.getJobs("scripting").triggerDescriptors.each { triggerDescriptors ->
            triggerDescriptors.each {
                // parse device ID from trigger description
                String deviceId = it.trigger.description
                if( deviceId ){
                    triggersMap.put deviceId, it
                }
            }
        }
        return triggersMap
    }
}
