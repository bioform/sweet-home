package sweethome

import grails.converters.JSON
import org.grails.databinding.BindingFormat

class MeasurementsController {

    def index(DateFilter filter) {
        Device device = Device.get(params.id)
        def meta = device.metaInfo
        def measurementsModel

        if( Double.isAssignableFrom(meta.type) ){
            measurementsModel = TrackingHistoryDouble
        }

        def measurements = []
        if(measurementsModel) {
            def history = measurementsModel.withCriteria {
                eq 'device', device

                if( filter.startDate ) {
                    ge 'dateCreated', filter.startDate
                }
                if( filter.endDate ) {
                    le 'dateCreated', filter.endDate
                }
                if(filter.startDate == null && filter.endDate == null){
                    order 'dateCreated', 'desc'
                    maxResults 500
                }
            }

            measurements = history.collect { [y: it.value, x: it.dateCreated.time / 1000] }
        }
        def data = [device: device, units: meta.units, measurements: measurements]
        render data as JSON
    }
}

class DateFilter {

    @BindingFormat("yyyy-MM-dd'T'hh:mm:ss.S'Z'")
    Date startDate

    @BindingFormat("yyyy-MM-dd'T'hh:mm:ss.S'Z'")
    Date endDate

}
