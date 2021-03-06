package sweethome

import sweethome.sensors.Sensor
import sweethome.sensors.SensorFactory
import sweethome.sensors.SensorMetaInfo

class Device {
    def sensorFactory
    def trackingService

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
    boolean frequencyOfMeasurementsChanged
    boolean tracked
    boolean trackedChanged
    
    boolean enabled

    private SensorMetaInfo metaInfo

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

    Sensor getSensor(){
        SensorMetaInfo meta = getMetaInfo()
        if(meta != null){
            return sensorFactory.get(meta, addr)
        } else {
            return null
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
    
    static transients = ['metaInfo', 'frequencyOfMeasurementsChanged', 'trackedChanged']

    def beforeUpdate() {
        frequencyOfMeasurementsChanged = this.isDirty('frequencyOfMeasurements')
        trackedChanged                 = this.isDirty('tracked')
        return true //Indicates that the update can proceed
    }

    def afterUpdate() {
        if(trackedChanged || frequencyOfMeasurementsChanged) {
            if(tracked && enabled){
                trackingService.schedule( [this] )
            } else {
                trackingService.unschedule( [this] )
            }
            frequencyOfMeasurementsChanged = false
            trackedChanged = false
        }

    }
}
