package com.markin.app.view.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.markin.app.R;
import com.markin.app.callback.OnTaskCompleted;
import com.markin.app.common.AuthManager;
import com.markin.app.common.StaticValues;
import com.markin.app.connector.friend.FriendListTask;
import com.markin.app.connector.friend.FriendReceiveListTask;
import com.markin.app.connector.friend.FriendSentListTask;
import com.markin.app.connector.invitation.InvitationCodeGetTask;
import com.markin.app.model.Friend;
import com.markin.app.model.User;
import com.markin.app.view.Activity.FriendArchiveActivity;
import com.markin.app.view.Activity.FriendSearchActivity;
import com.markin.app.view.Activity.InvitatonSendActivity;
import com.markin.app.view.Activity.SettingActivity;
import com.markin.app.view.Adapter.FriendAdatper;

import java.util.ArrayList;

/**
 * Created by wonmook on 2015-03-18.
 */
public class FriendsFragment extends Fragment {

    private ImageButton settingBtn = null;
    private ImageButton searchBtn = null;
    private ImageButton invitationUseBtn = null;
    private TextView invitationTv = null;
    private TextView invitationNumTv = null;
    private TextView invitationUseTv = null;
    private ListView friendList = null;
    private ArrayList<Friend> friends = new ArrayList<Friend>();
    private FriendAdatper friendAdatper = null;
    private ProgressDialog pDialog;
    ArrayList<String> invitationNum = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friend_layout, container, false);

        settingBtn = (ImageButton) rootView.findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });
        searchBtn = (ImageButton) rootView.findViewById(R.id.friend_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendSearchActivity.class);
                startActivity(intent);
            }
        });

        invitationUseBtn = (ImageButton)rootView.findViewById(R.id.invitation_use_btn);
        invitationUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(invitationNum.size()!=0){
                    Intent intent = new Intent(getActivity(),InvitatonSendActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(StaticValues.INVITATIONCODE,invitationNum.get(0));
                    bundle.putInt(StaticValues.INVITATIONNUM, invitationNum.size());
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            }
        });

        invitationTv = (TextView)rootView.findViewById(R.id.invitation_tv);
        invitationTv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/AppleSDGothicNeo-Regular.otf"));

        invitationNumTv = (TextView) rootView.findViewById(R.id.invitation_num_tv);
        invitationNumTv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/AppleSDGothicNeo-SemiBold.otf"));

        invitationUseTv = (TextView) rootView.findViewById(R.id.invitation_use_tv);
        invitationUseTv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/AppleSDGothicNeo-Medium.otf"));

        friendList = (ListView) rootView.findViewById(R.id.friend_list);


        friendAdatper = new FriendAdatper(inflater.getContext(), friends);
        friendList.setAdapter(friendAdatper);
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (friends.get(position).getType().equals(Friend.FRIEND.FRIEND)) {
                    String user_id = friends.get(position).getUserInfo().getUserId();
                    Intent intent = new Intent(getActivity(), FriendArchiveActivity.class);
                    intent.putExtra(FriendArchiveActivity.FRIENDID, user_id);
                    startActivity(intent);

                }
            }
        });

        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setMessage("Loading....");

        return rootView;
    }

    private void updateFriendskList() {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                friendAdatper.notifyDataSetChanged();
            }
        });
    }

    private void getFriendsList() {
        OnTaskCompleted onTaskCompleted;
        onTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(Object object) {
                if (object != null) {
                    Log.d("FriendsFragment", ((ArrayList<User>) object).toString());
                    //friends.clear();
                    ArrayList<Friend> bothFriends = new ArrayList<Friend>();
                    for (int i = 0; i < ((ArrayList<User>) object).size(); ++i) {
                        bothFriends.add(new Friend(((ArrayList<User>) object).get(i), Friend.FRIEND.FRIEND));
                    }
                    friends.addAll(bothFriends);
                    updateFriendskList();
                }

                pDialog.dismiss();
            }
        };
        String user_id = (AuthManager.getAuthManager().getLoginInfo(getActivity().getSharedPreferences(AuthManager.LOGIN_PREF, Context.MODE_PRIVATE))).getUserId();
        String token = AuthManager.getAuthManager().getToken(getActivity().getSharedPreferences(AuthManager.LOGIN_PREF, Context.MODE_PRIVATE));
        pDialog.show();
        new FriendListTask(onTaskCompleted, user_id, token).execute();
    }

    private void getFriendsSentList() {
        OnTaskCompleted onTaskCompleted;
        onTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(Object object) {
                if (object != null) {
                    Log.d("FriendsFragment", ((ArrayList<User>) object).toString());
                    //friends.clear();
                    ArrayList<Friend> sentFriends = new ArrayList<Friend>();
                    for (int i = 0; i < ((ArrayList<User>) object).size(); ++i) {
                        sentFriends.add(new Friend(((ArrayList<User>) object).get(i), Friend.FRIEND.FRIENDSENT));
                    }
                    friends.addAll(sentFriends);
                    updateFriendskList();
                }

                pDialog.dismiss();
            }
        };
        String user_id = (AuthManager.getAuthManager().getLoginInfo(getActivity().getSharedPreferences(AuthManager.LOGIN_PREF, Context.MODE_PRIVATE))).getUserId();
        String token = AuthManager.getAuthManager().getToken(getActivity().getSharedPreferences(AuthManager.LOGIN_PREF, Context.MODE_PRIVATE));
        new FriendSentListTask(onTaskCompleted, user_id, token).execute();
    }

    private void getFriendsReceiveList() {
        OnTaskCompleted onTaskCompleted;
        onTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(Object object) {
                friends.clear();
                if (object != null) {
                    Log.d("FriendsFragment", ((ArrayList<User>) object).toString());
                    //friends.clear();
                    ArrayList<Friend> receiveFriends = new ArrayList<Friend>();
                    for (int i = 0; i < ((ArrayList<User>) object).size(); ++i) {
                        receiveFriends.add(new Friend(((ArrayList<User>) object).get(i), Friend.FRIEND.FRIENDRECEIVE));
                    }
                    friends.addAll(receiveFriends);
                    updateFriendskList();
                }

                pDialog.dismiss();
            }
        };
        String user_id = (AuthManager.getAuthManager().getLoginInfo(getActivity().getSharedPreferences(AuthManager.LOGIN_PREF, Context.MODE_PRIVATE))).getUserId();
        String token = AuthManager.getAuthManager().getToken(getActivity().getSharedPreferences(AuthManager.LOGIN_PREF, Context.MODE_PRIVATE));
        new FriendReceiveListTask(onTaskCompleted, user_id, token).execute();
    }

    private void getInvitation(){
        OnTaskCompleted onTaskCompleted;
        onTaskCompleted = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(Object object) {
                if(object!=null){
                    invitationNum.clear();
                    invitationNum.addAll((ArrayList<String>)object);
                }
                invitationNumTv.setText(invitationNum.size()+"장");
            }
        };
        String user_id = (AuthManager.getAuthManager().getLoginInfo(getActivity().getSharedPreferences(AuthManager.LOGIN_PREF, Context.MODE_PRIVATE))).getUserId();
        String token = AuthManager.getAuthManager().getToken(getActivity().getSharedPreferences(AuthManager.LOGIN_PREF, Context.MODE_PRIVATE));
        new InvitationCodeGetTask(onTaskCompleted, user_id, token).execute();

    }

    @Override
    public void onResume(){
        super.onResume();
        getFriendsReceiveList();
        getFriendsSentList();
        getFriendsList();
        getInvitation();

    }

}
