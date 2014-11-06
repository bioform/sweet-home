package sweethome.scripting

import sweethome.Device
import sweethome.sensors.Sensor
import groovy.util.logging.Log4j

@Log4j
class JsHome implements Map<String, JsLocation> {

    @Delegate Map<String, JsLocation> locations = new HashMap<>()

    private Map<String, Sensor> sensors = [:]


    JsDevice device(String deviceAddrOrName){
        JsDevice result = null

        Device device= Device.findByAddrOrName(deviceAddrOrName, deviceAddrOrName)
        if(device) {
            JsLocation location = new JsLocation(device.location, this)
            result = new JsDevice(device, location, this)
        }

        return result
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
        for(Map.Entry<Device, Sensor> ent:sensors){
            Device device = ent.key
            Sensor sensor = ent.value
            try {
                if(sensor != null) sensor.close();
            } catch (Exception e){
                // close quiet
                log.error "Cannot close sensor ${device.addr}", e
            }
        }
    }
}
