package com.example.messenger.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private String message;
    private Date dateCreated;
    private User userSender;
    private User userReceiver;
    private String urlImage;

    public Message()
    {

    }

    public Message(String _message, User _Sender)
    {
        this.message = _message;
        this.userSender = _Sender;
    }

    public Message(String _message,String _urlImage, User _userSender, User _userReceiver)
    {
        this.message = _message;
        this.urlImage = _urlImage;
        this.userSender = _userSender;
        this.userReceiver = _userReceiver;
    }

    public String getMessage()
    {
        return message;
    }
    @ServerTimestamp
    public Date getDateCreated()
    {
        return dateCreated;
    }

    public User getUserSender()
    {
        return userSender;
    }

    public String getUrlImage()
    {
        return urlImage;
    }

    public User getUserReceiver()
    {
        return userReceiver;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;

    }

    public void setUrlImage(String urlImage)
    {
        this.urlImage = urlImage;
    }

    public void setUserReceiver(User userReceiver)
    {
        this.userReceiver = userReceiver;
    }

    public void setUserSender(User userSender)
    {
        this.userSender = userSender;
    }
}
