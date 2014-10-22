package sweethome.scripting

import sweethome.Device
import sweethome.sensors.Sensor
import groovy.util.logging.Log4j

@Log4j
class JsHome extends HashMap {

    Map<Device, Sensor> sensors = [:]

    JsDevice device(String deviceAddrOrName){
        JsDevice result = null

        Device device= Device.findByAddrOrName(deviceAddrOrName, deviceAddrOrName)
        if(device)
            result = new JsDevice(device, this)

        return result
    }

    Sensor openSensor(Device device) {
        Sensor sensor = device.sensor
        sensors.put device, sensor
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
