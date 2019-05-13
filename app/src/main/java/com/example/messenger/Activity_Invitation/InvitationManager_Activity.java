package com.example.messenger.Activity_Invitation;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.messenger.AccountParameter_Activity;
import com.example.messenger.Activity_ChatRoomList.MainActivity;
import com.example.messenger.Activity_Map.MapsActivity;
import com.example.messenger.Activity_Friend.FriendManager_Activity;
import com.example.messenger.R;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class InvitationManager_Activity extends AppCompatActivity {

    private ListView listView;
    private Button btnInvitation;
    private FirebaseAuth auth;
    private Button btnListfriend;
    private TextView activityName;
    private ImageButton btnMapsActivity;
    private ImageButton btnChatRoom;
    private ArrayList<User> listInvitation;
    private ManageInvitationListViewAdapter mAdadpter;
    private TextView listEmpty;
    private ImageView pictureProfile;
    private ImageButton btnFriend;

    private static final int ID_ACTIVITY = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_manager_);

        auth = FirebaseAuth.getInstance();
        listInvitation = new ArrayList<User>();

        activityName = (TextView)findViewById(R.id.txt_name_activity);
        listView = (ListView)findViewById(R.id.listview_invitation);
        listEmpty = (TextView)findViewById(R.id.list_invitation_empty);
        btnInvitation = (Button)findViewById(R.id.btn_open_invitation_list);
        btnListfriend = (Button)findViewById(R.id.btn_open_friend_list);
        btnChatRoom = (ImageButton)findViewById(R.id.btn_chat_room);
        btnMapsActivity =(ImageButton)findViewById(R.id.btn_map_activity);
        btnFriend = (ImageButton)findViewById(R.id.btn_friend_activity);
        activityName.setText(R.string.invitations);
        pictureProfile = (ImageView)findViewById(R.id.list_friend_picture_profile);

        SetProfilPicture();

        SetFooterColor();

        btnListfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityListFriend();
            }
        });

        btnMapsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityMap();
            }
        });

        btnChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityListChatRoom();
            }
        });

        pictureProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityManageUser();
            }
        });

        GetAllFriend();
    }

    private void SetFooterColor()
    {
        btnChatRoom.setImageResource(R.drawable.ic_chat_blue);
        btnChatRoom.setBackgroundResource(R.drawable.hexagon_shape_unselected);
        btnFriend.setImageResource(R.drawable.ic_supervisor_account_black_24dp_selected);
        btnFriend.setBackgroundResource(R.drawable.rectangle_point_right_selected);
    }

    private void StartActivityManageUser()
    {
        Intent intent = new Intent(InvitationManager_Activity.this, AccountParameter_Activity.class);
        intent.putExtra("id_activity", ID_ACTIVITY);
        startActivity(intent);
        finish();
    }

    private void StartActivityListFriend()
    {
        Intent intent = new Intent(InvitationManager_Activity.this, FriendManager_Activity.class);
        startActivity(intent);
        finish();
    }

    private void StartActivityListChatRoom()
    {
        Intent intent = new Intent(InvitationManager_Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void StartActivityMap()
    {
        Intent intent = new Intent(InvitationManager_Activity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    private void GetAllFriend()
    {
        Task task = UserHelper.getUsersCollection().document(FirebaseAuth.getInstance().getUid())
                .collection("invitation")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                listInvitation.add(GetUser(document.getId()));
                                Log.d("Test", document.getId() + " => " + document.getData());
                            }
                            SetListInvitations();
                        } else {
                            Log.d("Test", "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    public User GetUser(String uid)
    {
        Task<DocumentSnapshot> task = UserHelper.getUser(uid);

        while (!task.isComplete())
        {
            //Standby treading
        }

        DocumentSnapshot doc = task.getResult();

        return doc.toObject(User.class);
    }

    public void SetListInvitations()
    {
        mAdadpter = new ManageInvitationListViewAdapter(listInvitation, this);
        listView.setAdapter(mAdadpter);

        if(listInvitation.size() == 0)
        {
            listEmpty.setVisibility(View.VISIBLE);
        }
        else
        {
            listEmpty.setVisibility(View.INVISIBLE);
        }
    }

    private void SetProfilPicture()
    {
        Task<DocumentSnapshot> task = UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid());

        while(!task.isComplete()){}

        DocumentSnapshot document = task.getResult();
        String uri = document.getString("urlPicture");

        if(uri != null)
        {
            if(!uri.isEmpty())
            {
                FirebaseStorage ref = FirebaseStorage.getInstance();
                StorageReference pathReference = ref.getReference("images/stars.jpg");

                Glide.with(this)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(pictureProfile);
            }
        }
    }
}
