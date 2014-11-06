package sweethome

class TrackingHistory {
    String name
    Device device
    Date dateCreated

    static mapping = {

    }

    static constraints = {
        device nullable: true
        name   nullable: true
    }
}
