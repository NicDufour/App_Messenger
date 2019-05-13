package com.example.messenger;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.messenger.Activity_ChatRoomList.MainActivity;
import com.example.messenger.Activity_Friend.FriendManager_Activity;
import com.example.messenger.Activity_Invitation.InvitationManager_Activity;
import com.example.messenger.Activity_Map.MapsActivity;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.BaseActivities.BaseActivity;
import com.example.messenger.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AccountParameter_Activity extends BaseActivity {

    private ImageView profilePicture;
    private TextView txtEmail;
    private CheckBox location;
    private Button btnSignOut;
    private Button btnChangePic;
    private ImageButton backButton;
    private StorageReference ref;
    private Uri uriProfilPicture;
    private TextView username;
    private int source;

    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_parameter_);

        source = getIntent().getIntExtra("id_activity", 0);

        profilePicture = (ImageView)findViewById(R.id.apa_profile_picture);
        txtEmail = (TextView)findViewById(R.id.apa_txt_email);
        backButton = (ImageButton)findViewById(R.id.back_setting);
        location = (CheckBox)findViewById(R.id.apa_autorise_location);
        btnSignOut = (Button)findViewById(R.id.apa_btn_sign_out);
        username = (TextView)findViewById(R.id.apa_txt_name);
        btnChangePic = (Button)findViewById(R.id.btn_change_picture_profile);

        SetInitialView();

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LogOut();

                FirebaseAuth.getInstance()
                        .signOut();

                while (FirebaseAuth.getInstance().getCurrentUser() != null)
                {

                }

                LogOut();

            }
        });

        btnChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddFile();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateLocationStatue();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitSetUpAccount();
            }
        });

    }

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickAddFile()
    {
        SelectImage();
    }

    private void SelectImage()
    {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.app_name), RC_IMAGE_PERMS, PERMS);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_CHOOSE_PHOTO);
    }

    private void Responce(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == RC_CHOOSE_PHOTO)
        {
            uriProfilPicture = data.getData();
            Glide.with(this)
                    .load(uriProfilPicture)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profilePicture);
        }
        else
        {
            Log.i("Test", "Aucune image de profile !");
        }
    }

    private void ExitSetUpAccount()
    {
        Intent intent;

        if(source == 1)
        {
            intent = new Intent(AccountParameter_Activity.this, MainActivity.class);
        }
        else if(source == 2)
        {
            intent = new Intent(AccountParameter_Activity.this, MapsActivity.class);
        }
        else if(source == 3)
        {
            intent = new Intent(AccountParameter_Activity.this, FriendManager_Activity.class);
        }
        else
        {
            intent = new Intent(AccountParameter_Activity.this, InvitationManager_Activity.class);
        }

        startActivity(intent);
        finish();
    }

    private void SetInitialView()
    {
        if(GetCurrentUser() != null)
        {
            Task<DocumentSnapshot> task = UserHelper.getUser(GetCurrentUser().getUid());

            while(!task.isComplete()){}

            DocumentSnapshot document = task.getResult();
            User user = document.toObject(User.class);

            username.setText(user.getName());

            if(user.getUrlPicture() != null)
            {
                if(!user.getUrlPicture().isEmpty())
                {
                    FirebaseStorage ref = FirebaseStorage.getInstance();
                    StorageReference pathReference = ref.getReference("images/stars.jpg");

                    Glide.with(this)
                            .load(user.getUrlPicture())
                            .apply(RequestOptions.circleCropTransform())
                            .into(profilePicture);
                }
            }

            txtEmail.setText(user.geteMail());

            if(user.getAutoriseLocation().equals(true))
            {
                location.setChecked(true);
            }
        }
    }


    private void UploadPictureInFirebase()
    {
        String uuid = UUID.randomUUID().toString();

        ref = FirebaseStorage.getInstance().getReference(uuid);
        ref.putFile(uriProfilPicture).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUri = uri;
                        Task task = UserHelper.updateProfilPicture(FirebaseAuth.getInstance().getUid(), downloadUri.toString());
                        while (!task.isComplete()){}
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null)
        {
            super.onActivityResult(requestCode, resultCode, data);
            this.Responce(requestCode, resultCode, data);
            UploadPictureInFirebase();
        }

    }

    private void LogOut()
    {
        Intent intent = new Intent(AccountParameter_Activity.this, Login_Activity.class);

        startActivity(intent);

        finish();
    }


    private void UpdateLocationStatue()
    {
        if(location.isChecked())
        {
            UserHelper.UpdateLocalisationStatue(true, FirebaseAuth.getInstance().getUid());
        }
        else
        {
            UserHelper.UpdateLocalisationStatue(false, FirebaseAuth.getInstance().getUid());
        }
    }
}

