package sweethome

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.web.json.JSONObject

class ApplicationController {

    def grailsApplication

    protected void assignAttributes(item, params){
        def domainDescriptor = grailsApplication.getArtefact(DomainClassArtefactHandler.TYPE, item.class.name)
        for(String param:params){
            if(request.JSON.containsKey(param)){
                def property = domainDescriptor.getPropertyByName(param)
                def type = property.getType()

                def val
                if( Integer.isAssignableFrom(type) ) {
                    val = jsonInt(param)
                } else if( boolean.isAssignableFrom(type) ){
                    val = !!request.JSON."$param"
                } else {
                    val = request.JSON."$param"
                }
                item."$param" = JSONObject.NULL.equals(val) ? null:val
            }
        }
    }

    private Integer jsonInt(param){
        def val = request.JSON."$param"
        if( !(val instanceof Integer) ){
            val = val && val.isInteger() ? val.toInteger() :  null
        }
        return val
    }
}
