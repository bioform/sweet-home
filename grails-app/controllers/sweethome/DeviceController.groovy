package sweethome

import grails.converters.JSON
import grails.transaction.Transactional
import sweethome.sensors.Sensor
import util.SensorUtils

class DeviceController {

    def deviceService
    def trackingService

    def index() {
        def list = params.sync ? deviceService.sync() : Device.list()
        render list as JSON
    }

    @Transactional
    def save() {
        def device = Device.get(params.id)

        boolean wasTracked = device.tracked

        if(request.JSON.title != null)
            device.title = request.JSON.title

        if(request.JSON.tracked != null)
            device.tracked = !!request.JSON.tracked

        if(request.JSON.frequency != null)
            device.frequencyOfMeasurements = jsonInt('frequency')

        if(request.JSON.location != null) {
            def newLocation = request.JSON.location
            device.location = newLocation.asBoolean() ? Location.get(newLocation.id) : null
        }

        if( device.save() && device.enabled){
            if(device.tracked != wasTracked){
                if(device.tracked)
                    trackingService.enableDevices([device])
                else
                    trackingService.disableDevices([device])
            }
        }

        // set previous status since we don't retrieve it from 1-Wire
        device.enabled = request.JSON.enabled

        render device as JSON
    }

    def read(){
        def device = Device.get(params.id)

        if(device != null){
            device.withSensor { sensor ->
                if(sensor) {
                    def val = SensorUtils.addCorrection( sensor.read(), device.coefficient, device.correction)
                    def formatted = sensor.format(val)

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

    private Integer jsonInt(param){
        def val = request.JSON."$param"
        if( !(val instanceof Integer) ){
            val = val && val.isInteger() ? val.toInteger() :  null
        }
        return val
    }
}
