package com.example.messenger.DbHelper;

import com.example.messenger.Models.Message;
import com.example.messenger.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

public class MessageHelper {

    private static final String COLLECTION_NAME = "messages";

    public static Query getMessagesForChatRoom(String idChat){
        return ChatHelper.getChatCollection()
                .document(idChat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    public static Task<DocumentReference> AddNewMessage(String _chatRoomId, String _message, User sender)
    {
        Message message = new Message(_message, sender);

        return ChatHelper.getChatCollection()
                .document(_chatRoomId)
                .collection(COLLECTION_NAME)
                .add(message);
    }

    public static Task<DocumentReference> AddNewImage(String _chatRoomId, String uriPicture, User sender)
    {
        Message message = new Message();
        message.setUrlImage(uriPicture);
        message.setUserSender(sender);

        return ChatHelper.getChatCollection()
                .document(_chatRoomId)
                .collection(COLLECTION_NAME)
                .add(message);
    }
}
