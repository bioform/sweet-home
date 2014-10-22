package sweethome.scripting

import jdk.nashorn.api.scripting.AbstractJSObject;

class JsLog extends AbstractJSObject {

    def brokerMessagingTemplate
    private static List<String> AVAILABLE_METHODS =["debug", "info", "warn", "error", "log"]

    Map<String, LogFunction> logFunctionMap = new HashMap<>(4)
    String topic

    public JsLog(def brokerMessagingTemplate, def id){
        this.brokerMessagingTemplate = brokerMessagingTemplate
        this.topic = "/topic/script/$id/logs"
    }

    // do you have a property of that given name?
    @Override
    public boolean hasMember(String name) {
        return AVAILABLE_METHODS.contains(name);
    }

    // get the value of that named property
    @Override
    public Object getMember(String name) {
        if( !hasMember(name) ){
            return null
        }
        // return a 'function' value for this property
        LogFunction logFunction = logFunctionMap.get(name)
        if( !logFunction ){
            logFunctionMap.put( name, logFunction = new LogFunction(name) )
        }
        return logFunction
    }

    void log(String level, target, Object... args) {
        String msg = Arrays.asList(args).join(" ")
        Map json = [date: new Date(), level: level, msg: msg]
        if(target){
            json.target = target
        }
        brokerMessagingTemplate.convertAndSend topic, json
    }

    private class LogFunction extends AbstractJSObject{

        String name

        LogFunction(String name){
            this.name = name
        }

        @Override
        public Object call(Object thiz, Object... args) {
            //JsLog.this.log(name, name == 'log' ? 'console':null , args)
            JsLog.this.log(name, null , args)
            return  null
        }
        // yes, I'm a function !
        @Override
        public boolean isFunction() {
            return true;
        }
    }

}
