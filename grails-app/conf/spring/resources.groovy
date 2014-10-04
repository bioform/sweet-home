import sweethome.SensorReadingsMarshaller
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
}