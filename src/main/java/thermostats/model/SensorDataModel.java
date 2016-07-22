package thermostats.model;

import java.io.Serializable;

/**
 * Created by Tim on 2016-07-21.
 */
public class SensorDataModel implements Serializable {
    public String error;
    public float value;

    public boolean isValid() {
        return error == null || error.isEmpty();
    }
}
