package sweethome.scripting

class JsLog {
    def brokerMessagingTemplate
    String topic

    public JsLog(def brokerMessagingTemplate, def id){
        this.brokerMessagingTemplate = brokerMessagingTemplate
        this.topic = "/topic/script/$id/logs"
    }

    def debug(String msg){
        log 'debug', msg
    }
    def info(String msg){
        log 'info', msg
    }
    def warn(String msg){
        log 'warn', msg
    }
    def error(String msg){
        log 'error', msg
    }

    def log(String level, String msg) {
        brokerMessagingTemplate.convertAndSend topic, [date: new Date(), level: level, msg: msg]
    }
}
