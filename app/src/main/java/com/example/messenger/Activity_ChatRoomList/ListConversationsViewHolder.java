package com.example.messenger.Activity_ChatRoomList;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.messenger.R;
import com.example.messenger.DbHelper.ChatHelper;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.Models.ChatRoom;
import com.example.messenger.Models.Message;
import com.example.messenger.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ListConversationsViewHolder extends RecyclerView.ViewHolder {

    private ImageView pic;
    private TextView name;
    private TextView message;
    private final View view;

    public ListConversationsViewHolder(final View itemView)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
        view = itemView;

        pic = (ImageView)view.findViewById(R.id.list_conversation_picture_profile);
        name = (TextView)view.findViewById(R.id.list_conversation_name_chatroom);
        message = (TextView)view.findViewById(R.id.list_conversation_last_message_send);
    }

    public void UpdateConversationList(ChatRoom chatRoom, String currentUser, RequestManager glide)
    {
        ArrayList<String> users = chatRoom.getUsers();

        if(users.size() <= 2 && users.size() >= 1)
        {
            for (String userUid : users)
            {
                if(!userUid.equals(currentUser))
                {
                    User mUser = GetUser(userUid);
                    this.name.setText(mUser.getName());
                    DownloadPictureInFirebase(mUser);
                    String idChatRoom = ChatHelper.GetChatRoomId(users.get(0), users.get(1));
                    Message mMessage = ChatHelper.GetLastMessage(idChatRoom);

                    if(mMessage != null)
                    {
                        if(mMessage.getUserSender().getUid().equals(currentUser))
                        {
                            String tempVar;
                            if(mMessage.getUrlImage() != null)
                            {
                                this.message.setText("Picture");

                            }
                            else if(mMessage.getMessage().length() > 20)
                            {
                                tempVar = mMessage.getMessage().substring(0, 20);
                                this.message.setText("You :  " + tempVar + " ...");
                            }
                            else
                            {
                                tempVar = mMessage.getMessage();
                                this.message.setText("You :  " + tempVar + " ...");
                            }


                        }
                        else
                        {
                            String tempVar;

                            if(mMessage.getUrlImage() != null)
                            {
                                this.message.setText("Picture");

                            }
                            else if(mMessage.getMessage().length() > 20)
                            {
                                tempVar = mMessage.getMessage().substring(0, 20);
                                this.message.setText(tempVar + " ...");
                            }
                            else
                            {
                                tempVar = mMessage.getMessage();
                                this.message.setText(tempVar + " ...");
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void DownloadPictureInFirebase(User user)
    {

        if(user.getUrlPicture() != null)
        {
            if(!user.getUrlPicture().isEmpty())
            {
                Glide.with(view)
                        .load(user.getUrlPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(pic);
            }
        }

    }

    private User GetUser(String uid)
    {
        Task<DocumentSnapshot> task = UserHelper.getUser(uid);

        while (!task.isComplete())
        {
            //Standby treading
        }

        DocumentSnapshot doc = task.getResult();

        return doc.toObject(User.class);
    }
}

