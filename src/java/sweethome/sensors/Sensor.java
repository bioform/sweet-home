package sweethome.sensors;

import java.io.Closeable;
import java.util.function.Consumer;

public interface Sensor extends Closeable {

    /**
     * Read raw and formatted value
     * @return
     */
    public Object read() throws Exception;

}
