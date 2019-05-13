package com.example.messenger.Models;

import javax.annotation.Nullable;

public class Friend {

    private String friendId;
    @Nullable
    private String chatRoomId;

    public Friend()
    {
        friendId = "";
        chatRoomId = null;

    }

    public Friend(String idFriend)
    {
        friendId = idFriend;
        chatRoomId = null;

    }

    public Friend(String idFriend, String idChatRoom)
    {
        friendId = idFriend;
        chatRoomId = idChatRoom;

    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    @Nullable
    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(@Nullable String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
