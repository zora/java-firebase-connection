package thermostats.manager;

import thermostats.base.Log;
import thermostats.bridge.FirebaseBridge;
import thermostats.bridge.IOBridge;
import thermostats.model.SensorDataModel;
import thermostats.model.SensorEventModel;
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
public abstract class AbstractSensorManager implements Observer, FirebaseBridge.SensorEventListener {

    private static final String TAG = "sensor";
    private static final long INIT_DELAY = 2000;
    private static final long POLL_PERIOD = 15000;

    private FirebaseBridge mFirebaseBridge;
    private IOBridge mIOBridge;
    private final Map<String, SensorModel> mManagedSensors;
    private final ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);

    public AbstractSensorManager(FirebaseBridge firebaseBridge, IOBridge ioBridge) {
        mFirebaseBridge = firebaseBridge;
        mIOBridge = ioBridge;
        mManagedSensors = new HashMap<>();
    }

    public void initialize() {
        Log.d(TAG, "Initializing Sensor of type: " + getSensorType());
        mFirebaseBridge.addSensorChangeListener(new FirebaseBridge.SensorChangeListener() {
            @Override
            public void onSensorAdded(FirebaseBridge.Snapshot<SensorModel> sensorSnapshot) {
                SensorModel sensor = sensorSnapshot.getSnapshot();
                if (sensor.type == getSensorType()) {
                    if (!mManagedSensors.containsKey(sensor.id)) {
                        Log.d(TAG, "Sensor Added:" + sensor);
                        mManagedSensors.put(sensor.id, sensor);
                        mFirebaseBridge.addSensorEventListener(sensor.id, AbstractSensorManager.this);
                    }

                    sensorUpdated(sensor);
                    sensorSnapshot.addObserver(AbstractSensorManager.this);
                }
            }

            @Override
            public void onSensorRemoved(String sensorID) {
                mManagedSensors.remove(sensorID);
            }
        });

        mScheduler.scheduleAtFixedRate(() -> {
            synchronized (mManagedSensors) {
                for (SensorModel model : mManagedSensors.values()) {
                    if (model.active) {
                        SensorDataModel freshData = mIOBridge.getSensorData(model);
                        if (freshData != null && freshData.isValid()) {
                            receivedDataFromSensor(model, freshData);
                        } else {
                            // TODO: Add error log for invalid sensor data.
                        }
                    }
                }
            }
        }, INIT_DELAY, POLL_PERIOD, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onSensorEventReceived(String sensorID, SensorEventModel sensorEvent) {
        Log.d(TAG, String.format("%s received sensor event: %s",sensorID, sensorEvent));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof SensorModel) {
            SensorModel sensor = (SensorModel) arg;
            if (sensor.type == getSensorType()) {
                sensorUpdated(sensor);
            }
        }
    }

    /**
     * Called when the Manager is notified that the sensor has been updated.
     *
     * @param sensor
     */
    protected void sensorUpdated(SensorModel sensor) {
        // TODO: Verify sensor after update.
    }

    protected abstract SensorModel.SensorType getSensorType();

    protected void receivedDataFromSensor(SensorModel sensor, SensorDataModel freshData) {
        publishNewSensorData(sensor, freshData);
    }

    protected void publishNewSensorData(SensorModel sensorModel, SensorDataModel freshData) {
        mFirebaseBridge.publishSensorData(sensorModel, freshData);
    }
}
