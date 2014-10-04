package sweethome

import sweethome.actors.ActorFactory
import sweethome.sensors.SensorFactory

class Device {

    String addr
    String name
    String title
    String desc
    String containerClass
    Location location
    float coefficient
    float correction
    
    boolean enabled

    boolean isReadable(){
        SensorFactory.readable(containerClass, name)
    }

    static mapping = {
        coefficient defaultValue: 1
        correction  defaultValue: 0
    }

    static constraints = {
        location nullable:true
        enabled bindable:true
    }
    
    static transients = ['enabled']
}
