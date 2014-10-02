package sweethome

class Location {

    String name
    Location parent
    static hasMany = [devices: Device]

    static constraints = {
        parent(nullable:true)
    }
}
