package com.ttcnpm.nuntius;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.MemoryFile;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //storage
    StorageReference storageReference;
    // path where images of user profile and cover will be storaged
    String storagePath = "Users_Profile_Cover_Imgs/";

    Button btnchangepass;

    ImageView avatarIv, coverIv;
    TextView nameTv,statusTv,emailTv,phoneTv,genderTv,cityTv;
    FloatingActionButton fab;


    //progress dialog
    ProgressDialog pd;

    //permission
    private  static  final  int CAMERA_REQUEST_CODE = 100;
    private  static  final  int STORAGE_REQUEST_CODE = 200;
    private  static  final  int IMAGE_PICK_GALLERY_CODE = 300;
    private  static  final  int IMAGE_PICK_CAMERA_CODE = 400;

    //arrays of persmissions to be requested

    String cameraPermissions[];
    String storagePermissions[];

    //uri of image pick
    Uri image_uri;
    // for checking profile or cover
    String profileOrCoverPhoto;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();




        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference();
        //init arrays of permissions
        cameraPermissions = new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnchangepass = (Button) view.findViewById(R.id.btn_change_pass);
        btnchangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getActivity(),Change_Password.class);
                startActivity(intent);
            }
        });

        //init views
        avatarIv = view.findViewById(R.id.avatarIv);
        nameTv = view.findViewById(R.id.nameTV);
        statusTv = view.findViewById(R.id.statusTv);
        emailTv = view.findViewById(R.id.emailTv);
        phoneTv = view.findViewById(R.id.phoneTv);
        genderTv = view.findViewById(R.id.genderTv);
        cityTv = view.findViewById(R.id.cityTv);
        coverIv = view.findViewById(R.id.coverIv);
        fab = view.findViewById(R.id.fab);

        pd = new ProgressDialog(getActivity());
        //find database by key email
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // fab button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });


        return view;
    }

    private  boolean checkStoragePermission(){
        //check xem có cho ko, nếu cho true, không false;
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == (PackageManager.PERMISSION_GRANTED);
            return  result;

    }
    private  void requestStoragePermission(){
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }


    private  boolean checkCameraPermission(){
        //check xem có cho ko, nếu cho true, không false;

        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;

    }
    private  void requestCameraPermission(){
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
    }




    private void showImagePicDialog() {
        // show dialog với option là camera và gallery


        String options []= {"Máy ảnh","Thư viện ảnh"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chọn ảnh từ");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //xử lý item được click
                if (which == 0) {
                    // chọn camera
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickFromCamera();
                    }

                } else if (which == 1) {
                    // chọn gallery
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        PickFromGallery();
                    }
                }
            }
        });
        //create and show dialog

        builder.create().show();


    }
    private void showEditProfileDialog() {
        //show dialogs have many options
        String options []= {"Chỉnh sửa ảnh đại diện","Chỉnh sửa cover",
        "Chỉnh sửa tên", "Chỉnh sửa số điện thoại","Chỉnh sửa giới tính"
        ,"Chỉnh sửa thành phố"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chọn hành động");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //xử lý item được click
                if (which == 0){
                    pd.setMessage("Đang cập nhật ảnh đại diện");
                    profileOrCoverPhoto = "image";
                    showImagePicDialog();
                }
                else if (which ==1){
                    profileOrCoverPhoto = "cover";
                    showImagePicDialog();
                    pd.setMessage("Đang cập nhật cover");

                }else if (which ==2){
                    pd.setMessage("Đang cập nhật tên");
                    showOthersUpdateDialog("name");

                }else if (which ==3){
                    pd.setMessage("Đang cập nhật số điện thoại");
                    showOthersUpdateDialog("phone");
                }else if (which ==4){
                    pd.setMessage("Đang cập nhật giới tính");
                    showOthersUpdateDialog("gender");
                }else if (which ==5){
                    pd.setMessage("Đang cập nhật thành phố");
                    showOthersUpdateDialog("city");

                };

            }
        });
        //create and show dialog

        builder.create().show();

    }

    private void showOthersUpdateDialog(final String key) {
        //key chứa các tên value cần sửa

        //custom dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cập nhật " + key);
        //set layout của dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Nhập vào "  + key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        //add button to update
        builder.setPositiveButton("Cập nhật ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Lấy edit text chuyển thành string
                String value = editText.getText().toString().trim();
                // check xem user có nhập gì không
                if (!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String,Object> result = new HashMap<>();
                    result.put(key,value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // update thành công
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Đang cập nhật ..." ,Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //update lỗi
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),""+ e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });


                }
                else{
                    Toast.makeText(getActivity(),"Xin nhập vào "+key, Toast.LENGTH_SHORT).show();
                }

            }
        });
        //add button to cancel
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        //create and show dialog
        builder.create().show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //allow and denied
         switch (requestCode) {
             case CAMERA_REQUEST_CODE: {
                 if (grantResults.length > 0) {
                     boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                     boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                     if (cameraAccepted && writeStorageAccepted) {
                         //cho phép
                         pickFromCamera();
                     } else {
                         //Không cho
                         Toast.makeText(getActivity(), "Xin hãy cho phép truy cập máy ảnh và lưu trữ", Toast.LENGTH_SHORT).show();
                     }

                 }
             }
             break;
             case STORAGE_REQUEST_CODE: {
                 if (grantResults.length > 0) {
                     boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                     if (writeStorageAccepted) {
                         //cho phép
                         PickFromGallery();
                     } else {
                         //Không cho
                         Toast.makeText(getActivity(), "Xin hãy cho phép truy cập lưu trữ", Toast.LENGTH_SHORT).show();
                     }

                 }

             }
             break;
         }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // được gọi sau khi lấy hình từ camera hoặc thư viện
        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //Hình lấy từ thư viện, lấy uri của hình
                image_uri= data.getData();
                uploadProfilePhoto(image_uri);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                uploadProfilePhoto(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfilePhoto(final Uri uri) {
        //show progress
        pd.show();

        //func được sử dụng cho cả profile và cover, "image" sd cho profile,"cover" sd cho cover

        //path và tên của hình được store
        String filePathAndName = storagePath + "" + profileOrCoverPhoto + "" + user.getUid();

        StorageReference  storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // hình được upload tới storage, giờ lấy uri và lưu vào database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //check xem hình được upload // uri lấy được chưa
                        if (uriTask.isSuccessful()){
                            // hìnnh được upload
                            // update url trong user database
                            HashMap<String,Object> results = new HashMap<>();
                            results.put(profileOrCoverPhoto,downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //add url vào database thành công
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Đang update hình...", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(),"Lỗi xảy ra khi update hình!",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            //error
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Vài lỗi xảy ra..",Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // hiện lỗi, xóa pd
                        pd.dismiss();
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }


    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to start camera
        Intent cameraIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }

    private void PickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);

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
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(),Splash_Screen.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
