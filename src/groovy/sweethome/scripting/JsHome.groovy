package sweethome.scripting

import sweethome.Device

class JsHome extends HashMap {

    JsDevice device(String deviceAddrOrName){
        JsDevice result = null

        Device device= Device.findByAddrOrName(deviceAddrOrName, deviceAddrOrName)
        if(device)
            result = new JsDevice(device)

        return result
    }
}
