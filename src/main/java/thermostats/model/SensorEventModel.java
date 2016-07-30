package thermostats.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Tim on 2016-07-22.
 */
public class SensorEventModel implements Serializable{
    public long timestamp;
    public float value;

    public SensorEventModel() {}
    public SensorEventModel(long timestamp, float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public String toString() {
        return "[SensorEvent - Time: " + new Date(timestamp) + " Value:" + value + "]";
    }
}
