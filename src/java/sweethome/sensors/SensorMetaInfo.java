package sweethome.sensors;

import sweethome.sensors.annotations.Units;

import java.lang.reflect.Constructor;

public class SensorMetaInfo {
    private final Units units;
    private final Constructor<Sensor> constructor;

    public SensorMetaInfo(Constructor<Sensor> constructor) {
        Class<Sensor> clazz = constructor.getDeclaringClass();
        this.constructor = constructor;
        this.units = clazz.getAnnotation(Units.class);
    }

    public Units getUnits() {
        return units;
    }

    public Constructor<Sensor> getConstructor() {
        return constructor;
    }
}
