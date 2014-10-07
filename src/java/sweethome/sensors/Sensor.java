package sweethome.sensors;

import java.io.Closeable;
import java.util.function.Consumer;

public interface Sensor extends Closeable {

    /**
     * Read raw and formatted value
     * @return
     */
    public Object read() throws Exception;

    /**
     * Write value
     * @param value
     */
    public void write(Object value);

    public String format(Object value);

    /**
     * Execute lambda and close sensor
     * @param lambda
     */
    public void withSensor(Consumer<Sensor> lambda);

}
