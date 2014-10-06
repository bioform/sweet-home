package sweethome.sensors;


import com.dalsemi.onewire.container.OneWireContainer;
import sweethome.Home;

import java.util.function.Consumer;

public abstract class AbstractSensor implements Sensor{

    protected abstract OneWireContainer getContainer();

    @Override
    public void runAndClose(Consumer<Sensor> lambda) {
        try {
            lambda.accept(this);
        } finally {
            Home.close( getContainer() );
        }
    }

    @Override
    public void close() {
        Home.close( getContainer() );
    }

}
