package sweethome

import sweethome.sensors.Sensor
import sweethome.sensors.SensorFactory
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

    /**
     * Frequency Of Measurements in seconds (it can be null)
     */
    Integer frequencyOfMeasurements
    boolean tracked
    
    boolean enabled

    SensorMetaInfo metaInfo

    boolean isReadable(){
        SensorMetaInfo meta = getMetaInfo()
        return meta != null && meta.units != null
    }

    SensorMetaInfo getMetaInfo(){
        if( metaInfo == null){
            metaInfo = sensorFactory.getMetaInfo(containerClass, name)
        }
        return metaInfo
    }

    void withSensor(Closure closure){
        Sensor sensor = null
        try {
            SensorMetaInfo meta = getMetaInfo()
            if(meta != null){
                sensor = sensorFactory.get(meta, addr)
            }
            closure(sensor);
        } finally {
            if(sensor != null) sensor.close();
        }
    }

    static mapping = {
        coefficient defaultValue: "1"
        correction  defaultValue: "0"
    }

    static constraints = {
        location nullable:true
        frequencyOfMeasurements nullable:true
    }
    
    static transients = ['metaInfo']
}
