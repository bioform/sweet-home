package sweethome

class Location {

    String name
    Location parent
    static hasMany = [devices: Device]

    static mapping = {
        addresses cascade: "all"
    }

    static constraints = {
        parent(nullable:true)
    }
}
