package sweethome.sensors;

import com.dalsemi.onewire.container.OneWireContainer;

public class SensorFactory {
    public static Sensor get(OneWireContainer container){
        // we can have basic Actor for provided container class or
        // for container name (container.getName()). The last one has highest priority.
        return null;
    }
    public static boolean readable(String containerClass, String name){
        return true;
    }

}
