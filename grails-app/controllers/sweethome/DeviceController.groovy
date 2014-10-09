package sweethome

import grails.converters.JSON
import grails.transaction.Transactional
import sweethome.sensors.Sensor
import util.SensorUtils

class DeviceController {

    def deviceService
    def sensorFactory

    def index() {
        def json = deviceService.list() as JSON
        render json
    }

    @Transactional
    def save() {
        def device = Device.get(params.id)
        device.title = request.JSON.title

        def newLocation = request.JSON.location
        device.location = newLocation.asBoolean() ? Location.get(newLocation.id) : null

        device.save()

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
}
