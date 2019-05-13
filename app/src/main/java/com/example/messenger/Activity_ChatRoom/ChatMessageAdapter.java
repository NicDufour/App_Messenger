package com.example.messenger.Activity_ChatRoom;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.bumptech.glide.RequestManager;
import com.example.messenger.ChatMessageViewHolder;
import com.example.messenger.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.example.messenger.Models.Message;
import javax.annotation.Nonnull;

public class ChatMessageAdapter extends FirestoreRecyclerAdapter<Message, ChatMessageViewHolder> {

    public interface Listener {
        void onDataChanged();
    }

    private final RequestManager glide;
    private final String idCurrentUser;
    private Listener callback;

    public ChatMessageAdapter(@Nonnull FirestoreRecyclerOptions<Message> options, RequestManager glide, Listener callback, String idCurrentUser) {
        super(options);
        this.glide = glide;
        this.callback = callback;
        this.idCurrentUser = idCurrentUser;
    }

    @Override
    protected void onBindViewHolder(@Nonnull ChatMessageViewHolder holder, int position, @Nonnull Message model) {
        holder.updateMessage(model, this.idCurrentUser, this.glide);
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatMessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat_item, parent, false));
    }


    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }
}
