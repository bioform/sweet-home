package sweethome.scripting

import jdk.nashorn.api.scripting.AbstractJSObject
import sweethome.Device
import sweethome.sensors.Sensor
import groovy.util.logging.Log4j

@Log4j
class JsHome extends AbstractJSObject {

    @Delegate Map<String, JsLocation> locations = new HashMap<>()

    private Map<String, Sensor> sensors = [:]
    private final DeviceFunction deviceFunction = new DeviceFunction();

    // do you have a property of that given name?
    @Override
    public boolean hasMember(String name) {
        return "device".equals(name) || locations?.containsKey(name);
    }

    @Override
    public Object getMember(String name) {
        if (!hasMember(name)) {
            return null
        }
        switch (name){
            case "device":
                return deviceFunction;
            default:
                return locations.get(name);
        }
    }

    Sensor getSensor(Device device) {
        Sensor sensor = sensors.get(device.addr)
        if(sensor == null) {
            sensor = device.sensor
            sensors.put device.addr, sensor
        }
        return sensor
    }

    void close(){
        for(Map.Entry<String, Sensor> ent:sensors){
            String addr = ent.key
            Sensor sensor = ent.value
            try {
                if(sensor != null) sensor.close();
            } catch (Exception e){
                // close quiet
                log.error "Cannot close sensor ${addr}", e
            }
        }
    }

    private class DeviceFunction extends AbstractJSObject {
        @Override
        public Object call(Object thiz, Object... args) {
            if(args == null)
                throw new NullPointerException("Provide device address or name")
            if(args.length != 1)
                throw new IllegalArgumentException("Provide device address or name")

            String deviceAddrOrName = args[0].toString()
            JsDevice result = null

            Device device= Device.findByAddrOrName(deviceAddrOrName, deviceAddrOrName)
            if(device) {
                JsLocation location = new JsLocation(device.location, JsHome.this)
                result = new JsDevice(device, location, JsHome.this)
            }

            return result
        }
        // yes, I'm a function !
        @Override
        public boolean isFunction() {
            return true;
        }
    }
}
