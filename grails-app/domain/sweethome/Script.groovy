package sweethome

class Script {

    def scriptingService

    String name
    String code

    String cronExpression
    boolean cronExpressionChanged

    boolean active
    boolean activeChanged

    static mapping = {
        code type: "text"
    }

    static constraints = {
        cronExpression nullable:true
    }

    static transients = ['cronExpressionChanged', 'activeChanged']

    def beforeUpdate() {
        cronExpressionChanged = this.isDirty('cronExpression')
        activeChanged         = this.isDirty('active')
        return true //Indicates that the update can proceed
    }

    def afterUpdate() {
        if(activeChanged || cronExpressionChanged) {
            if(active){
                scriptingService.schedule( this )
            } else {
                scriptingService.unschedule( this )
            }
            cronExpressionChanged = false
            activeChanged = false
        }

    }
}
