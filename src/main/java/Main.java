import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Zoranor on 2016-07-04.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting application...");
        int runningTime = 0;

        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream("serviceAccountCredentials.json"))
                    .setDatabaseUrl("https://javaconnectedthermostats.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("message");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("Reading from server: " + dataSnapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("onCancelled for ref:" + ref.toString() + " with error" + databaseError.getMessage());
                }
            });



            while(true) {
                try {
                    Thread.sleep(5000);
                    runningTime += 5000;
                    ref.setValue("Hello, World! Running Time:" + runningTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
