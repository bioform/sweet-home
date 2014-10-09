package sweethome.sensors;

import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.container.OneWireContainer;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import sweethome.HomeNet;
import sweethome.sensors.annotations.SupportedDevices;

import java.lang.reflect.Constructor;
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

    public Sensor get(String addr){
        OneWireContainer container = null;
        try {
            container = HomeNet.getDevice(addr);
        } catch (OneWireException e) {
            log.error("Error during retrieving device. " + e);
        }
        if (container == null) {
            return null;
        }
        Sensor sensor = get(container);
        return sensor;
    }

    public Sensor get(SensorMetaInfo meta, String addr){
        Sensor sensor = null;
        Constructor<Sensor> constructor = meta.getConstructor();
        if (constructor != null) {
            try {
                OneWireContainer container = HomeNet.getDevice(addr);
                if (container == null) {
                    return null;
                }
                sensor = constructor.newInstance(container);
            } catch (Exception e) {
                new RuntimeException("Cannot instantiate sensor", e);
            }
        }
        return sensor;
    }
    public Sensor get(OneWireContainer container){
        Sensor sensor = null;
        Constructor<Sensor> constructor = findConstructor(container.getClass().getName(), container.getName());
        if (constructor != null) {
            try {
                sensor = constructor.newInstance(container);
            } catch (Exception e) {
                new RuntimeException("Cannot instantiate sensor", e);
            }
        }
        return sensor;
    }

    public SensorMetaInfo getMetaInfo(String containerClassName, String name){
        SensorMetaInfo meta = null;
        Constructor<Sensor> constructor = findConstructor(containerClassName, name);
        if(constructor != null){
            meta = new SensorMetaInfo(constructor);
        }
        return meta;
    }

    private Constructor<Sensor> findConstructor(String containerClassName, String name){
        if(name == null){
            throw new NullPointerException("Device \"name\" cannot be null");
        }
        Constructor<Sensor> constructor = null;
        // find by device name
        Class clazz = deviceMap.get(name);
        if(clazz != null){
            try {
                Class parameterClass = Class.forName(containerClassName);
                constructor = ConstructorUtils.getMatchingAccessibleConstructor(clazz, new Class[]{parameterClass});
            } catch (ClassNotFoundException e) {
                log.error("Cannot find class for container \""+containerClassName+"\"");
            }
        }

        // find by class name
        if(constructor == null){
            constructor = constructorMap.get(containerClassName);
        }
        return constructor;
    }

    private void init() {
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
