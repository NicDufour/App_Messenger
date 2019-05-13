package com.example.messenger.Activity_Map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.messenger.AccountParameter_Activity;
import com.example.messenger.Activity_Friend.FriendManager_Activity;
import com.example.messenger.Activity_ChatRoomList.MainActivity;
import com.example.messenger.Models.Users;
import com.example.messenger.R;
import com.example.messenger.DbHelper.FriendHelper;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.Models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = MainActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private ImageButton btnChatRoom;
    private ImageButton btnFriendActivity;
    private ImageButton btnMapsActivity;
    private FusedLocationProviderClient fusedLocationClient;
    private ImageView btnProfile;
    private TextView textView;

    private static final int ID_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnChatRoom = (ImageButton)findViewById(R.id.btn_chat_room);
        btnFriendActivity = (ImageButton)findViewById(R.id.btn_friend_activity);
        btnMapsActivity = (ImageButton)findViewById(R.id.btn_map_activity);
        btnProfile = (ImageView)findViewById(R.id.list_friend_picture_profile);
        textView = (TextView)findViewById(R.id.txt_name_activity);

        textView.setText(R.string.map_name);
        textView.setVisibility(View.VISIBLE);
        SetFooterColor();

        SetProfilPicture();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnFriendActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartFriendActivity();
            }
        });

        btnChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartMainActivity();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivityManageUser();
            }
        });
    }

    private void SetFooterColor()
    {
        btnChatRoom.setImageResource(R.drawable.ic_chat_blue);
        btnChatRoom.setBackgroundResource(R.drawable.hexagon_shape_unselected);
        btnMapsActivity.setImageResource(R.drawable.ic_map_black_24dp_selected);
        btnMapsActivity.setBackgroundResource(R.drawable.rectangle_point_left_selected);
    }


    private void StartActivityManageUser()
    {
        Intent intent = new Intent(MapsActivity.this, AccountParameter_Activity.class);
        intent.putExtra("id_activity", ID_ACTIVITY);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(16);
        mMap.setBuildingsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mMap.animateCamera(GetMapFocus(location));
                        }
                    }
                });
        subscribeToUpdates();
    }

    private CameraUpdate GetMapFocus(Location location)
    {
        CameraPosition camPos = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(40)
                .bearing(location.getBearing())
                .tilt(0)
                .build();
        CameraUpdate focus = CameraUpdateFactory.newCameraPosition(camPos);

        return  focus;
    }


    private void StartFriendActivity()
    {
        Intent intent = new Intent(MapsActivity.this, FriendManager_Activity.class);
        startActivity(intent);
        finish();
    }

    private void StartMainActivity()
    {
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void SetProfilPicture()
    {
        btnProfile.setVisibility(View.VISIBLE);

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
                        .into(btnProfile);
            }
        }
        else
        {
            btnProfile.setImageResource(R.drawable.ic_chat_user);
        }
    }

    private void subscribeToUpdates() {

        ArrayList<String> friendList = FriendHelper.GetFriendList(FirebaseAuth.getInstance().getUid());

        for (String uid : friendList)
        {
            UserHelper.getUsersCollection()
                    .whereEqualTo("uid", uid)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                User user = doc.toObject(User.class);

                                if(user.getAutoriseLocation().equals(true))
                                {
                                    try
                                    {
                                        setMarker(user);
                                    }
                                    catch (Exception exep)
                                    {
                                        Log.i("Test", "Location statue invalid" + "Exception: "+ exep.getMessage());
                                    }

                                }
                                else
                                {
                                    try
                                    {
                                        removeMarker(user);
                                    }
                                    catch (Exception exep)
                                    {
                                        Log.i("Test", "Location statue invalid" + "Exception: "+ exep.getMessage());
                                    }

                                }
                            }
                        }
                    });
        }

    }

    private void setMarker(User user) {
        String key = user.getUid();

        double lat = user.getLocation().getLatitude();
        double lng = user.getLocation().getLongitude();

        LatLng location = new LatLng(lat, lng);

        if (!mMarkers.containsKey(key)) {

            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(user.getName()).icon(bitmapDescriptorFromVector(this, R.drawable.ic_accessibility_black_24dp)).position(location)));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SetProfilPicture();
    }

    private void removeMarker(User user)
    {
        String key = user.getUid();

        if (mMarkers.containsKey(key))
        {
            mMarkers.remove(key);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
