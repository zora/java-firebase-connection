package thermostats.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Tim on 2016-07-21.
 */
public class DeviceModel implements Serializable {
    public String name;
    public String location;
    public int logLevel;
    public List<String> sensorIDs;

    @Override
    public String toString() {
        return "DeviceModel{" +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", sensorIDs=" + sensorIDs +
                '}';
    }
}
