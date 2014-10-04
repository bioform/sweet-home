package sweethome

import grails.converters.JSON
import sweethome.sensors.Sensor

class SensorReadingsMarshaller {
    void register() {
        JSON.registerObjectMarshaller( Sensor.Readings ) { Sensor.Readings readings ->
            return [
                    val: readings.val,
                    formatted: readings.formatted
            ]
        }
    }
}
