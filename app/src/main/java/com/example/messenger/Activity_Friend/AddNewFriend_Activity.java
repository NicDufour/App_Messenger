package com.example.messenger.Activity_Friend;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.messenger.R;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.Models.ResearchUser;
import com.example.messenger.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddNewFriend_Activity extends AppCompatActivity {

    private ArrayList<ResearchUser> listUser;
    private AddFriendListAdapter mAdapter;
    private ImageButton btn_back;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friend_);

        listView = (ListView)findViewById(R.id.list_user_result);
        listUser = new ArrayList<ResearchUser>();
        btn_back = findViewById(R.id.back_add_friend);

        GetAllUsers();

        Log.i("Test", "sdjflksjdflsf");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void GetAllUsers()
    {
        UserHelper.getUsersCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                if(!document.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                {
                                    Log.d("Test", document.getId() + " => " + document.getData());
                                    listUser.add(new ResearchUser(document.toObject(User.class), CheckIfInvited(document.getId())));

                                }
                            }
                            SetViewListUser();
                        } else {
                            Log.d("Test", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private boolean CheckIfInvited(String uid)
    {
        Task<DocumentSnapshot> doc = FirebaseFirestore.getInstance().collection("users")
            .document(uid)
            .collection("invitation")
            .document(FirebaseAuth.getInstance().getUid())
            .get();
        while (!doc.isComplete())
        {

        }
        DocumentSnapshot document = doc.getResult();

        if(document.exists())
        {
            Log.i("Test", uid + " --> true");
            return true;
        }
        else
        {
            Log.i("Test", uid + " --> false");
            return  false;
        }


    }
    private void SetViewListUser()
    {
        mAdapter = new AddFriendListAdapter(listUser, this);
        listView.setAdapter(mAdapter);
    }
}
