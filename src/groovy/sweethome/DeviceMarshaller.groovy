package sweethome

import grails.converters.JSON
import sweethome.Device

class DeviceMarshaller {
    void register() {
        JSON.registerObjectMarshaller( Device ) { Device device ->
            return [
                    id : device.id,
                    addr : device.addr,
                    name : device.name,
                    title : device.title,
                    desc: device.desc,
                    coefficient: device.coefficient,
                    correction: device.correction,
                    containerClass: device.containerClass,
                    enabled: device.enabled,
                    readable: device.readable,
                    tracked: device.tracked,
                    frequencyOfMeasurements: device.frequencyOfMeasurements,
                    location: device.location ? [
                                id: device.location.id,
                                name: device.location.name
                              ] : null

            ]
        }
    }
}
