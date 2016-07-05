import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Created by Zoranor on 2016-07-05.
 */
public class Bridge {
    private Gson gson = new Gson();

    public class TemperatureModel implements Serializable {
        public String error;
        public float calcTemp;
        public float rawTemp;

        public boolean isValid() {
            return error == null || error.isEmpty();
        }
    }

    public TemperatureModel getTemperature() {
        try {
            Process p = Runtime.getRuntime().exec("python ../temp.py");
            return gson.fromJson(new JsonReader(new InputStreamReader(p.getInputStream())), TemperatureModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
