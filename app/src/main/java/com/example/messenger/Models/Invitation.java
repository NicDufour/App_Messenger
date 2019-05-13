package com.example.messenger.Models;

public class Invitation {

    private User userTarget;
    private User userSender;

    public Invitation(User _userTarget,User _userSender)
    {
        userSender = _userSender;
        userTarget = _userTarget;
    }

    public User getUserSender() {
        return userSender;
    }

    public User getUserTarget() {
        return userTarget;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
    }

    public void setUserTarget(User userTarget) {
        this.userTarget = userTarget;
    }
}
