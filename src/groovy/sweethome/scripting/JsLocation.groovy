package sweethome.scripting

import jdk.nashorn.api.scripting.AbstractJSObject;
import sweethome.Device;
import sweethome.Location;

import java.util.*;

class JsLocation extends AbstractJSObject {

    private final Map<String, JsDevice> deviceAddrMap = new HashMap<>(4);
    private final Map<String, JsDevice> deviceNameMap = new HashMap<>(4);
    private final Location location;
    private final JsHome home;

    private boolean isDeviceLoaded;

    public JsLocation(Location location, JsHome home){
        this.location = location;
        this.home = home;
    }

    public JsHome getHome() {
        return home;
    }

    private void loadDevices() {
        if( !this.isDeviceLoaded ) {
            Set<Device> devices = location.getDevices();
            for (Device it : devices) {
                //println "---> $addresses"
                //println "===> $names"
                JsDevice device = new JsDevice(it, this, this.home);
                this.deviceAddrMap.put(it.getAddr(), device);
                this.deviceNameMap.put(it.getTitle(), device);
            }
        }

        this.isDeviceLoaded = true;
    }

    @Override
    public boolean hasMember(String key) {
        if( !this.isDeviceLoaded ){
            loadDevices();
        }
        boolean result = this.deviceAddrMap.containsKey(key);
        return result || this.deviceNameMap.containsKey(key);
    }

    @Override
    public Object getMember(String key) {
        if( !this.isDeviceLoaded ){
            loadDevices();
        }
        Object result = this.deviceAddrMap.get(key);

        if( result == null){
            result = this.deviceNameMap.get(key);
        }
        return result;
    }

    @Override
    public Set keySet() {
        return this.deviceNameMap.keySet();
    }

    @Override
    public Collection values() {
        return this.deviceNameMap.values();
    }
}
