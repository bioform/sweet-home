package sweethome

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

    boolean isReadable(){
        sensorFactory.readable(containerClass, name)
    }

    static mapping = {
        coefficient defaultValue: "1"
        correction  defaultValue: "0"
    }

    static constraints = {
        location nullable:true
        enabled bindable:true
    }
    
    static transients = ['enabled']
}
