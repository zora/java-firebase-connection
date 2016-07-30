package thermostats.bridge;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import thermostats.model.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Tim on 2016-07-21.
 */
public class FirebaseBridge {
    private String mDeviceID;



    // Device Snapshots
    private final Snapshot<DeviceModel> mDeviceSnapshot = new Snapshot<>(null);
    private final Snapshot<DeviceStatisticsModel> mDeviceStatisticsSnapshot = new Snapshot<>(null);
    private final Map<String, Snapshot<SensorModel>> mSensorSnapshots = new HashMap<>();

    private Set<String> mSensorsTracked;
    private Set<SensorChangeListener> mSensorListeners;
    private Map<String, Set<SensorEventListener>> mSensorEventListeners;

    // Database References
    private DatabaseReference mDeviceRef;
    private DatabaseReference mSensorsRef;
    private DatabaseReference mSensorDataRef;
    private DatabaseReference mDeviceStatisticsRef;
    private DatabaseReference mDeviceLogsRef;

    public class Snapshot<T> extends Observable {
        private T snapshot;
        public Snapshot(T snapshot) {
            this.snapshot = snapshot;
        }

        public T getSnapshot() {
            return snapshot;
        }

        public void setSnapshot(T snapshot) {
            this.snapshot = snapshot;
            setChanged();
            notifyObservers(snapshot);
        }
    }

    public FirebaseBridge(String deviceID) {
        mSensorsTracked = new HashSet<>();
        mSensorListeners = new HashSet<>();
        mSensorEventListeners = new HashMap<>();
        mDeviceID = deviceID;
    }

    public void initialize() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream("serviceAccountCredentials.json"))
                    .setDatabaseUrl("https://javaconnectedthermostats.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);
            setupRootReferences();
            setupDevice();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setupRootReferences() {
        mDeviceRef = FirebaseDatabase.getInstance().getReference("devices").child(mDeviceID);
        mSensorsRef = FirebaseDatabase.getInstance().getReference("sensors");
        mSensorDataRef = FirebaseDatabase.getInstance().getReference("sensorData");
        mDeviceLogsRef = FirebaseDatabase.getInstance().getReference("logs").child(mDeviceID);
        mDeviceStatisticsRef = FirebaseDatabase.getInstance().getReference("deviceStatistics").child(mDeviceID);
    }

    private void setupDevice() {
        mDeviceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    DeviceModel device = dataSnapshot.getValue(DeviceModel.class);
                    mDeviceSnapshot.setSnapshot(device);
                    if (device != null && device.sensorIDs != null && !device.sensorIDs.isEmpty()) {

                        // Check for added sensors.
                        for (String sensorID : device.sensorIDs) {
                            if (!mSensorsTracked.contains(sensorID)) {
                                mSensorsTracked.add(sensorID);
                                setupNewSensor(sensorID);
                            }
                        }

                        // Check for Removed sensors.
                        for(String sensorID : mSensorsTracked) {
                            if(!device.sensorIDs.contains(sensorID)) {
                                mSensorsTracked.remove(sensorID);
                                removeSensor(sensorID);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void setupNewSensor(String sensorID) {
        DatabaseReference newSensor = mSensorsRef.child(sensorID);
        newSensor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    SensorModel model = dataSnapshot.getValue(SensorModel.class);
                    if (model != null) {
                        Snapshot<SensorModel> sensorSnapshot = mSensorSnapshots.get(sensorID);
                        if(sensorSnapshot != null) {
                            sensorSnapshot.setSnapshot(model);
                        } else {
                            sensorSnapshot = new Snapshot<>(model);
                            mSensorSnapshots.put(sensorID, sensorSnapshot);
                            for(SensorChangeListener listener : mSensorListeners) {
                                listener.onSensorAdded(sensorSnapshot);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference sensorData = mSensorDataRef.child(sensorID);
        sensorData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // New Sensor Data event received from sensorID.
                if(dataSnapshot == null) {
                    return;
                }

                SensorEventModel event = dataSnapshot.getValue(SensorEventModel.class);
                Set<SensorEventListener> interestedListeners = mSensorEventListeners.get(sensorID);
                if(event != null && interestedListeners != null && !interestedListeners.isEmpty()) {
                    for(SensorEventListener listener : interestedListeners) {
                        listener.onSensorEventReceived(sensorID, event);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeSensor(String sensorID) {
        Snapshot<SensorModel> sensor = mSensorSnapshots.get(sensorID);
        if(sensor != null && sensor.getSnapshot() != null) {
            sensor.setSnapshot(null);
        }

        if(sensor != null) {
            sensor.deleteObservers();
        }

        mSensorSnapshots.remove(sensorID);
        for(SensorChangeListener listener : mSensorListeners) {
            listener.onSensorRemoved(sensorID);
        }
    }

    public interface SensorChangeListener {
        void onSensorAdded(Snapshot<SensorModel> sensorSnapshot);
        void onSensorRemoved(String sensorID);
    }

    public interface SensorEventListener {
        void onSensorEventReceived(String sensorID, SensorEventModel sensorEvent);
    }

    public void addSensorChangeListener(SensorChangeListener listener) {
        mSensorListeners.add(listener);
    }

    public void removeSensorChangeListener(SensorChangeListener listener) {
        mSensorListeners.remove(listener);
    }

    public void addSensorEventListener(String sensorID, SensorEventListener listener) {
        if(!mSensorEventListeners.containsKey(sensorID)) {
            mSensorEventListeners.put(sensorID, new HashSet<>());
        }

        mSensorEventListeners.get(sensorID).add(listener);
    }

    public void removeSensorEventListener(String sensorID, SensorEventListener listener) {
        if(mSensorEventListeners.containsKey(sensorID)) {
            mSensorEventListeners.get(sensorID).remove(listener);
        }
    }

    public Map<String, Snapshot<SensorModel>> getSensorSnapshots() {
        return mSensorSnapshots;
    }

    public Snapshot<DeviceModel> getDeviceSnapshot() {
        return mDeviceSnapshot;
    }

    public void publishSensorData(SensorModel sensor, SensorDataModel freshData) {
        DatabaseReference ref = mSensorDataRef.child(sensor.id);
        String eventKey = ref.push().getKey();

        long timestamp = System.currentTimeMillis();
        ref.child(eventKey).setValue(new SensorEventModel(timestamp, freshData.value));
    }

    public void publishLogCall(String tag, LogEventModel logEvent) {
        DatabaseReference ref = mDeviceLogsRef.child(tag);
        String eventKey = ref.push().getKey();
        ref.child(eventKey).setValue(logEvent);
    }
}
