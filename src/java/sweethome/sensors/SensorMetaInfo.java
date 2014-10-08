package sweethome.sensors;

import sweethome.sensors.annotations.Units;

public class SensorMetaInfo {
    private final Units units;

    public SensorMetaInfo(Class<Sensor> clazz) {
        this.units = clazz.getAnnotation(Units.class);
    }

    public Units getUnits() {
        return units;
    }
}
