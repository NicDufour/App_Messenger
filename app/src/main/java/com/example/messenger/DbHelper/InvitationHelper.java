package com.example.messenger.DbHelper;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InvitationHelper {

    private static final String COLLECTION_NAME = "users";


    //Reference de la collection (TESTED)
    public static CollectionReference GetUsersCollection(){

        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //Supprimer une invitation.
    public static void DeleteInvitation (String currentUserUid, String targetUserUid)
    {
        GetUsersCollection().document(targetUserUid).collection("invitation").document(currentUserUid).delete();
    }

    //Ajouter une invitation.
    public static void AddInvitation(String currentUserUid, String targetUserUid)
    {
        Map<String, Object> user = new HashMap<>();

        user.put("USER_KEY", currentUserUid);

        FirebaseFirestore.getInstance().collection("users").document(targetUserUid).collection("invitation").document(currentUserUid).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("Test", "Invitation envoyé avec succes");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Test", "Échec de l'invitation");
                    }
                });
    }

}
