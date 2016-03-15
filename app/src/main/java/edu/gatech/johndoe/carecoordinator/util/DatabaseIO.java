package edu.gatech.johndoe.carecoordinator.util;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Static class for handling database IO
 */
public class DatabaseIO {
    public static final Firebase DATABASE = new Firebase("https://cdccoordinator2.firebaseIO.com");

    public static void singleValueQuery(String key, final DatabaseCallback callback) {
        DATABASE.child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String data = (String) snapshot.getValue();
                callback.dataReceived(data);
            }

            @Override public void onCancelled(FirebaseError error) {
                // TODO handle failure to obtain the data from the database
            }

        });
    }
}
