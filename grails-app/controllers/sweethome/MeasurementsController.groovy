package sweethome

import grails.converters.JSON

class MeasurementsController {

    def index() {
        Device device = Device.get(params.id)
        def meta = device.metaInfo
        def measurementsModel

        if( Double.isAssignableFrom(meta.type) ){
            measurementsModel = TrackingHistoryDouble
        }

        def measurements = []
        if(measurementsModel)
            measurements = measurementsModel.findAllByDevice(device).collect {[y: it.value, x: it.dateCreated.time/1000]}

        def data = [device: device, units: meta.units, measurements: measurements]
        render data as JSON
    }
}
