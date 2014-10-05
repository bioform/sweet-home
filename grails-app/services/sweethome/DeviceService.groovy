package sweethome

import com.dalsemi.onewire.container.OneWireContainer
import grails.transaction.Transactional

@Transactional
class DeviceService {

    def list() {
        Set<String> addresses = new HashSet<>()
        try {
            Collection<OneWireContainer> rawList = Home.getContainers()
            rawList.each {
                String addr = it.addressAsString
                addresses.add(addr)
                Device device = Device.findByAddr(addr)
                if ( device == null ) {
                    device = new Device(
                            addr: addr,
                            name: it.name,
                            title: it.name,
                            desc: it.description,
                            containerClass: it.class.name
                    )
                    device.save(flush: true, failOnError: true)
                }
                if(device.containerClass != it.class.name){
                    device.containerClass = it.class.name
                    device.save(flush: true, failOnError: true)
                }
            }
        } catch (Exception e){
            log.error("Cannot get device list. " + e)
        }
        def devices = Device.list()
        devices.each {
            boolean rawEnabled = addresses.contains(it.addr)
            if (it.enabled != rawEnabled) {
                it.enabled = rawEnabled
            }
        }
        return devices
    }

}
