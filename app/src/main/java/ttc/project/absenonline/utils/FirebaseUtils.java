package ttc.project.absenonline.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Fikry-PC on 12/5/2017.
 */

public class FirebaseUtils {
    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    public static DatabaseReference getDatabaseReference(){
        return FirebaseDatabase.getInstance().getReference();
    }
}
