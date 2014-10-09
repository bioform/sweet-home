import sweethome.JobDescriptorMarshaller
import sweethome.sensors.SensorFactory
import util.marshalling.CustomObjectMarshallers
import sweethome.DeviceMarshaller

import java.util.concurrent.*
import org.springframework.context.event.*

// Place your Spring DSL code here
beans = {
    customObjectMarshallers( CustomObjectMarshallers ) {
        marshallers = [
            new DeviceMarshaller(),
            new JobDescriptorMarshaller()
        ]
    }

    sensorFactory(SensorFactory) { bean ->
        bean.autowire = 'byName'
    }
}