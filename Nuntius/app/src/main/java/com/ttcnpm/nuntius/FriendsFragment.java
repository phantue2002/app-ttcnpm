package com.ttcnpm.nuntius;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView myFriendList;
    private DatabaseReference FriendsReference;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersReference;
    String online_user_id;
    private View myMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        myMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        myFriendList = (RecyclerView) myMainView.findViewById(R.id.friend_list);
        myFriendList.setLayoutManager(new LinearLayoutManager(getContext()));
        myFriendList.setHasFixedSize(true);


        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");



        // Inflate the layout for this fragment
        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(FriendsReference, Friends.class)
                .build();

         FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int i, @NonNull Friends friends) {
                    holder.setDate(friends.getDate());

                    String list_user_id = getRef(i).getKey();
                    UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String userName = dataSnapshot.child("name").getValue().toString();
                            String thumbImage = dataSnapshot.child("image").getValue().toString();
                            String email = dataSnapshot.child("email").getValue().toString();
                            final String hisUID = dataSnapshot.child("uid").getValue().toString();

                            holder.email.setText(email);
                            holder.usernameDisplay.setText(userName);
                            try {
                                Picasso.get().load(thumbImage)
                                        .placeholder(R.drawable.ic_default_img)
                                        .into(holder.avatarIv);
                            }
                            catch  (Exception e){

                            }

                          holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), Person_Profile.class);
                                    intent.putExtra("visit_user_id", hisUID);
                                    startActivity(intent);
                                }
                            });

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend,parent,false);
                FriendsViewHolder viewHolder = new FriendsViewHolder(view);
                return viewHolder;
            }
        };
        myFriendList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        ImageView avatarIv;
        TextView DateTv,usernameDisplay,email;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            avatarIv = itemView.findViewById(R.id.friends_profile_image);
            DateTv = itemView.findViewById(R.id.datefriends);
            email = itemView.findViewById(R.id.emailfriends);
            usernameDisplay = (TextView) itemView.findViewById(R.id.usernamefriends);
        }


        public void setDate (String date){
            DateTv.setText("Kết bạn từ ngày " + date);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_logout) {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(),Splash_Screen.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
