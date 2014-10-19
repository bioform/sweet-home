package sweethome.scripting

import sweethome.Device
import sweethome.Location

class JsLocation implements Map {

    private Map deviceAddrMap
    private Map deviceNameMap
    Location location

    public JsLocation(Location location){
        this.location = location
    }

    private void loadDevices() {
        Device.list().each {
            JsDevice device = new JsDevice(it)
            deviceAddrMap.put device.addr, device
            deviceNameMap.put device.title, device
        }
    }

    @Override
    int size() {
        if( deviceAddrMap == null){
            loadDevices()
        }
        return deviceAddrMap.size()
    }

    @Override
    boolean isEmpty() {
        if( deviceAddrMap == null){
            loadDevices()
        }
        return deviceAddrMap.isEmpty()
    }

    @Override
    boolean containsKey(Object key) {
        if( deviceAddrMap == null){
            loadDevices()
        }
        boolean result = deviceAddrMap.containsKey(key)
        return result || deviceNameMap.containsKey(key)
    }

    @Override
    boolean containsValue(Object value) {
        if( deviceAddrMap == null){
            loadDevices()
        }
        boolean result = deviceAddrMap.containsValue(key)
        return result || deviceNameMap.containsValue(key)
    }

    @Override
    Object get(Object key) {
        if( deviceAddrMap == null){
            loadDevices()
        }
        def result = deviceAddrMap.get(key)
        return result || deviceNameMap.get(key)
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
        return deviceNameMap.keySet()
    }

    @Override
    Collection values() {
        return deviceNameMap.values()
    }

    @Override
    Set<Map.Entry> entrySet() {
        return deviceNameMap.entrySet()
    }
}
