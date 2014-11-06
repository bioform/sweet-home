package sweethome.sensors;

import com.dalsemi.onewire.container.OneWireContainer;
import com.dalsemi.onewire.container.TemperatureContainer;
import sweethome.HomeNet;
import sweethome.sensors.annotations.FrequencyOfMeasurements;
import sweethome.sensors.annotations.SupportedDevices;
import sweethome.sensors.annotations.Units;

@Units({"°C","",""})
@SupportedDevices({"DS18S20", "DS18B20"})
@FrequencyOfMeasurements(60*60)
public class TemperatureSensor implements Sensor {
    // constant for temperature display option
    static final int CELSIUS    = 0x01;
    static final int FAHRENHEIT = 0x02;

    // temperature display mode
    private int tempMode = CELSIUS;

    private final TemperatureContainer tc;

    public TemperatureSensor(TemperatureContainer container){
        this.tc = container;
    }

    @Override
    public Double read() throws Exception {
        byte[] state = tc.readDevice();
        double lastTemp;
        // perform a temperature conversion
        tc.doTemperatureConvert(state);

        // read the result of the conversion
        state = tc.readDevice();

        // extract the result out of state
        lastTemp = tc.getTemperature(state);

        if (tempMode == FAHRENHEIT)
            lastTemp = convertToFahrenheit(lastTemp);

        return (( int ) (lastTemp * 100)) / 100.0;
    }

    @Override
    public void close() {
        HomeNet.close((OneWireContainer) tc);
    }

    /** Convert a temperature from Celsius to Fahrenheit. */
    public static double convertToFahrenheit (double celsiusTemperature)
    {
        return ( double ) (celsiusTemperature * 9.0 / 5.0 + 32.0);
    }

    /** Convert a temperature from Fahrenheit to Celsius.  */
    public static double convertToCelsius (double fahrenheitTemperature)
    {
        return ( double ) ((fahrenheitTemperature - 32.0) * 5.0 / 9.0);
    }

    public static Double toDouble(Object val){
        if(val == null){
            return null;
        }
        else if(val instanceof  Double){
            return (Double)val;
        } else {
            return Double.valueOf(String.valueOf(val));
        }
    }
}
