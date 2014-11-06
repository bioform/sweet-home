package sweethome

class TrackingHistoryDouble extends TrackingHistory {

    Double raw
    Double value

    static constraints = {
        raw   nullable: true
        value nullable: true
    }
}
