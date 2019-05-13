package com.example.messenger.DbHelper;

import android.util.Log;

import com.example.messenger.Models.ChatRoom;
import com.example.messenger.Models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatHelper {

    private static final String COLLECTION_NAME = "chat_room";

    // Références de la collection

    public static CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //Requête qui créer une nouvelle chatroom et retourne l'id de celle-ci.
    public static String CreateNewChatRoom()
    {
        ChatRoom chatRoom = new ChatRoom(Calendar.getInstance().getTime());

        Task<DocumentReference> task = getChatCollection().add(chatRoom);

        while (!task.isComplete())
        {
            //Standby
        }

        DocumentReference doc = task.getResult();

        Log.i("Test", "Id de la nouvelle conversation:  " + doc.getId());

        return  doc.getId();
    }

    public static String CreateNewChatRoom(String user_1, String user_2)
    {
        ArrayList<String> users = new ArrayList<>();
        users.add(user_1);
        users.add(user_2);

        ChatRoom chatRoom = new ChatRoom(Calendar.getInstance().getTime(), users);

        Task<DocumentReference> task = getChatCollection().add(chatRoom);

        while (!task.isComplete())
        {
            //Standby
        }

        DocumentReference doc = task.getResult();

        Log.i("Test", "Id de la nouvelle conversation:  " + doc.getId());

        return  doc.getId();
    }


    public static void AddConvoToUser(String uidUser_1, String uidUser_2, String _idChatRoom)
    {
        Map<String, Object> idChatRoom = new HashMap<>();

        idChatRoom.put("chatRoomId", _idChatRoom);

        UserHelper.getUsersCollection().document(uidUser_1).collection("friends").document(uidUser_2).update("chatRoomId", _idChatRoom);
        UserHelper.getUsersCollection().document(uidUser_2).collection("friends").document(uidUser_1).update("chatRoomId", _idChatRoom);
    }

    public static String GetChatRoomId(String currentUserUid, String friendUid)
    {
        Task<DocumentSnapshot> task = UserHelper.getUsersCollection().document(currentUserUid).collection("friends").document(friendUid).get();

        while (!task.isComplete())
        {
            //Standby
        }

        DocumentSnapshot document = task.getResult();

        String result = document.get("chatRoomId").toString();
        return result;
    }

    public static Query GetChatRooms(String idUser){
        return ChatHelper.getChatCollection()
                .whereArrayContains("users", idUser);
    }

    public static void DeleteChatRoom(String idChatRoom)
    {
        ChatHelper.getChatCollection().document(idChatRoom).delete();
    }

    public static Message GetLastMessage(String chatRoomId)
    {
        Message lastMessage = null;

        Task<QuerySnapshot> task = getChatCollection().document(chatRoomId).collection("messages")
                .orderBy("dateCreated", Query.Direction.DESCENDING)
                .limit(1)
                .get();

        while (!task.isComplete())
        {
            //Standby
        }

        for (QueryDocumentSnapshot document : task.getResult())
        {
            lastMessage = document.toObject(Message.class);
        }

        return lastMessage;
    }


}
