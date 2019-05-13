package com.example.messenger.Activity_Invitation;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.messenger.R;
import com.example.messenger.DbHelper.InvitationHelper;
import com.example.messenger.DbHelper.UserHelper;
import com.example.messenger.Models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ManageInvitationListViewAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<User> mListInvitation;
    private Context context;
    private View view;
    private FirebaseAuth auth;

    public ManageInvitationListViewAdapter(ArrayList<User> list, Context _context)
    {
        mListInvitation = list;
        context = _context;
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return mListInvitation.size();
    }

    @Override
    public Object getItem(int position) {
        return mListInvitation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public String getUserId(int position)
    {
        return mListInvitation.get(position).getUid();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_invitation_adapter, null);
        }

        TextView friendName = (TextView)view.findViewById(R.id.list_friend_name);
        ImageView pictureProfile = (ImageView)view.findViewById(R.id.list_friend_picture_profile);
        Button btnAccept = (Button) view.findViewById(R.id.list_invitation_btn_accept);
        Button btnRefuse = (Button) view.findViewById(R.id.list_invitation_btn_refuse);

        friendName.setText(mListInvitation.get(position).getName());


        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriend(position);
                mListInvitation.remove(position);
                notifyDataSetChanged();

            }
        });

        btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeclineInvitation(position, mListInvitation.get(position).getUid());
                mListInvitation.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    private void AddFriend(int position)
    {
        //Ajout de l'ami dans la liste de l'utilisateur courrant.
        UserHelper.addFriend(FirebaseAuth.getInstance().getUid(), mListInvitation.get(position).getUid());

        //Ajout de l'utilisateur courrant dans la liste de l'autre utilisateur.
        UserHelper.addFriend(mListInvitation.get(position).getUid(), FirebaseAuth.getInstance().getUid());

        InvitationHelper.DeleteInvitation(mListInvitation.get(position).getUid(), auth.getUid());
    }

    private void DeclineInvitation(int position, String uidSenderUser)
    {
        InvitationHelper.DeleteInvitation(uidSenderUser, auth.getUid());
        mListInvitation.remove(position);
    }
}
