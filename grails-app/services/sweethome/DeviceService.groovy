package sweethome

import com.dalsemi.onewire.container.OneWireContainer
import grails.transaction.Transactional
import org.hibernate.SessionFactory
import sweethome.sensors.SensorFactory
import sweethome.sensors.SensorMetaInfo

@Transactional
class DeviceService {

    def brokerMessagingTemplate

    SessionFactory sessionFactory
    SensorFactory  sensorFactory
    String lastException

    def synchronized sync(){
        def newDevices = []
        def enabledDevices = []
        def disabledDevices = []

        Set<String> addresses = new HashSet<>()
        try {
            Collection<OneWireContainer> rawList = HomeNet.getContainers()
            rawList.each {
                String addr = it.addressAsString
                addresses.add(addr)
                Device device = Device.findByAddr(addr)

                if ( device == null ) {
                    SensorMetaInfo meta = sensorFactory.getMetaInfo(it.class.name, it.name)
                    device = new Device(
                            addr: addr,
                            name: it.name,
                            title: it.name,
                            desc: it.description,
                            containerClass: it.class.name,
                            enabled: true,
                            frequencyOfMeasurements: meta?.getFrequencyOfMeasurements()
                    )
                    if( device.save() ){
                        newDevices << device
                    } else {
                        StringBuilder sb = new StringBuilder()
                        if( device.hasErrors() ) {
                            device.errors.each { sb << "\n$it" }
                        }
                        log.error "Cannot register device \"${it.name}\" (addr: \"$addr\"). $sb"
                    }

                }
                else if(device.containerClass != it.class.name || !device.enabled){
                    boolean wasEnabled = !device.enabled
                    device.containerClass = it.class.name
                    device.enabled = true
                    device.save()
                    if( wasEnabled ) enabledDevices << device
                }
            }
            lastException = null
        } catch (Exception e){
            String msg = "Cannot get device list. " + e
            if(msg != lastException){
                log.error msg
                lastException = msg
            }
        }
        def devices = Device.list()
        devices.each {
            boolean rawEnabled = addresses.contains(it.addr)
            if (it.enabled != rawEnabled) {
                it.enabled = rawEnabled
                it.save()
                if( !rawEnabled ) disabledDevices << it
            }
        }
        flush() // flush data to DB

        // send notification
        if( !newDevices.isEmpty() ){
            event('newDevices', newDevices)
        }
        if( !enabledDevices.isEmpty() ){
            event('enableDevices', enabledDevices)
        }
        if( !disabledDevices.isEmpty() ){
            event('disableDevices', disabledDevices)
        }

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
