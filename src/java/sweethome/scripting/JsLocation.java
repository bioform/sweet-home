package sweethome.scripting;

import sweethome.Device;
import sweethome.Location;

import java.util.*;

class JsLocation implements Map {

    private final Map deviceAddrMap = new HashMap(4);
    private final Map deviceNameMap = new HashMap(4);
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
    public int size() {
        if( !this.isDeviceLoaded ){
            loadDevices();
        }
        return this.deviceAddrMap.size();
    }

    @Override
    public boolean isEmpty() {
        if( !this.isDeviceLoaded ){
            loadDevices();
        }
        return this.deviceAddrMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if( !this.isDeviceLoaded ){
            loadDevices();
        }
        boolean result = this.deviceAddrMap.containsKey(key);
        return result || this.deviceNameMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if( !this.isDeviceLoaded ){
            loadDevices();
        }
        boolean result = this.deviceAddrMap.containsValue(value);
        return result || this.deviceNameMap.containsValue(value);
    }

    @Override
    public Object get(Object key) {
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
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set keySet() {
        return this.deviceNameMap.keySet();
    }

    @Override
    public Collection values() {
        return this.deviceNameMap.values();
    }

    @Override
    public Set<Map.Entry> entrySet() {
        return this.deviceNameMap.entrySet();
    }
}
