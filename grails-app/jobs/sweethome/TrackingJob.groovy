package sweethome

import grails.converters.JSON
import util.SensorUtils


class TrackingJob {

    def group = "tracking"
    def deviceService
    def trackingService

    def execute(context) {
        synchronized (deviceService){
            Device device = context.mergedJobDataMap.device
            device.withSensor { sensor ->
                if(sensor) {
                    def raw = sensor.read()
                    def correctedValue = SensorUtils.addCorrection( raw, device.coefficient, device.correction)

                    // write raw and corrected data to tracking history
                    def history = trackingService.track(device, raw, correctedValue)

                    if( !history ){
                        log.error "Cannot find tracking history table for type \"${raw.class.getSimpleName()}\""
                    }
                } else {
                    log.error "Cannot find device with address \"${device.addr}\""
                    trackingService.track(device, null, null)
                }
            }
        }
    }
}
