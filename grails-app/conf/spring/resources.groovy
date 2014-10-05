import sweethome.SensorReadingsMarshaller
import sweethome.sensors.SensorFactory
import util.marshalling.CustomObjectMarshallers
import sweethome.DeviceMarshaller

// Place your Spring DSL code here
beans = {
    customObjectMarshallers( CustomObjectMarshallers ) {
        marshallers = [
                new DeviceMarshaller(),
                new SensorReadingsMarshaller()
        ]
    }

    sensorFactory(SensorFactory) { bean ->
        bean.autowire = 'byName'
    }
}