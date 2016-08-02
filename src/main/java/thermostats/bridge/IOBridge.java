package thermostats.bridge;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import thermostats.model.SensorDataModel;
import thermostats.model.SensorModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zoranor on 2016-07-05.
 */
public class IOBridge {

    private Map<SensorModel.SensorType, String> mSensorScriptMap;
    private Gson mGson = new Gson();

    public IOBridge() {
        mSensorScriptMap = new HashMap<>();
        mSensorScriptMap.put(SensorModel.SensorType.temperature, "python3 scripts/temp.py");
        mSensorScriptMap.put(SensorModel.SensorType.humidity, "python3 scripts/humidity.py");
        mSensorScriptMap.put(SensorModel.SensorType.cpuUsage, "python3 scripts/device_cpu.py");
        mSensorScriptMap.put(SensorModel.SensorType.memoryUsage, "python3 scripts/device_memory.py");
        mSensorScriptMap.put(SensorModel.SensorType.diskUsage, "python3 scripts/device_disk.py");
    }

    public SensorDataModel getSensorData(SensorModel sensor) {
        try {
            Process p = Runtime.getRuntime().exec(mSensorScriptMap.get(sensor.type));
            return mGson.fromJson(new JsonReader(new InputStreamReader(p.getInputStream())), SensorDataModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
