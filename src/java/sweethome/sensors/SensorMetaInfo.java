package sweethome.sensors;

import sweethome.sensors.annotations.FrequencyOfMeasurements;
import sweethome.sensors.annotations.Units;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SensorMetaInfo {
    private final String units;
    private final Constructor<Sensor> constructor;
    private final Integer frequencyOfMeasurements;
    private final Class type;

    public SensorMetaInfo(Constructor<Sensor> constructor) {
        Class<Sensor> clazz = constructor.getDeclaringClass();
        this.constructor = constructor;

        final Units unitsAnn = clazz.getAnnotation(Units.class);
        this.units = (unitsAnn != null) ? unitsAnn.value() : null;

        final FrequencyOfMeasurements frequencyAnn = clazz.getAnnotation(FrequencyOfMeasurements.class);
        this.frequencyOfMeasurements = (frequencyAnn != null) ? frequencyAnn.value() : null;
        try {
            this.type = clazz.getMethod("read").getReturnType();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot find mandatory method \"read()\"", e);
        }
    }

    public String getUnits() {
        return units;
    }

    public Integer getFrequencyOfMeasurements() {
        return frequencyOfMeasurements;
    }

    public Constructor<Sensor> getConstructor() {
        return constructor;
    }

    public Method[] getMethods() {
        return constructor.getDeclaringClass().getMethods();
    }

    public Class getType() {
        return type;
    }
}
