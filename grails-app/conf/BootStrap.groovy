import org.springframework.web.context.support.WebApplicationContextUtils

import sweethome.HomeNet
import sweethome.OneWireServer

class BootStrap {

    def sensorFactory
    private OneWireServer oneWireServer
    private Thread oneWireServerThread

    def init = { servletContext ->
        oneWireServer = new OneWireServer()
        oneWireServerThread = new Thread(oneWireServer)
        oneWireServerThread.start()

        // Get spring
        def springContext = WebApplicationContextUtils.getWebApplicationContext( servletContext )

        // Custom marshalling
        springContext.getBean( "customObjectMarshallers" ).register()
    }
    def destroy = {
        if(oneWireServer){
            oneWireServerThread.interrupt()
            oneWireServer.cleanup()
        }
    }


}
