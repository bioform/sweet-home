package sweethome.scripting

import sweethome.Device

class JsDevice {
    Device device

    public JsDevice(Device device){
        this.device = device
    }

    def read(){
        def val
        device.withSensor { sensor ->
            val = sensor.read()
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
