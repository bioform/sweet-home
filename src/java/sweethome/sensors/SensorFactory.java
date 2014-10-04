package sweethome.sensors;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.container.OneWireContainer;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import sweethome.Home;
import sweethome.sensors.annotations.SupportedDevices;
import sweethome.sensors.annotations.Units;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SensorFactory {
    private static Logger log = Logger.getLogger(SensorFactory.class);
    private final static Map<String, Class> deviceMap = new ConcurrentHashMap<>();
    private final static Map<String, Constructor<Sensor>> constructorMap = new ConcurrentHashMap<>();

    public static Sensor get(OneWireContainer container){
        return getByClassName(container.getClass().getName(), container.getAddressAsString());
    }
    public static Sensor getByClassName(String containerClassName, String addr){
        Sensor sensor = null;
        try {
            Constructor<Sensor> constructor = constructorMap.get(containerClassName);
            if (constructor != null) {
                sensor = constructor.newInstance(Home.getDevice(addr));
            }
        } catch (Exception e){
            new RuntimeException("Cannot instantiate sensor",e);
        }
        return sensor;
    }

    public static Sensor getByDeviceName(String name){
        //TODO implement "getByDeviceName"
        return null;
    }
    public static boolean readable(String containerClassName, String name){
        boolean readable = false;
        try {
            Constructor<Sensor> constructor = constructorMap.get(containerClassName);
            if (constructor != null) {
                readable = constructor.getAnnotation(Units.class) != null;
            }
        } catch (Exception e){
            new RuntimeException("Cannot instantiate sensor",e);
        }
        return readable;
    }

    public static synchronized void init() {
        Reflections reflections = new Reflections("com.dalsemi.onewire.container");
        Set<Class<? extends OneWireContainer>> containerClasses = reflections.getSubTypesOf(OneWireContainer.class);

        reflections = new Reflections("sweethome.sensors");
        Set<Class<? extends Sensor>> sensorClasses = reflections.getSubTypesOf(Sensor.class);

        for(Class<? extends Sensor> clazz:sensorClasses){

            // check all constructors
            boolean hasAppropriateConstructor = false;
            Constructor[] constructors = clazz.getConstructors();
            for (Constructor constructor : constructors) {
                Class[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length != 1) {
                    continue;
                }
                Class<?> parameterType = parameterTypes[0];
                // find appropriate types
                for(Class checkType:containerClasses){
                    if(parameterType.isAssignableFrom(checkType)){
                        constructorMap.put(checkType.getName(), constructor);
                        hasAppropriateConstructor = true;
                    }
                }
            }

            if( !hasAppropriateConstructor ){
                String name = clazz.getSimpleName();
                log.error("Sensor class\""+name+"\" doesn't have appropriate constructor \"public "+name+"(<SubClass_of_OneWireContainer> container)\"");
                continue;
            } else {
                // add root class... may be it is useless...
                try {
                    Constructor constructor = clazz.getConstructor(OneWireContainer.class);
                    constructorMap.put(OneWireContainer.class.getName(), constructor);
                } catch (NoSuchMethodException e) {
                    // it is OK
                }
            }


            SupportedDevices supportedDevices=clazz.getAnnotation(SupportedDevices.class);
            if(supportedDevices != null) {
                String[] names = supportedDevices.value();
                if (names != null) for (String name : names) {
                    deviceMap.put(name, clazz);
                }
            }

            System.out.println("mmmm");
        }
    }
}
