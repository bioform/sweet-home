package sweethome.sensors;

public interface Sensor {

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
     * Close connection to adapter
     */
    public void close();

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
