package sweethome.scripting

import sweethome.Device
import sweethome.Location

class JsLocation implements Map {

    private Map deviceAddrMap = new HashMap(4)
    private Map deviceNameMap = new HashMap(4)
    private boolean isDeviceLoaded
    Location location
    JsHome home

    public JsLocation(Location location, JsHome home){
        this.location = location
        this.home = home
    }

    private void loadDevices() {
        Map addresses = this.deviceAddrMap
        Map names = this.deviceNameMap
        if( !this.isDeviceLoaded ) Device.list().each {
            //println "---> $addresses"
            //println "===> $names"
            JsDevice device = new JsDevice(it, this, this.getHome())
            addresses.put it.addr, device
            names.put it.title, device
        }

        this.isDeviceLoaded = true
    }

    @Override
    int size() {
        if( !this.isDeviceLoaded ){
            loadDevices()
        }
        return this.deviceAddrMap.size()
    }

    @Override
    boolean isEmpty() {
        if( !this.isDeviceLoaded ){
            loadDevices()
        }
        return this.deviceAddrMap.isEmpty()
    }

    @Override
    boolean containsKey(Object key) {
        if( !this.isDeviceLoaded ){
            loadDevices()
        }
        boolean result = this.deviceAddrMap.containsKey(key)
        return result || this.deviceNameMap.containsKey(key)
    }

    @Override
    boolean containsValue(Object value) {
        if( !this.isDeviceLoaded ){
            loadDevices()
        }
        boolean result = this.deviceAddrMap.containsValue(key)
        return result || this.deviceNameMap.containsValue(key)
    }

    @Override
    Object get(Object key) {
        if( !this.isDeviceLoaded ){
            loadDevices()
        }
        def result = this.deviceAddrMap.get(key)

        if( result == null){
            result = this.deviceNameMap.get(key)
        }
        return result
    }

    @Override
    Object put(Object key, Object value) {
        throw new UnsupportedOperationException()
    }

    @Override
    Object remove(Object key) {
        throw new UnsupportedOperationException()
    }

    @Override
    void putAll(Map m) {
        throw new UnsupportedOperationException()
    }

    @Override
    void clear() {
        throw new UnsupportedOperationException()
    }

    @Override
    Set keySet() {
        return this.deviceNameMap.keySet()
    }

    @Override
    Collection values() {
        return this.deviceNameMap.values()
    }

    @Override
    Set<Map.Entry> entrySet() {
        return this.deviceNameMap.entrySet()
    }
}
