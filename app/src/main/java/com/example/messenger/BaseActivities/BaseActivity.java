package com.example.messenger.BaseActivities;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nullable;

public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    protected FirebaseUser GetCurrentUser()
    {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean CurrentUserLogged()
    {
        return (this.GetCurrentUser() != null);
    }
}
