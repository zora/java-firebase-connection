package thermostats.bridge;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import thermostats.model.DeviceModel;
import thermostats.model.SensorDataModel;
import thermostats.model.SensorEventModel;
import thermostats.model.SensorModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Tim on 2016-07-21.
 */
public class FirebaseBridge extends Observable {
    private DeviceModel mDeviceSnapshot;
    private Set<String> mSensorsTracked;
    private DatabaseReference mDeviceReference;
    private DatabaseReference mSensorsReference;
    private DatabaseReference mSensorDataReference;
    private DatabaseReference mDeviceLogsReference;

    public FirebaseBridge() {
        mSensorsTracked = new HashSet<>();
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
        mDeviceReference = FirebaseDatabase.getInstance().getReference("devices").child("device1");
        mSensorsReference = FirebaseDatabase.getInstance().getReference("sensors");
        mSensorDataReference = FirebaseDatabase.getInstance().getReference("sensorData");
        mDeviceLogsReference = FirebaseDatabase.getInstance().getReference("deviceLogs");
    }

    private void setupDevice() {
        mDeviceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    mDeviceSnapshot = dataSnapshot.getValue(DeviceModel.class);
                    System.out.println("Device: " + mDeviceSnapshot);
                    if (mDeviceSnapshot != null && mDeviceSnapshot.sensorIDs != null && !mDeviceSnapshot.sensorIDs.isEmpty()) {
                        for (String sensorID : mDeviceSnapshot.sensorIDs) {
                            if (!mSensorsTracked.contains(sensorID)) {
                                mSensorsTracked.add(sensorID);
                                setupSensor(sensorID);
                            }
                        }
                    }

                    setChanged();
                    notifyObservers(mDeviceSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setupSensor(String sensorID) {
        DatabaseReference newSensor = mSensorsReference.child(sensorID);

        newSensor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    SensorModel model = dataSnapshot.getValue(SensorModel.class);
                    if (model != null) {
                        setChanged();
                        // Notify that we have a new or an updated sensor.
                        notifyObservers(model);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public DeviceModel getDeviceSnapshot() {
        return mDeviceSnapshot;
    }

    public void publishSensorData(SensorModel sensor, SensorDataModel freshData) {
        DatabaseReference ref = mSensorDataReference.child(sensor.id);
        String eventKey = ref.push().getKey();

        long timestamp = System.currentTimeMillis();
        ref.child(eventKey).setValue(new SensorEventModel(timestamp, freshData.value));
    }

    /*

    public void loadState() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sensor");
        DatabaseReference rawTempRef = ref.child("raw-temp");
        DatabaseReference calcTempRef = ref.child("calc-temp");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("From Server: " + dataSnapshot.getKey() + ": " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("onCancelled for ref:" + ref.toString() + " with error" + databaseError.getMessage());
            }
        };
        rawTempRef.addValueEventListener(listener);
        calcTempRef.addValueEventListener(listener);


        while(true) {
            try {
                Thread.sleep(5000);
                TemperatureModel temperature = bridge.getTemperature();
                if(temperature != null && temperature.isValid()) {
                    rawTempRef.setValue(DecimalFormat.getInstance().format(temperature.rawTemp));
                    calcTempRef.setValue(DecimalFormat.getInstance().format(temperature.calcTemp));
                } else {
                    System.err.println("Invalid Temperature: " + temperature);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    */
}
