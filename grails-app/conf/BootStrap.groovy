import org.springframework.web.context.support.WebApplicationContextUtils

import sweethome.Home
import sweethome.OneWireServer
import sweethome.sensors.SensorFactory

class BootStrap {

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

        // register sensors implementation
        SensorFactory.init()
    }
    def destroy = {
        if(oneWireServer){
            oneWireServerThread.interrupt()
            oneWireServer.cleanup()
        }
    }


}
