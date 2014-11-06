package sweethome

class ScriptingJob {
    def group = "scripting"
    def deviceService
    def scriptingService

    def execute(context) {
        synchronized (deviceService){
            Script script = context.mergedJobDataMap.script
            scriptingService.exec script
        }
    }
}
