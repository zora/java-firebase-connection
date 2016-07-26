package thermostats.manager;

import thermostats.bridge.FirebaseBridge;
import thermostats.bridge.IOBridge;
import thermostats.model.SensorModel;

import java.util.Observer;

/**
 * Created by Tim on 2016-07-22.
 */
public class TemperatureManager extends AbstractSensorManager implements Observer {

    public TemperatureManager(FirebaseBridge firebaseBridge, IOBridge ioBridge) {
        super(firebaseBridge, ioBridge);
    }

    public void initialize() {
        super.initialize();
    }

    @Override
    protected SensorModel.SensorType getSensorType() {
        return SensorModel.SensorType.temperature;
    }
}
