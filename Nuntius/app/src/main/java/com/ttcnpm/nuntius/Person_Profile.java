package com.ttcnpm.nuntius;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Person_Profile extends AppCompatActivity {

    ImageView avatarIv, coverIv;
    TextView nameTv, statusTv, emailTv, phoneTv, genderTv, cityTv;
    Button btnsendrequest, btndeclinerequest;

    private DatabaseReference FriendRequestRef, UserRef, FriendsRef;
    private FirebaseAuth mAuth;
    private String senderUserid, receiverUserid, CURRENT_STATE, saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person__profile);

        Intent intent = getIntent();
        receiverUserid = intent.getStringExtra("visit_user_id");


        mAuth = FirebaseAuth.getInstance();
        senderUserid = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        //init views
        avatarIv = (ImageView) findViewById(R.id.personavatarIv);
        nameTv = (TextView) findViewById(R.id.personnameTV);
        statusTv = (TextView) findViewById(R.id.personstatusTv);
        emailTv = (TextView) findViewById(R.id.personemailTv);
        phoneTv = (TextView) findViewById(R.id.personphoneTv);
        genderTv = (TextView) findViewById(R.id.persongenderTv);
        cityTv = (TextView) findViewById(R.id.personcityTv);
        coverIv = (ImageView) findViewById(R.id.personcoverIv);
        btnsendrequest = (Button) findViewById(R.id.btn_send_friend_request);
        btndeclinerequest = (Button) findViewById(R.id.btn_decline_friend_request);

        CURRENT_STATE = "not_friends";

        Query userQuery = UserRef.orderByChild("uid").equalTo(receiverUserid);

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until data required get
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String name = "" + ds.child("name").getValue();
                    String status = "" + ds.child("status").getValue();
                    String email = "Email: " + ds.child("email").getValue();
                    String phone = "Số điện thoại: " + ds.child("phone").getValue();
                    String gender = "Giới tính: " + ds.child("gender").getValue();
                    String image = "" + ds.child("image").getValue();
                    String city = "Thành phố: " + ds.child("city").getValue();
                    String cover = "" + ds.child("cover").getValue();
                    //set data
                    nameTv.setText(name);
                    statusTv.setText(status);
                    emailTv.setText(email);
                    phoneTv.setText(phone);
                    genderTv.setText(gender);
                    cityTv.setText(city);

                    try {
                        Picasso.get().load(image).into(avatarIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_white).into(avatarIv);
                    }

                    try {
                        Picasso.get().load(cover).into(coverIv);
                    } catch (Exception e) {

                    }
                    MaintainanceofButtons();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btndeclinerequest.setVisibility(View.INVISIBLE);
        btndeclinerequest.setEnabled(false);

        if (!senderUserid.equals(receiverUserid)) {
            btnsendrequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnsendrequest.setEnabled(false);
                    if (CURRENT_STATE.equals("not_friends")){
                        SendFriendRequestToaPerson();
                    }
                    if (CURRENT_STATE.equals("request_sent")){
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends")){
                        UnfriendAnFriend();
                    }
                }
            });
        }else {
            btndeclinerequest.setVisibility(View.INVISIBLE);
            btnsendrequest.setVisibility(View.INVISIBLE);
        }
    }

    private void UnfriendAnFriend() {
        FriendsRef.child(senderUserid).child(receiverUserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            FriendsRef.child(receiverUserid).child(senderUserid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                btnsendrequest.setEnabled(true);
                                                CURRENT_STATE ="not_friends";
                                                btnsendrequest.setText("Gửi lời mời kết bạn");

                                                btndeclinerequest.setVisibility(View.INVISIBLE);
                                                btndeclinerequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void AcceptFriendRequest() {
        Calendar calforDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calforDate.getTime());

        FriendsRef.child(senderUserid).child(receiverUserid).child("date").setValue(saveCurrentDate)
          .addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()){
                      FriendsRef.child(receiverUserid).child(senderUserid).child("date").setValue(saveCurrentDate)
                              .addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      if (task.isSuccessful()){
                                          FriendRequestRef.child(senderUserid).child(receiverUserid)
                                                  .removeValue()
                                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<Void> task) {
                                                          if (task.isSuccessful())
                                                          {
                                                              FriendRequestRef.child(receiverUserid).child(senderUserid)
                                                                      .removeValue()
                                                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                          @Override
                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                              if (task.isSuccessful()){
                                                                                  btnsendrequest.setEnabled(true);
                                                                                  CURRENT_STATE ="friends";
                                                                                  btnsendrequest.setText("Hủy kết bạn");

                                                                                  btndeclinerequest.setVisibility(View.VISIBLE);
                                                                                  btndeclinerequest.setEnabled(true);
                                                                                  btndeclinerequest.setText("Gửi tin nhắn");
                                                                                  btndeclinerequest.setOnClickListener(new View.OnClickListener() {
                                                                                      @Override
                                                                                      public void onClick(View v) {
                                                                                          Intent intent = new Intent(Person_Profile.this,ChatActivity.class);
                                                                                          intent.putExtra("hisUid", receiverUserid);
                                                                                          startActivity(intent);
                                                                                      }
                                                                                  });
                                                                              }
                                                                          }
                                                                      });
                                                          }
                                                      }
                                                  });

                                      }
                                  }
                              });

                  }
              }
          });
    }

    private void CancelFriendRequest() {
        FriendRequestRef.child(senderUserid).child(receiverUserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            FriendRequestRef.child(receiverUserid).child(senderUserid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                btnsendrequest.setEnabled(true);
                                                CURRENT_STATE ="not_friends";
                                                btnsendrequest.setText("Gửi lời mời kết bạn");

                                                btndeclinerequest.setVisibility(View.INVISIBLE);
                                                btndeclinerequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void MaintainanceofButtons() {
        FriendRequestRef.child(senderUserid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverUserid)){
                            String request_type = dataSnapshot.child(receiverUserid).child("request_type").getValue().toString();
                            if (request_type.equals("sent")){
                                CURRENT_STATE = "request_sent";
                                btnsendrequest.setText("Hủy lời mời kết bạn");
                                btndeclinerequest.setVisibility(View.INVISIBLE);
                                btndeclinerequest.setEnabled(false);
                            }
                            else if (request_type.equals("received"))
                            {
                                CURRENT_STATE = "request_received";
                                btnsendrequest.setText("Chấp nhận lời mời kết bạn");
                                btndeclinerequest.setVisibility(View.VISIBLE);
                                btndeclinerequest.setEnabled(true);

                                btndeclinerequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });

                            }
                        }
                        else {
                            FriendsRef.child(senderUserid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(receiverUserid)){
                                                CURRENT_STATE = "friends";
                                                btnsendrequest.setText("Hủy kết bạn");

                                                btndeclinerequest.setVisibility(View.VISIBLE);
                                                btndeclinerequest.setEnabled(true);
                                                btndeclinerequest.setText("Gửi tin nhắn");
                                                btndeclinerequest.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(Person_Profile.this,ChatActivity.class);
                                                        intent.putExtra("hisUid", receiverUserid);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendRequestToaPerson() {
        FriendRequestRef.child(senderUserid).child(receiverUserid)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            FriendRequestRef.child(receiverUserid).child(senderUserid)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                btnsendrequest.setEnabled(true);
                                                CURRENT_STATE ="request_sent";
                                                btnsendrequest.setText("Hủy lời mời kết bạn");

                                                btndeclinerequest.setVisibility(View.INVISIBLE);
                                                btndeclinerequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}
