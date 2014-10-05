package sweethome.sensors;

import java.io.Closeable;
import java.util.function.Consumer;

public interface Sensor extends Closeable {

    /**
     * Read raw and formatted value
     * @return
     */
    public Readings read() throws Exception;

    /**
     * Write value
     * @param value
     */
    public void write(Object value);

    /**
     * Execute lambda and close sensor
     * @param lambda
     */
    public void runAndClose(Consumer<Sensor> lambda);

    public class Readings {
        public Object val;
        public String formatted;

        public Readings(Object val, String formatted){
            this.val = val;
            this.formatted = formatted;
        }

        public Object val(){
            return val;
        }
        public String formatted(){
            return formatted;
        }

    }
}
