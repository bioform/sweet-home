package sweethome.sensors.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Units {

    /**
     * For boolean typed sensor you need to specify true and false units, for all other types - only one unit
     * @return
     */
    String[] value();
}
