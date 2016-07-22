package thermostats.bridge.mock;

import thermostats.bridge.IOBridge;
import thermostats.model.SensorDataModel;
import thermostats.model.SensorModel;

import java.util.Random;

/**
 * Created by Tim on 2016-07-21.
 */
public class MockIOBridge extends IOBridge {

    @Override
    public SensorDataModel getSensorData(SensorModel sensor) {
        SensorDataModel model = new SensorDataModel();

        if(sensor.type == SensorModel.SensorType.temperature) {
            model.value = (new Random().nextFloat()) * 50.0f;
        } else if (sensor.type == SensorModel.SensorType.humidity){
            model.value = 75.0f;
        }

        return model;
    }
}
