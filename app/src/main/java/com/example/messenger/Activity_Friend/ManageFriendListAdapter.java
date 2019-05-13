package com.example.messenger.Activity_Friend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.messenger.Activity_ChatRoom.ChatRoom_Activity;
import com.example.messenger.R;
import com.example.messenger.DbHelper.ChatHelper;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.Models.Friend;
import com.example.messenger.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class ManageFriendListAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<User> mListUsers;
    private Context context;
    private View view;
    Boolean userResult;
    private FirebaseAuth auth;

    public ManageFriendListAdapter(ArrayList<User> list, Context _context)
    {
        mListUsers = list;
        context = _context;
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return mListUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mListUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public String getUserId(int position)
    {
        return mListUsers.get(position).getUid();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_friend_adapter, null);
        }

        TextView friendName = (TextView)view.findViewById(R.id.list_friend_name);
        ImageView pictureProfile = (ImageView)view.findViewById(R.id.list_friend_picture_profile);
        ImageButton btnInfo = (ImageButton) view.findViewById(R.id.list_friend_btn_info);
        ImageButton btnMessage = (ImageButton) view.findViewById(R.id.list_friend_btn_message);

        friendName.setText(mListUsers.get(position).getName());

        Task<DocumentSnapshot> task = UserHelper.getUser(mListUsers.get(position).getUid());

        while(!task.isComplete()){}

        DocumentSnapshot document = task.getResult();
        String uri = document.getString("urlPicture");

        if(uri != null)
        {
            if(!uri.isEmpty())
            {
                Glide.with(view)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(pictureProfile);
            }
        }


        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layout = LayoutInflater.from(context);
                View promptsView = layout.inflate(R.layout.activity_friend_manager_delete_prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setView(promptsView);



                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteFriend(position);
                                userResult = true;
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userResult = false;
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();

            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenChatRoom(position);
            }
        });

        return view;
    }

    private void DeleteFriend(int position)
    {

        String friend = mListUsers.get(position).getUid();
        mListUsers.remove(position);

        try {
            ChatHelper.DeleteChatRoom(ChatHelper.GetChatRoomId(auth.getUid(), friend));
        }
        catch (Exception e)
        {
            Log.i("Test", e.getMessage());
        }
        //Retirer l'ami de la liste d'ami de cet l'utilisateur
        UserHelper.RemoveFriend(auth.getUid(), friend);

        //Retirer l'utilisateur de la liste d'ami de l'autre utilisateur.
        UserHelper.RemoveFriend(friend, auth.getUid());

        notifyDataSetChanged();
    }

    private void OpenChatRoom(int position)
    {
        if(CheckIfChatRoomExist(mListUsers.get(position).getUid()))
        {
            Log.i("Test", "Il y a un chatroom");
            OpenCurrentChatRoom(auth.getUid(), mListUsers.get(position).getUid());

        }
        else
        {
            Log.i("Test", "Il n'y a pas de chatroom");
            OpenNewChatRoom(auth.getUid(), mListUsers.get(position).getUid());
        }

    }

    private void OpenCurrentChatRoom(String idCurrentUser, String idFriend)
    {
        String idChatRoom = ChatHelper.GetChatRoomId(idCurrentUser, idFriend);
        OpenChatRoomActivity(idChatRoom);
    }

    private void OpenNewChatRoom(String user_1, String user_2)
    {
        String idChatRoom;

        idChatRoom = ChatHelper.CreateNewChatRoom(user_1, user_2);

        OpenChatRoomActivity(idChatRoom);

        ChatHelper.AddConvoToUser(user_1, user_2, idChatRoom);
    }

    private void OpenChatRoomActivity(String idChatRoom)
    {
        Intent intent = new Intent(context, ChatRoom_Activity.class);
        intent.putExtra("idChatRoom", idChatRoom);
        context.startActivity(intent);
    }

    private boolean CheckIfChatRoomExist(String uidFriend)
    {
        Task<DocumentSnapshot> querry = UserHelper.getFriends(auth.getUid(), uidFriend);

        while (!querry.isComplete())
        {
            //Standby
        }

        DocumentSnapshot document = querry.getResult();

        Friend friend = document.toObject(Friend.class);

        if (friend.getChatRoomId() == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
