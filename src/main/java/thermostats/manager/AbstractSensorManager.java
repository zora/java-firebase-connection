package thermostats.manager;

import thermostats.bridge.FirebaseBridge;
import thermostats.bridge.IOBridge;
import thermostats.model.SensorDataModel;
import thermostats.model.SensorModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tim on 2016-07-22.
 */
public abstract class AbstractSensorManager implements Observer {

    private static final long INIT_DELAY = 2000;
    private static final long POLL_PERIOD = 5000;

    private FirebaseBridge mFirebaseBridge;
    private IOBridge mIOBridge;
    private final Map<String, SensorModel> mSensorSnapshots;
    private final ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);

    public AbstractSensorManager(FirebaseBridge firebaseBridge, IOBridge ioBridge) {
        mFirebaseBridge = firebaseBridge;
        mIOBridge = ioBridge;
        mSensorSnapshots = new HashMap<>();
    }

    public void initialize() {
        mFirebaseBridge.addObserver(this);

        mScheduler.scheduleAtFixedRate(() -> {
            synchronized (mSensorSnapshots) {
                for (SensorModel model : mSensorSnapshots.values()) {
                    if(model.active) {
                        SensorDataModel freshData = mIOBridge.getSensorData(model);
                        if(freshData != null && freshData.isValid()) {
                            receivedNewSensorData(model, freshData);
                        } else {
                            // TODO: Add error log for invalid sensor data.
                        }
                    }
                }
            }
        }, INIT_DELAY, POLL_PERIOD, TimeUnit.MILLISECONDS);
    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof SensorModel) {
            SensorModel updated = (SensorModel) arg;

            if (updated.type == getSensorType()) {

                synchronized (mSensorSnapshots) {
                    if (!mSensorSnapshots.containsKey(updated.id)) {
                        setupNewSensor(updated);
                    } else {
                        sensorUpdated(updated);
                    }
                    mSensorSnapshots.put(updated.id, updated);
                }
            }
        }
    }

    protected void setupNewSensor(SensorModel sensor) {}
    protected void sensorUpdated(SensorModel sensor) {}

    protected abstract SensorModel.SensorType getSensorType();

    protected void receivedNewSensorData(SensorModel sensor, SensorDataModel freshData) {
        publishNewSensorData(sensor, freshData);
    }

    protected void publishNewSensorData(SensorModel sensorModel, SensorDataModel freshData) {
        mFirebaseBridge.publishSensorData(sensorModel, freshData);
    }
}
