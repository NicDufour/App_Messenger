package com.example.messenger.Models;

public class ResearchUser {

    private User userTarget;
    private boolean invitationSended;

    public ResearchUser(User _userTarget, boolean _invitationSended)
    {
        invitationSended = _invitationSended;
        userTarget = _userTarget;
    }

    public boolean getInvitationSended() {
        return invitationSended;
    }

    public void setInvitationSended(boolean invitationSended) {
        this.invitationSended = invitationSended;
    }

    public User getUserTarget() {
        return userTarget;
    }

    public void setUserTarget(User userTarget) {
        this.userTarget = userTarget;
    }
}
