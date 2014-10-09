package sweethome

import grails.converters.JSON
import grails.plugins.quartz.JobDescriptor

class JobDescriptorMarshaller {
    void register() {
        JSON.registerObjectMarshaller( JobDescriptor ) { JobDescriptor job ->
            List triggers = job.triggerDescriptors.collect {
                [
                    name: it.name,
                    state: it.state.name(),
                    desc: it.trigger.description,
                    previousFireTime: it.trigger.previousFireTime,
                    nextFireTime: it.trigger.nextFireTime
                ]
            }
            return [
                    group: job.group,
                    name: job.name,
                    desc: job.jobDetail.description,
                    triggers: triggers

            ]
        }
    }
}
