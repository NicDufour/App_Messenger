package com.example.messenger.Activity_ChatRoom;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.messenger.R;
import com.example.messenger.DbHelper.MessageHelper;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.BaseActivities.BaseActivity;
import com.example.messenger.Models.Message;
import com.example.messenger.Models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatRoom_Activity extends BaseActivity implements ChatMessageAdapter.Listener {

    private RecyclerView.Adapter mAdapter;
    private ChatMessageAdapter layoutManager;
    private RecyclerView adapter;

    private String chatId;
    private FirebaseAuth mAuth;
    private User currentUser;

    private RecyclerView recycleView;
    private TextView recycleViewEmpty;

    private Button sendButton;
    private TextView txt_send;
    private String idChatRoom;
    private TextView chatRoomName;
    private ImageButton attachment;
    private ImageButton btnBack;
    private Uri uriProfilPicture;
    StorageReference ref;

    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        idChatRoom = intent.getStringExtra("idChatRoom");

        recycleView = (RecyclerView)findViewById(R.id.activity_chat_room_recycler_view);
        sendButton = (Button)findViewById(R.id.acr_send_button);
        chatRoomName = (TextView)findViewById(R.id.chat_room_name);
        txt_send = (TextView)findViewById(R.id.send_message_text);
        btnBack = (ImageButton)findViewById(R.id.back_chat_room);
        attachment = (ImageButton)findViewById(R.id.activity_mentor_chat_add_file_button);
        recycleViewEmpty = (TextView)findViewById(R.id.recycler_view_empty);

        currentUser = UserHelper.GetUserWithUid(mAuth.getCurrentUser().getUid());

        chatRoomName.setText(currentUser.getName());
        chatRoomName.setVisibility(View.INVISIBLE);

        this.configureRecyclerView(idChatRoom);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txt_send.equals(""))
                {
                    MessageHelper.AddNewMessage(idChatRoom, txt_send.getText().toString(), currentUser);
                    txt_send.setText("");
                }
            }
        });

        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddFile();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickAddFile()
    {
        SelectImage();
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



    private void Responce(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == RC_CHOOSE_PHOTO)
        {
            uriProfilPicture = data.getData();
        }
        else
        {
            Log.i("Test", "Aucune image sélectionné !");
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
                        Task task = MessageHelper.AddNewImage(idChatRoom, downloadUri.toString(), currentUser);
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

    private void configureRecyclerView(String _chatId){

        this.chatId = _chatId;

        layoutManager = new ChatMessageAdapter(GetAdapterOptions(MessageHelper.getMessagesForChatRoom(chatId)), Glide.with(this), this, mAuth.getCurrentUser().getUid());
        layoutManager.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recycleView.smoothScrollToPosition(layoutManager.getItemCount());
            }
        });
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(this.layoutManager);
    }

    private FirestoreRecyclerOptions<Message> GetAdapterOptions(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }


    public void onDataChanged() {
        recycleViewEmpty.setVisibility(this.layoutManager.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
