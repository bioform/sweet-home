package sweethome.sensors;


import com.dalsemi.onewire.container.OneWireContainer;
import sweethome.HomeNet;

import java.util.function.Consumer;

public abstract class AbstractSensor implements Sensor{

    protected abstract OneWireContainer getContainer();

    @Override
    public void withSensor(Consumer<Sensor> lambda) {
        try {
            lambda.accept(this);
        } finally {
            HomeNet.close(getContainer());
        }
    }

    @Override
    public void close() {
        HomeNet.close(getContainer());
    }

}
