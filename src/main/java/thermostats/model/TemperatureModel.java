package thermostats.model;

import java.io.Serializable;

/**
 * Created by Tim on 2016-07-21.
 */
public class TemperatureModel implements Serializable {
    public String error;
    public float calcTemp;
    public float rawTemp;

    public boolean isValid() {
        return error == null || error.isEmpty();
    }
}
