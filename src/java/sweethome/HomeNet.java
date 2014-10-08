package sweethome;

import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;

public class HomeNet {
    private final static Logger log = Logger.getLogger(HomeNet.class);

    public static Collection<OneWireContainer> getContainers() throws OneWireException {
        Collection<OneWireContainer> list = new LinkedList();
        DSPortAdapter adapter = getAdapter();
        try {
            // get exclusive use of adapter
            adapter.beginExclusive(true);
            adapter.setSearchAllDevices();
            adapter.targetAllFamilies();
            adapter.setSpeed(adapter.SPEED_REGULAR);

            for (Enumeration owc_enum = adapter.getAllDeviceContainers();
                 owc_enum.hasMoreElements(); ) {
                // get the next owc
                list.add((OneWireContainer) owc_enum.nextElement());
            }
        } finally {
            close(adapter);
        }

        return list;
    }

    public static OneWireContainer getDevice(String addr) throws OneWireException {
        DSPortAdapter adapter = getAdapter();
        try {
            return adapter.getDeviceContainer(addr);
        } catch(Exception e) {
            close(adapter);
            throw e;
        }

    }

    private static DSPortAdapter getAdapter() throws OneWireException {
        NetAdapter adapter = new NetAdapter();
        adapter.selectPort("127.0.0.1");
        return adapter;
    }

    public static void close(DSPortAdapter adapter) {
        adapter.endExclusive();
        try {
            adapter.freePort();
        } catch (OneWireException e) {
            // close quiet
        }
    }

    public static void close(OneWireContainer container) {
        DSPortAdapter adapter = container.getAdapter();
        adapter.endExclusive();
        try {
            adapter.freePort();
        } catch (OneWireException e) {
            // close quiet
        }
    }

}