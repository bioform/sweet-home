package sweethome.sensors;

import sweethome.sensors.annotations.FrequencyOfMeasurements;
import sweethome.sensors.annotations.Units;

import java.lang.reflect.Constructor;

public class SensorMetaInfo {
    private final String units;
    private final Constructor<Sensor> constructor;
    private final Integer frequencyOfMeasurements;

    public SensorMetaInfo(Constructor<Sensor> constructor) {
        Class<Sensor> clazz = constructor.getDeclaringClass();
        this.constructor = constructor;

        final Units unitsAnn = clazz.getAnnotation(Units.class);
        this.units = (unitsAnn != null) ? unitsAnn.value() : null;

        final FrequencyOfMeasurements frequencyAnn = clazz.getAnnotation(FrequencyOfMeasurements.class);
        this.frequencyOfMeasurements = (frequencyAnn != null) ? frequencyAnn.value() : null;
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
}
