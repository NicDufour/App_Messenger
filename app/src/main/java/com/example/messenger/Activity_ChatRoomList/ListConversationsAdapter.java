package com.example.messenger.Activity_ChatRoomList;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.example.messenger.Activity_ChatRoom.ChatRoom_Activity;
import com.example.messenger.DbHelper.ChatHelper;
import com.example.messenger.Models.ChatRoom;
import com.example.messenger.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class ListConversationsAdapter extends FirestoreRecyclerAdapter<ChatRoom, ListConversationsViewHolder> {

    private final RequestManager glide;
    private final String currentUser;
    private Listener callback;
    private Context context;


    public interface Listener{
        void onDataChanged();
    }

    public ListConversationsAdapter(@Nonnull FirestoreRecyclerOptions<ChatRoom> options, RequestManager glide, Listener callback, String currentUser, Context context)
    {
        super(options);
        this.glide = glide;
        this.callback = callback;
        this.currentUser = currentUser;
        this.context = context;

    }
    @Override
    protected void onBindViewHolder(@NonNull final ListConversationsViewHolder holder, final int position, @Nonnull ChatRoom model) {
        holder.UpdateConversationList(model, this.currentUser, this.glide);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> users = getItem(position).getUsers();
                String idChatRoom = ChatHelper.GetChatRoomId(users.get(0), users.get(1));
                OpenChatRoomActivity(idChatRoom);
            }
        });
    }

    private void OpenChatRoomActivity(String idChatRoom)
    {
        Intent intent = new Intent(context, ChatRoom_Activity.class);
        intent.putExtra("idChatRoom", idChatRoom);
        context.startActivity(intent);
    }


    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }

    @Override
    public ListConversationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListConversationsViewHolder(LayoutInflater.from(parent.getContext())
                   .inflate(R.layout.fragment_item_list_conversation, parent, false));
    }


}
