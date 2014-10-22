package sweethome.scripting

import jdk.nashorn.api.scripting.AbstractJSObject
import sweethome.Device
import sweethome.sensors.Sensor
import sweethome.sensors.SensorMetaInfo
import util.SensorUtils

class JsDevice extends AbstractJSObject{
    Device device
    JsLocation location
    JsHome home
    Sensor sensor
    SensorMetaInfo metaInfo
    ReadFunction readFunction = new ReadFunction();

    Map<String, Function> functionMap = new HashMap<>(4)
    Set<String> availableMethods

    public JsDevice(Device device, JsHome home){
        this(device, null, home)
    }

    public JsDevice(Device device, JsLocation location, JsHome home){
        this.device = device
        this.location = location
        this.home = home
        this.metaInfo = device.metaInfo
        if( this.metaInfo )
            this.availableMethods = metaInfo.methods.name.toSet() - Object.methods.name.toSet()
    }

    // do you have a property of that given name?
    @Override
    public boolean hasMember(String name) {
        return "location".equals(name) || availableMethods?.contains(name);
    }

    // get the value of that named property
    @Override
    public Object getMember(String name) {
        if( !hasMember(name) ){
            return null
        }
        // return a 'function' value for this property
        Function function = functionMap.get(name)
        if( !function ){
            switch (name){
                case "addr":
                    return device.addr
                case "title":
                    return device.title
                case "name":
                    return device.name
                case "location":
                    if(location == null){
                        location = new JsLocation(device.location, home)
                    }
                    return location;
                case "read":
                    return readFunction;
                default:
                    function = new Function(name);
            }
            functionMap.put( name, function )
        }
        return function
    }

    def read(){
        Sensor sensor = getSensor()
        if(sensor) {
            return SensorUtils.addCorrection(sensor.read(), device.coefficient, device.correction)
        } else {
            return null
        }
    }


    private getSensor(){
        if( !sensor ){
            sensor = home.openSensor(device)
        }
        return sensor
    }

    private class ReadFunction extends AbstractJSObject {
        @Override
        public Object call(Object thiz, Object... args) {
            JsDevice.this.read()
        }
        // yes, I'm a function !
        @Override
        public boolean isFunction() {
            return true;
        }
    }

    private class Function extends AbstractJSObject {

        String name

        Function(String name){
            this.name = name;
        }

        @Override
        public Object call(Object thiz, Object... args) {
            JsDevice.this.getSensor()."$name" *args
        }
        // yes, I'm a function !
        @Override
        public boolean isFunction() {
            return true;
        }
    }
}
