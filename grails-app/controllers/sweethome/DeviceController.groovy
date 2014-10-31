package sweethome

import grails.converters.JSON
import grails.transaction.Transactional
import sweethome.sensors.Sensor
import util.SensorUtils

class DeviceController extends ApplicationController {

    def deviceService
    def trackingService

    def index() {
        def json
        if(params.id != null){
            json = Device.get(params.id)
        } else {
            json = params.sync ? deviceService.sync() : Device.list()
        }
        render json as JSON
    }

    @Transactional
    def save() {
        def device = Device.get(params.id)

        assignAttributes(device, ["title", "tracked", "frequencyOfMeasurements","coefficient", "correction"])

        if(request.JSON.location != null) {
            def newLocation = request.JSON.location
            device.location = newLocation.asBoolean() ? Location.get(newLocation.id) : null
        }

        device.save(flush: true, failOnError: true)

        // set previous status since we don't retrieve it from 1-Wire
        device.enabled = request.JSON.enabled

        render device as JSON
    }

    def read(){
        def device = Device.get(params.id)

        if(device != null){
            def meta = device.metaInfo

            device.withSensor { sensor ->
                if(sensor) {
                    def raw = sensor.read()
                    def val = SensorUtils.addCorrection( raw, device.coefficient, device.correction)
                    def formatted = "$val ${meta.units}"

                    // save measurements to tracking history table
                    trackingService.track(device, raw, val)

                    def readings = [val: val, formatted: formatted]

                    render readings as JSON
                } else {
                    render status: 404, text: "Cannot find device with address \"${device.addr}\""
                }
            }
        } else {
            render status: 404, text: "Cannot find device with id \"${params.id}\""
        }
    }
}
