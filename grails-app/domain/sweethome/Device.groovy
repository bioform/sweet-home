package sweethome

import sweethome.sensors.SensorMetaInfo

class Device {
    def sensorFactory

    String addr
    String name
    String title
    String desc
    String containerClass
    Location location
    Float coefficient = 1f
    Float correction = 0f
    int frequencyOfMeasurements
    
    boolean enabled
    SensorMetaInfo metaInfo

    boolean isReadable(){
        if( metaInfo == null){
            metaInfo = sensorFactory.getMetaInfo(containerClass, name)
        }
        metaInfo.units != null
    }

    static mapping = {
        coefficient defaultValue: "1"
        correction  defaultValue: "0"
    }

    static constraints = {
        location nullable:true
        enabled bindable:true
    }
    
    static transients = ['enabled', 'metaInfo']
}
