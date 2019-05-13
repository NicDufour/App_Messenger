package com.example.messenger.Activity_Friend;

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
import com.example.messenger.Activity_ChatRoom.ChatRoom_Activity;
import com.example.messenger.Activity_ChatRoomList.MainActivity;
import com.example.messenger.Activity_Invitation.InvitationManager_Activity;
import com.example.messenger.Activity_Map.MapsActivity;
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

public class FriendManager_Activity extends AppCompatActivity {

    private ListView listView;
    private ImageButton btn_back;
    private Button btnInvitation;
    private ImageButton btnAddFriend;
    private TextView activityName;
    private ImageButton btnMapsActivity;
    private ImageButton btnFriend;
    private ImageButton btnChatRoom;
    private ArrayList<User> listFriend;
    private ImageView profilPicture;
    private ManageFriendListAdapter mAdadpter;

    private static final int ID_ACTIVITY = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_manager_);

        listFriend = new ArrayList<User>();

        activityName = (TextView)findViewById(R.id.txt_name_activity);
        btn_back = (ImageButton)findViewById(R.id.list_friend_btn_message);
        btnFriend = (ImageButton)findViewById(R.id.btn_friend_activity);
        listView = (ListView)findViewById(R.id.listview_friend);
        btnChatRoom = (ImageButton)findViewById(R.id.btn_chat_room);

        btnMapsActivity =(ImageButton)findViewById(R.id.btn_map_activity);
        profilPicture = (ImageView)findViewById(R.id.list_friend_picture_profile);
        btnInvitation = (Button)findViewById(R.id.btn_open_invitation_list);
        btnAddFriend = (ImageButton)findViewById(R.id.btn_friend_activity_start_add_friend_activity);

        activityName.setText("Friends");
        SetProfilPicture();
        SetFooterColor();


        profilPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityManageUser();
            }
        });

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityAddFriend();
            }
        });

        btnInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityListInvitation();
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

        GetAllFriend();
    }

    private void SetFooterColor()
    {
        btnChatRoom.setImageResource(R.drawable.ic_chat_blue);
        btnChatRoom.setBackgroundResource(R.drawable.hexagon_shape_unselected);
        btnFriend.setImageResource(R.drawable.ic_supervisor_account_black_24dp_selected);
        btnFriend.setBackgroundResource(R.drawable.rectangle_point_right_selected);
    }

    private void StartActivityListInvitation()
    {
        Intent intent = new Intent(FriendManager_Activity.this, InvitationManager_Activity.class);
        startActivity(intent);
        finish();
    }

    private void StartActivityListChatRoom()
    {
        Intent intent = new Intent(FriendManager_Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void StartActivityMap()
    {
        Intent intent = new Intent(FriendManager_Activity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    private void StartActivityManageUser()
    {
        Intent intent = new Intent(FriendManager_Activity.this, AccountParameter_Activity.class);
        intent.putExtra("id_activity", ID_ACTIVITY);
        startActivity(intent);
        finish();
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
                        .into(profilPicture);
            }
        }
    }


    public void StartActivityChatRoom(String idChatRoom)
    {
        Intent intent = new Intent(FriendManager_Activity.this, ChatRoom_Activity.class);
        intent.putExtra("idChatRoom", idChatRoom);
        startActivity(intent);
    }

    private void GetAllFriend()
    {
        final ArrayList<String> listIdFriends = new ArrayList<>();

        Task task = UserHelper.getUsersCollection().document(FirebaseAuth.getInstance().getUid())
                .collection("friends")
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
                                    listFriend.add(GetUser(document.getId()));
                                }
                            }
                            SetListFriend();
                        } else {
                            Log.d("Test", "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SetProfilPicture();
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

    public void SetListFriend()
    {
        mAdadpter = new ManageFriendListAdapter(listFriend, FriendManager_Activity.this);
        listView.setAdapter(mAdadpter);
    }

    private void StartActivityAddFriend()
    {
        Intent intent = new Intent(FriendManager_Activity.this, AddNewFriend_Activity.class);
        startActivity(intent);
    }
}
