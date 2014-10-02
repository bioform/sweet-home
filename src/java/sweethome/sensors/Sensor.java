package sweethome.sensors;

public interface Sensor {
    public Object get();
    public String getFormatted();
    public void set(Object value);
}
