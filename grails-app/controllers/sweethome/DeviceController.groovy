package sweethome

import grails.converters.JSON
import grails.transaction.Transactional

class DeviceController {

    def deviceService

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

        render device as JSON
    }
}
