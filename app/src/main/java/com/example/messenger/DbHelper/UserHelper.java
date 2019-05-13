//QUERRY Bd (CRUD)

package com.example.messenger.DbHelper;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.messenger.Models.Friend;
import com.example.messenger.Models.Invitation;
import com.example.messenger.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class UserHelper {
    private static final String COLLECTION_NAME = "users";


    //Reference de la collection (TESTED)
    public static CollectionReference getUsersCollection(){

        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CollectionReference getLocationReference(String uid){

        return getUsersCollection().document(uid).collection("location");
    }



    //CREATE USER (TESTED)
    public static Task<Void> createUser(String uid, String name, String email, String genre) {

        User userToCreate = new User(uid, name, email, genre);
        return UserHelper.getUsersCollection()
                .document(uid)
                .set(userToCreate);
    }


    public static void RemoveFriend(String mUid, String friendUid)
    {
        Log.i("Test", "class --> UserHelper: RemoveFriend(Function)");

        FirebaseFirestore.getInstance().collection("users").document(mUid).collection("friends").document(friendUid).delete()
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("Test", "Function(RemoveFriend): Successful operation");
                    }
                })

                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@Nonnull Exception e) {
                        Log.i("Test", "Function(RemoveFriend): Failed operation");
                    }
                });
    }


    //ADD FRIEND (TESTED)
    public static void addFriend(String mUid, String friendUid)
    {
        Log.i("Test", "class --> UserHelper: addFriend(Function)");

        Friend friend = new Friend(friendUid);

        FirebaseFirestore.getInstance().collection("users").document(mUid).collection("friends").document(friendUid).set(friend)
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("Test", "Function(addFriend): Successful operation");
                    }
                })

                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@Nonnull Exception e) {
                        Log.i("Test", "Function(addFriend): Failed operation");
                    }
                });
    }

    public static void SendInvitation(User mUid, User userInvitedUid)
    {
        Map<String, Object> user = new HashMap<>();

        Invitation mInvitation = new Invitation(userInvitedUid, mUid);

        user.put("USER_KEY", userInvitedUid);

        FirebaseFirestore.getInstance().collection("users").document(mUid.getUid()).collection("invitation").document(userInvitedUid.getUid()).set(mInvitation)
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

    public static void CancelInvitation(String mUid, String userInvitedUid)
    {
        FirebaseFirestore.getInstance().collection("users").document(mUid).collection("invitation").document(userInvitedUid).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("Test", "Invitation cacellé avec succes");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Test", "Échec de l'opération");
                    }
                });
    }

    //GET User
    public static Task<DocumentSnapshot> getUser(String uid){

        return UserHelper.getUsersCollection().document(uid).get();
    }

    //GET Users
    public static ArrayList<User> getAllUsers(){

        final ArrayList<User> users = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        users.add(document.toObject(User.class));
                        Log.d("Test", document.getId() + " => " + document.getData());
                    }
                    Log.i("Test", "Recherche tous utilisateur réussi");
                }
                else
                {
                    Log.d("Test", "Error getting documents: ", task.getException());
                }
            }
        });

        return  users;
    }

    public static  User GetUserWithUid(String uid)
    {
        Task<DocumentSnapshot> task = UserHelper.getUsersCollection().document(uid).get();
        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Log.i("Test", "User getted with succes");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Test", "User getted with fail");
            }
        });

        while (!task.isComplete())
        {
            //standby
        }

        DocumentSnapshot doc = task.getResult();

        User user = doc.toObject(User.class);

        return user;
    }

    // GET Friends
    public static Task<QuerySnapshot> getFriends(String uid) {

        Log.i("Test", "Entrer getFriend");
        Log.i("Test", "For udi: " + uid);

        return FirebaseFirestore.getInstance().collection("users").document(uid).collection("friends")
                .get();
    }

    public static Task<DocumentSnapshot> getFriends(String uid, String uidFriend) {

        return getUsersCollection().document(uid).collection("friends").document(uidFriend)
                .get();
    }

    //Update User
    public static Task<Void> updateUsername(String username, String uid) {

        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateAutoriseLocation(String uid, Boolean isMentor) {

        return UserHelper.getUsersCollection().document(uid).update("autoriseLocation", isMentor);
    }

    public static Task<Void> updateProfilPicture(String uid, String uriPicture) {

        return UserHelper.getUsersCollection().document(uid).update("urlPicture", uriPicture);
    }

    public static  void UpdateLocalisationStatue(Boolean value, String currentUid)
    {
        UserHelper.getUsersCollection().document(currentUid).update("autoriseLocation", value);
    }

    //DELETE User
    public static Task<Void> deleteUser(String uid) {

        return UserHelper.getUsersCollection().document(uid).delete();
    }

}
