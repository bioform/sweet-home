package sweethome.scripting;

import sweethome.Device;
import sweethome.Location;

import java.util.*;

class JsLocation implements Map {

    private Map deviceAddrMap = new HashMap(4);
    private Map deviceNameMap = new HashMap(4);
    private boolean isDeviceLoaded;
    Location location;
    JsHome home;

    public JsLocation(Location location, JsHome home){
        this.location = location;
        this.home = home;
    }

    private void loadDevices() {
        if( !this.isDeviceLoaded ) for(Device it:(List<Device>)Device.list()) {
            //println "---> $addresses"
            //println "===> $names"
            JsDevice device = new JsDevice(it, this, this.home);
            this.deviceAddrMap.put( it.getAddr(), device );
            this.deviceNameMap.put( it.getTitle(), device );
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
