package sweethome

import grails.converters.JSON
import util.SensorUtils


class TrackingJob {

    def group = "tracking"
    def deviceService

    def execute(context) {
        synchronized (deviceService){
            Device device = context.mergedJobDataMap.device
            device.withSensor { sensor ->
                if(sensor) {
                    def raw = sensor.read()
                    def correctedValue = SensorUtils.addCorrection( raw, device.coefficient, device.correction)

                    // write raw and corrected data to tracking history
                    def history
                    if(raw instanceof Double){
                        history = new TrackingHistoryDouble([device: device, raw: raw, value: correctedValue])
                    }
                    if(history){
                        if( !history.save() ){
                            StringBuilder sb = new StringBuilder()
                            if( history.hasErrors() ) {
                                history.errors.each { sb << "\n$it" }
                            }
                            log.error "Cannot save tracking history for \"${device.name}\" (addr: \"${device.addr}\"). $sb"
                        }
                    }
                    else {
                        log.error "Cannot find tracking history table for type \"${raw.class.getSimpleName()}\""
                    }
                } else {
                    log.error "Cannot find device with address \"${device.addr}\""
                }
            }
        }
    }
}
