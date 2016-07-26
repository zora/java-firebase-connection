package thermostats.model;

import java.io.Serializable;

/**
 * Created by Tim on 2016-07-26.
 */
public class LogEventModel implements Serializable{
    public long timestamp;
    public int level;
    public String data;

    public LogEventModel() {}
    public LogEventModel(long timestamp, int level, String data) {
        this.timestamp = timestamp;
        this.level = level;
        this.data = data;
    }
}
