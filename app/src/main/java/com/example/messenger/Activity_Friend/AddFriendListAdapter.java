package com.example.messenger.Activity_Friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.messenger.R;
import com.example.messenger.DbHelper.InvitationHelper;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.Models.Invitation;
import com.example.messenger.Models.ResearchUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class AddFriendListAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<ResearchUser> mListUsers;
    private ArrayList<Invitation> mListInvitation = new ArrayList<>();
    private Context context;
    private View view;
    private FirebaseAuth auth;
    private Boolean invitationSended;

    TextView txt_sended;

    public AddFriendListAdapter(ArrayList<ResearchUser> list, Context _context)
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
        return mListUsers.get(position).getUserTarget().getUid();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_user_adapter, null);
        }

        TextView itemUserName = (TextView)view.findViewById(R.id.list_user_name);
        Button btnAddFriend = (Button)view.findViewById(R.id.btn_add_friend);
        TextView textAlreadyUser = (TextView)view.findViewById(R.id.list_user_already_friend);
        TextView txt_inv = (TextView)view.findViewById(R.id.txt_invitation_sended);
        ImageButton btnCancel = (ImageButton)view.findViewById(R.id.btn_cancel_invitation);

        Boolean isAFriend = CheckIfIsAFriend(mListUsers.get(position).getUserTarget().getUid());

        if(mListUsers.get(position).getInvitationSended())
        {
            btnAddFriend.setVisibility(View.INVISIBLE);
            textAlreadyUser.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            txt_inv.setVisibility(View.VISIBLE);
        }
        else if(isAFriend == true)
        {
            btnCancel.setVisibility(View.INVISIBLE);
            txt_inv.setVisibility(View.INVISIBLE);
            btnAddFriend.setVisibility(View.INVISIBLE);
            textAlreadyUser.setVisibility(View.VISIBLE);
        }
        else
        {
            btnAddFriend.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.INVISIBLE);
            txt_inv.setVisibility(View.INVISIBLE);
            textAlreadyUser.setVisibility(View.INVISIBLE);
        }
        itemUserName.setText(mListUsers.get(position).getUserTarget().getName());

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListUsers.get(position).setInvitationSended(true);
                notifyDataSetChanged();

                SendInvitation(mListUsers.get(position).getUserTarget().getUid());
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListUsers.get(position).setInvitationSended(false);
                notifyDataSetChanged();

                CancelInvitation(mListUsers.get(position).getUserTarget().getUid());
            }
        });

        return view;
    }
    private void SendInvitation(String uidInvitedFriend)
    {
        InvitationHelper.AddInvitation(auth.getUid(), uidInvitedFriend);
    }

    private void CancelInvitation(String uidInvitedFriend)
    {
        InvitationHelper.DeleteInvitation(auth.getUid(), uidInvitedFriend);
    }

    private Boolean CheckIfIsAFriend(String userUid)
    {
        DocumentReference docReference = UserHelper.getUsersCollection().document(FirebaseAuth.getInstance().getUid()).collection("friends").document(userUid);

        Task<DocumentSnapshot> task =  docReference.get();

        while (!task.isComplete()){
            //standby
        }

        DocumentSnapshot doc = task.getResult();

        if(doc.exists())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
