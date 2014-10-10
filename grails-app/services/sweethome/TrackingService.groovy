package sweethome

import grails.transaction.Transactional

@Transactional
class TrackingService {

    @grails.events.Listener
    def newDevices(List devices){
        log.debug "${devices.size()} devices was added"
    }

    @grails.events.Listener
    def enableDevices(List devices){
        log.debug "${devices.size()} devices was enabled"
    }

    @grails.events.Listener
    def disableDevices(List devices){
        log.debug "${devices.size()} devices was disabled"
    }
}
