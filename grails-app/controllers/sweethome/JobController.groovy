package sweethome

import grails.converters.JSON
import grails.plugins.quartz.JobManagerService

class JobController {

    def jobManagerService

    def index() {
        def jobs = jobManagerService.getAllJobs()
        render jobs as JSON
    }
}
