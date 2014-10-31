package sweethome

import grails.plugins.quartz.TriggerDescriptor
import grails.plugins.quartz.TriggerUtils
import grails.transaction.Transactional
import org.quartz.SimpleTrigger
import org.quartz.Trigger

@Transactional
class TrackingService {

    def deviceService
    def jobManagerService
    def quartzScheduler
    def brokerMessagingTemplate

    def init(){
        synchronized (deviceService){
            // start all tracking job
            schedule( Device.findAllByEnabled(true) )
        }
    }
    TrackingHistory track(Device device, Object raw, Object correctedValue){
        def history

        def type = device.metaInfo.type
        def measurementsModel

        if( raw instanceof Double || Double.isAssignableFrom(type) ){
            measurementsModel = TrackingHistoryDouble
        }
        if(measurementsModel){
            history = measurementsModel.create()
        }
        history.device = device
        history.raw = raw
        history.value = correctedValue


        if(history){
            if( !history.save() ){
                StringBuilder sb = new StringBuilder()
                if( history.hasErrors() ) {
                    history.errors.each { sb << "\n$it" }
                }
                log.error "Cannot save tracking history for \"${device.name}\" (addr: \"${device.addr}\"). $sb"
            }
        }
        return history
    }

    @grails.events.Listener(topic = "disableDevices")
    def unschedule(List<Device> devices){
        int count = 0

        Map<String, TriggerDescriptor> triggersMap = getTrackingJobTriggers()
        devices.each {
            TriggerDescriptor triggerDescriptor = triggersMap.get(it.id.toString())
            if(triggerDescriptor){
                // remove device tracking
                quartzScheduler.unscheduleJob(triggerDescriptor.trigger.key)
                count++
            }
        }

        if(count > 0){
            String msg = "${devices.size()} devices were removed from tracking"
            log.info msg
            brokerMessagingTemplate.convertAndSend "/topic/logs", [date: new Date(), level: 'danger', msg: msg]
        }
    }

    @grails.events.Listener(topic = "enableDevices newDevices")
    void schedule(Collection<Device> devices){
        int count = 0
        int rescheduledCount = 0

        Map<String, TriggerDescriptor> triggersMap = getTrackingJobTriggers()
        devices.each {
            if(it.enabled && it.frequencyOfMeasurements && it.tracked) {
                TriggerDescriptor triggerDescriptor = triggersMap.get(it.id.toString())
                long frequencyOfMeasurements = it.frequencyOfMeasurements*1000 // convert seconds to milliseconds

                // check repeat interval
                boolean rescheduled = false
                if( triggerDescriptor ){
                    long repeatInterval = ((SimpleTrigger)triggerDescriptor.trigger).repeatInterval;
                    if( repeatInterval != frequencyOfMeasurements ){
                        quartzScheduler.unscheduleJob(triggerDescriptor.trigger.key)
                        triggerDescriptor = null
                        rescheduled = true
                    }
                }

                if( triggerDescriptor ){
                    log.warn("Device \"${it.addr}\" is already tracked")
                } else {

                    String jobName = "xxx" //but it should be TrackingJob.class.name, but doesn't work with a proper value. It looks like a but in the plugin
                    String jobGroup = "tracking"
                    long repeatInterval = frequencyOfMeasurements
                    int repeatCount = SimpleTrigger.REPEAT_INDEFINITELY

                    Trigger trigger = TriggerUtils.buildSimpleTrigger(jobName, jobGroup, repeatInterval, repeatCount)
                    trigger.description = it.id.toString() // set device ID to associate this trigger with device

                    TrackingJob.schedule(trigger, [device: it])

                    if(!rescheduled){
                        count++
                    } else {
                        rescheduledCount++
                    }
                }
            }
        }

        if(count > 0){
            String msg = "$count devices were added for tracking"
            log.info msg
            brokerMessagingTemplate.convertAndSend "/topic/logs", [date: new Date(), level: 'info', msg: msg]
        }


        if(rescheduledCount > 0){
            String msg = "$rescheduledCount device tracking jobs were rescheduled"
            log.info msg
            brokerMessagingTemplate.convertAndSend "/topic/logs", [date: new Date(), level: 'info', msg: msg]
        }
    }

    private Map<String, TriggerDescriptor> getTrackingJobTriggers(){
        Map<String, TriggerDescriptor> triggersMap = new HashMap<>()
        jobManagerService.getJobs("tracking").triggerDescriptors.each { triggerDescriptors ->
            triggerDescriptors.each {
                // parse device ID from trigger description
                String deviceId = it.trigger.description
                if( deviceId ){
                    triggersMap.put deviceId, it
                }
            }
        }
        return triggersMap
    }
}
