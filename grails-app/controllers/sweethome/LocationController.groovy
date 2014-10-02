package sweethome

import grails.converters.JSON
import grails.transaction.Transactional

class LocationController {

    def index() {
        def json = Location.list() as JSON
        render json
    }

    @Transactional
    def save() {
        def location = params.id ? Location.get(params.id) : new Location()
        location.name = request.JSON.name
        location.save()

        render location as JSON
    }
}
