package thermostats.model;

import java.io.Serializable;

/**
 * Created by Tim on 2016-07-21.
 */
public class SensorModel implements Serializable {
    public String id;
    public SensorType type;
    public boolean active;

    public enum SensorType {
        temperature, humidity
    }

    @Override
    public String toString() {
        return "SensorModel{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", active=" + active +
                '}';
    }
}
