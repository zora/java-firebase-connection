package thermostats.bridge;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import thermostats.model.SensorDataModel;
import thermostats.model.SensorModel;
import thermostats.model.TemperatureModel;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Zoranor on 2016-07-05.
 */
public class IOBridge {
    private Gson gson = new Gson();

    public SensorDataModel getSensorData(SensorModel sensor) {
        try {
            Process p = Runtime.getRuntime().exec("python3 ../retrieveData.py");
            return gson.fromJson(new JsonReader(new InputStreamReader(p.getInputStream())), SensorDataModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public TemperatureModel getTemperature() {
        try {
            Process p = Runtime.getRuntime().exec("python3 ../temp.py");
            return gson.fromJson(new JsonReader(new InputStreamReader(p.getInputStream())), TemperatureModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
