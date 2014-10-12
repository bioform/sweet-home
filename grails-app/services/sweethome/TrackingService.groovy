package sweethome

import grails.plugins.quartz.TriggerDescriptor
import grails.plugins.quartz.TriggerUtils
import grails.transaction.Transactional
import org.quartz.SimpleTrigger
import org.quartz.Trigger

import javax.annotation.PostConstruct

@Transactional
class TrackingService {

    def deviceService
    def jobManagerService
    def quartzScheduler

    def init(){
        synchronized (deviceService){
            // start all tracking job
            schedule( Device.findAllByEnabled(true) )
        }
    }

    @grails.events.Listener
    def newDevices(List devices){
        log.debug "${devices.size()} devices was added"
        schedule devices
    }

    @grails.events.Listener
    def enableDevices(List<Device> devices){
        log.debug "${devices.size()} devices was enabled"
        schedule devices
    }

    @grails.events.Listener
    def disableDevices(List<Device> devices){
        int count = 0
        log.debug "${devices.size()} devices was disabled"
        Map<String, TriggerDescriptor> triggersMap = getTrackingJobTriggers()
        devices.each {
            TriggerDescriptor triggerDescriptor = triggersMap.get(it.id.toString())
            if(triggerDescriptor){
                // remove device tracking
                quartzScheduler.unscheduleJob(triggerDescriptor.trigger.key)
                count++
            }
        }
        log.info "$count devices was removed from tracking (was: ${triggersMap.size()})"
    }

    private void schedule(Collection<Device> devices){
        int count = 0
        Map<String, TriggerDescriptor> triggersMap = getTrackingJobTriggers()
        devices.each {
            if(it.enabled && it.frequencyOfMeasurements && it.tracked) {
                if( triggersMap.containsKey( it.id.toString() ) ){
                    log.warn("Device \"${it.addr}\" is already tracked")
                } else {

                    String jobName = "xxx" //but it should be TrackingJob.class.name, but doesn't work with a proper value. It looks like a but in the plugin
                    String jobGroup = "tracking"
                    long repeatInterval = it.frequencyOfMeasurements*1000 // convert seconds to milliseconds
                    int repeatCount = SimpleTrigger.REPEAT_INDEFINITELY

                    Trigger trigger = TriggerUtils.buildSimpleTrigger(jobName, jobGroup, repeatInterval, repeatCount)
                    trigger.description = it.id.toString() // set device ID to associate this trigger with device

                    TrackingJob.schedule(trigger, [device: it])
                    count++
                }
            }
        }
        log.info "$count devices was added to tracking (was: ${triggersMap.size()})"
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
