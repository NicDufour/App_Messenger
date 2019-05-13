package com.example.messenger.DbHelper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FriendHelper {
    private static final String COLLECTION_NAME = "users";


    //Reference de la collection
    public static CollectionReference getUsersCollection(){

        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static ArrayList<String> GetFriendList(String mUid)
    {
        ArrayList<String> listFriend = new ArrayList<>();

        Task<QuerySnapshot> task = getUsersCollection().document(mUid).collection("friends").get();

        while (!task.isComplete()){}

        for (QueryDocumentSnapshot document : task.getResult())
        {
            listFriend.add(document.get("friendId").toString());
        }

        return listFriend;
    }
}
