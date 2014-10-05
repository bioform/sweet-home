package sweethome

import grails.converters.JSON
import grails.transaction.Transactional

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
            sensorFactory.get(device.containerClass, device.name, device.addr).runAndClose { sensor ->
                if(sensor) {
                    render sensor.read() as JSON
                } else {
                    render status: 500, text: "Cannot define appropriate sensor"
                }
            }
        } else {
            render status: 404, text: "Cannot find device with id \"${params.id}\""
        }
    }
}
