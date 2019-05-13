package com.example.messenger.Activity_ChatRoomList;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.messenger.AccountParameter_Activity;
import com.example.messenger.Activity_Map.MapsActivity;
import com.example.messenger.Activity_Friend.FriendManager_Activity;
import com.example.messenger.DbHelper.ChatHelper;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.LoadingAppActivity;
import com.example.messenger.Models.ChatRoom;
import com.example.messenger.Models.Friend;
import com.example.messenger.R;
import com.example.messenger.Services.TrackerService;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListConversationsAdapter.Listener {

    private FirebaseAuth mAuth;

    ArrayList<Friend> mFriendList = new ArrayList<Friend>();

    String[] maintitle;
    String[] subtitle;
    Integer[] imgid;
    int nbFriends = mFriendList.size();
    private ImageButton btnStartFriendActivit;
    private RecyclerView recycleView;

    private static final int PERMISSIONS_REQUEST = 100;
    private static final int ID_ACTIVITY = 1;


    private ListConversationsAdapter layoutManager;
    private String currentUser;
    private ImageButton btn_map;
    private TextView recycleViewEmpty;
    private ImageView profilPicture;
    private TextView nameActivity;
    private ImageButton chatButton;
    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int code = getIntent().getIntExtra("startCode", 0);

        if(code == 1)
        {
            StartLoadindActivity();
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        startTrackerService();


        btnStartFriendActivit = (ImageButton)findViewById(R.id.btn_friend_activity);
        recycleView = (RecyclerView)findViewById(R.id.activity_main_recycler_view);
        recycleViewEmpty = (TextView)findViewById(R.id.recycler_view_empty);
        profilPicture = (ImageView)findViewById(R.id.list_friend_picture_profile);
        btn_map = (ImageButton)findViewById(R.id.btn_map_activity);
        nameActivity = (TextView)findViewById(R.id.txt_name_activity);
        chatButton = (ImageButton)findViewById(R.id.btn_chat_room);

        maintitle = new String[nbFriends];
        subtitle = new String[nbFriends];
        imgid = new Integer[nbFriends];

        nameActivity.setText(R.string.activity_main);
        SetProfilPicture();

        try
        {
            startTrackerService();
        }
        catch (Exception e)
        {
            SetLocationPermissions();
        }

        this.configureRecyclerView(mAuth.getUid());

        btnStartFriendActivit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LunchFriendActivity();
            }
        });


        recycleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LunchMapActivity();
            }
        });

        profilPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityManageUser();
            }
        });
    }

    private void SetLocationPermissions()
    {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        } else {
            finish();
        }
    }
    private void StartActivityManageUser()
    {
        Intent intent = new Intent(MainActivity.this, AccountParameter_Activity.class);
        intent.putExtra("id_activity", ID_ACTIVITY);
        startActivity(intent);
        finish();
    }

    private void startTrackerService() {
        startService(new Intent(this, TrackerService.class));

    }

    private void LunchFriendActivity()
    {
        Intent intent = new Intent(MainActivity.this, FriendManager_Activity.class);
        startActivity(intent);
        finish();
    }

    private void LunchMapActivity()
    {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    private void StartLoadindActivity()
    {
        Intent intent = new Intent(MainActivity.this, LoadingAppActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void configureRecyclerView(String _currentUser){

        this.currentUser = _currentUser;

        layoutManager = new ListConversationsAdapter(GetAdapterOptions(ChatHelper.GetChatRooms(_currentUser)), Glide.with(this), this, mAuth.getCurrentUser().getUid(), MainActivity.this);
        layoutManager.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recycleView.smoothScrollToPosition(layoutManager.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(this.layoutManager);
    }

    @Override
    public void onDataChanged() {
        recycleViewEmpty.setVisibility(this.layoutManager.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private FirestoreRecyclerOptions<ChatRoom> GetAdapterOptions(Query query){
        return new FirestoreRecyclerOptions.Builder<ChatRoom>()
                .setQuery(query, ChatRoom.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void SetProfilPicture()
    {
        Task<DocumentSnapshot> task = UserHelper.getUser(mAuth.getCurrentUser().getUid());

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
}
