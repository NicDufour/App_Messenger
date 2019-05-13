package com.example.messenger.Models;

import java.util.ArrayList;
import java.util.Date;

public class ChatRoom {

    private Date creationDate;
    private String name;
    private ArrayList<String> users;

    private boolean isPrivateChatRoom;

    public ChatRoom(Date _creationDate)
    {
        creationDate = _creationDate;
    }

    public ChatRoom(){}

    public ChatRoom(Date _creationDate, ArrayList<String> users)
    {
        this.creationDate = _creationDate;
        this.users = users;
    }


    public Date getCreationDate() {
        return creationDate;
    }

    public ArrayList<String> getUsers() {
        return users;
    }


    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
