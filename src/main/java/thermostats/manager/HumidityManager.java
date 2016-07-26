package thermostats.manager;

import thermostats.bridge.FirebaseBridge;
import thermostats.bridge.IOBridge;
import thermostats.model.SensorModel;

/**
 * Created by Tim on 2016-07-26.
 */
public class HumidityManager extends AbstractSensorManager {

    public HumidityManager(FirebaseBridge firebaseBridge, IOBridge ioBridge) {
        super(firebaseBridge, ioBridge);
    }

    @Override
    protected SensorModel.SensorType getSensorType() {
        return SensorModel.SensorType.humidity;
    }
}
