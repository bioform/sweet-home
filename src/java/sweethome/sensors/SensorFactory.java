package sweethome.sensors;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.container.OneWireContainer;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import sweethome.Home;
import sweethome.sensors.annotations.SupportedDevices;
import sweethome.sensors.annotations.Units;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SensorFactory {
    private static Logger log = Logger.getLogger(SensorFactory.class);
    private final Map<String, Class> deviceMap = new ConcurrentHashMap<>();
    private final Map<String, Constructor<Sensor>> constructorMap = new ConcurrentHashMap<>();

    public SensorFactory(){
        init();
        log.info("SensorFactory was initialized");
    }

    public Sensor get(OneWireContainer container){
        return get(container.getClass().getName(), container.getName(), container.getAddressAsString());
    }

    /**
     * Try to find sensor by sensor name, if it wasn't found then try to find sensor by container class name
     * @param containerClassName
     * @param name
     * @param addr
     * @return sensor or null if it wasn't found
     */
    public Sensor get(String containerClassName, String name, String addr){
        Sensor sensor = null;

        Constructor<Sensor> constructor = findConstructor(containerClassName, name);
        if(constructor != null){
            try {
                OneWireContainer container = Home.getDevice(addr);
                if (container == null) {
                    return null;
                }
                sensor = constructor.newInstance(container);
            } catch (Exception e){
                new RuntimeException("Cannot instantiate sensor",e);
            }
        }

        return sensor;
    }

    public SensorMetaInfo getMetaInfo(String containerClassName, String name){
        SensorMetaInfo meta = null;
        Constructor<Sensor> constructor = findConstructor(containerClassName, name);
        if(constructor != null){
            meta = new SensorMetaInfo(constructor.getDeclaringClass());
        }
        return meta;
    }

    private Constructor<Sensor> findConstructor(String containerClassName, String name){
        Constructor<Sensor> constructor = getByDeviceName(containerClassName, name);
        if(constructor == null){
            constructor = getByClassName(containerClassName);
        }
        return constructor;
    }

    /**
     * Find sensor by its container class name
     * @param containerClassName - device container class name
     * @return sensor instance or null if device was not found
     */
    private Constructor<Sensor> getByClassName(String containerClassName){
        return constructorMap.get(containerClassName);
    }

    /**
     * Find sensor by its name
     * @param containerClassName - device container class name
     * @param name - device name
     * @return sensor instance or null if device was not found
     */
    private Constructor<Sensor> getByDeviceName(String containerClassName, String name){
        Constructor<Sensor> constructor = null;
        Class clazz = deviceMap.get(name);
        if(clazz != null){
            try {
                Class parameterClass = Class.forName(containerClassName);
                constructor = ConstructorUtils.getMatchingAccessibleConstructor(clazz, new Class[]{parameterClass});
            } catch (ClassNotFoundException e) {
                log.error("Cannot find class for container \""+containerClassName+"\"");
            }
        }
        return constructor;
    }

    /**
     * Check is device "read()" results makes sense
     * @param containerClassName - device container class name
     * @param name - device name
     * @return true if device sensor class annotated with "@Units(String)"
     */
    public boolean readable(String containerClassName, String name){
        boolean readable = false;
        try {
            Class clazz = (name != null) ? deviceMap.get(name) : null;
            if(clazz != null){
                readable = clazz.getAnnotation(Units.class) != null;
            } else {
                Constructor<Sensor> constructor = constructorMap.get(containerClassName);
                if (constructor != null) {
                    readable = constructor.getDeclaringClass().getAnnotation(Units.class) != null;
                }
            }
        } catch (Exception e){
            // do nothing
        }
        return readable;
    }

    private synchronized void init() {
        Reflections reflections = new Reflections("com.dalsemi.onewire.container");
        Set<Class<? extends OneWireContainer>> containerClasses = reflections.getSubTypesOf(OneWireContainer.class);

        reflections = new Reflections("sweethome.sensors");
        Set<Class<? extends Sensor>> sensorClasses = reflections.getSubTypesOf(Sensor.class);

        for(Class<? extends Sensor> clazz:sensorClasses){
            if( (clazz.getModifiers() & Modifier.ABSTRACT) != 0){
                continue;
            }
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
        }
    }
}
