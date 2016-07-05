import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.text.DecimalFormat;

/**
 * Created by Zoranor on 2016-07-04.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting application...");
        Bridge bridge = new Bridge();
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream("serviceAccountCredentials.json"))
                    .setDatabaseUrl("https://javaconnectedthermostats.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("sensor");
            DatabaseReference rawTempRef = ref.child("raw-temp");
            DatabaseReference calcTempRef = ref.child("calc-temp");
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("From Server: " + dataSnapshot.getKey() + ": " + dataSnapshot.getValue(String.class));
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
                    Bridge.TemperatureModel temperature = bridge.getTemperature();
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
