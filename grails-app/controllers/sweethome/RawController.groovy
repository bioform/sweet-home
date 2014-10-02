package sweethome

import com.dalsemi.onewire.container.OneWireContainer

class RawController {

    def index() {
        Collection<OneWireContainer> oneWireContainers = Home.devices
        def devices = oneWireContainers.collect {
            [name: it.name, addr: it.addressAsString, desc: it.description]
        }

        render(contentType: "text/json") {devices}
    }
}
