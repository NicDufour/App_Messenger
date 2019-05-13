package com.example.messenger;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.Models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    private final int destinationUserColor;
    private final int currentUserColor;
    private View view;

    @BindView(R.id.fci_main_view) RelativeLayout mainView;
    @BindView(R.id.fci_profile_info_container) LinearLayout profileContainer;
    @BindView(R.id.fci_profil_picture) ImageView profilePicture;
    @BindView(R.id.fci_message_container) RelativeLayout messageContainer;
    @BindView(R.id.fci_cardview_container_image_sent) CardView cardView;
    @BindView(R.id.fci_image_cardview) ImageView imageCardView;
    @BindView(R.id.fci_text_message_container) LinearLayout messageTextContainer;
    @BindView(R.id.fci_message_text) TextView textMessage;
    @BindView(R.id.fci_text_date) TextView textDate;

    public ChatMessageViewHolder(View itemView)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
        view = itemView;
        destinationUserColor = ContextCompat.getColor(itemView.getContext(), R.color.lightGray);
        currentUserColor = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
    }
    public void updateMessage(Message message, String currentUserId, RequestManager glide){

        Boolean isCurrentUser = message.getUserSender().getUid().equals(currentUserId);

        this.textMessage.setText(message.getMessage());
        this.textMessage.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

        if (message.getDateCreated() != null) this.textDate.setText(this.convertDateToDay(message.getDateCreated()) + this.convertDateToHour(message.getDateCreated()));

       ((GradientDrawable) messageTextContainer.getBackground()).setColor(isCurrentUser ? currentUserColor : destinationUserColor);
        textMessage.setTextColor(isCurrentUser ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000"));

        DownloadPictureInFirebase(message.getUserSender().getUid());
        this.updateDesignDependingUser(isCurrentUser);

        if(message.getMessage() == null)
        {
            messageTextContainer.setVisibility(View.GONE);
        }

        // Update image sent ImageView
        if (message.getUrlImage() != null){
            glide.load(message.getUrlImage())
                    .into(imageCardView);
            this.imageCardView.setVisibility(View.VISIBLE);
        } else {
            this.imageCardView.setVisibility(View.GONE);
        }
    }

    private void DownloadPictureInFirebase(String user)
    {
        Task<DocumentSnapshot> task = UserHelper.getUser(user);

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
                        .into(profilePicture);
            }
        }
    }

    private void updateDesignDependingUser(Boolean isSender){


        // PROFILE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);

        if(isSender)
        {
            paramsLayoutHeader.setMargins(60, 0, 0, 0);
        }
        else
        {
            paramsLayoutHeader.setMargins(0, 0, 60, 0);
        }

        this.profileContainer.setLayoutParams(paramsLayoutHeader);

        // MESSAGE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.fci_profile_info_container);
        this.messageContainer.setLayoutParams(paramsLayoutContent);

        //CARDVIEW IMAGE SEND
        RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsImageView.addRule(isSender ? RelativeLayout.ALIGN_LEFT : RelativeLayout.ALIGN_RIGHT, R.id.fci_text_message_container);
        this.cardView.setLayoutParams(paramsImageView);
        this.mainView.requestLayout();
    }

    private String convertDateToHour(Date date){

        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        return dfTime.format(date);
    }

    private String convertDateToDay(Date date){

        DateFormat dfTime = new SimpleDateFormat("dd" + " " + "MMM" + " " + "yyyy"+ "      ");
        return dfTime.format(date);
    }
}
