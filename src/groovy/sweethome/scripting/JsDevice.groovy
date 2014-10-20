package sweethome.scripting

import sweethome.Device
import util.SensorUtils

class JsDevice {
    Device device

    public JsDevice(Device device){
        this.device = device
    }

    def read(){
        def val
        device.withSensor { sensor ->
            val = SensorUtils.addCorrection( sensor.read(), device.coefficient, device.correction)
        }
        return val
    }

    String getTitle(){
        device.title
    }

    String getAddr(){
        device.addr
    }

    String getName(){
        device.name
    }
}
