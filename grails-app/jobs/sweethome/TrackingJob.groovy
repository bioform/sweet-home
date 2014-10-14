package sweethome



class TrackingJob {

    def group = "tracking"

    def execute(context) {
        Device device = context.mergedJobDataMap.device
        //TODO add measurements to TrackingHistory table in DB
    }
}
