package sweethome

class Script {

    String name
    String code

    String cronExpression
    boolean active

    static mapping = {
        code type: "text"
    }

    static constraints = {
        cronExpression nullable:true
    }
}
