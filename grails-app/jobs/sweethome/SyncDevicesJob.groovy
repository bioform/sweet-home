package sweethome

import com.dalsemi.onewire.container.OneWireContainer
import grails.transaction.Transactional
import org.hibernate.SessionFactory


@Transactional
class SyncDevicesJob {
    def concurrent = false

    def group = "system"
    def description = "Synchronize DB with real devices"

    static triggers = {
      simple name: 'sync-devices-trigger', startDelay: 10*1000L, repeatInterval: 30*1000L // execute job once in 30 seconds
    }

    def deviceService

    def execute() {
        deviceService.sync()
    }

}
