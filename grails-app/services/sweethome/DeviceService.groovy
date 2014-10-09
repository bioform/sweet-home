package sweethome

import com.dalsemi.onewire.container.OneWireContainer
import grails.transaction.Transactional
import org.hibernate.SessionFactory

@Transactional
class DeviceService {

    SessionFactory sessionFactory

    def list() {
        Set<String> addresses = new HashSet<>()
        try {
            Collection<OneWireContainer> rawList = HomeNet.getContainers()
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
                            containerClass: it.class.name,
                            enabled: true
                    )
                    device.save(flush: true, failOnError: true)
                }
                else if(device.containerClass != it.class.name || !device.enabled){
                    device.containerClass = it.class.name
                    device.enabled = true
                    device.save()
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
                it.save()
            }
        }
        flush() // flush data to DB
        return devices
    }

    /**
     * Flush hibernate session to DB
     */
    private void flush(){
        assert sessionFactory != null
        def hibSession = sessionFactory.getCurrentSession()
        assert hibSession != null
        hibSession.flush()
    }

}
