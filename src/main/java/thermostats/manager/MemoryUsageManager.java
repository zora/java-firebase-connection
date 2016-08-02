package thermostats.manager;

import thermostats.bridge.FirebaseBridge;
import thermostats.bridge.IOBridge;
import thermostats.model.SensorModel;

/**
 * Created by Tim on 2016-08-02.
 */
public class MemoryUsageManager extends AbstractSensorManager {

    public MemoryUsageManager(FirebaseBridge firebaseBridge, IOBridge ioBridge) {
        super(firebaseBridge, ioBridge);
    }

    @Override
    protected SensorModel.SensorType getSensorType() {
        return SensorModel.SensorType.memoryUsage;
    }
}
