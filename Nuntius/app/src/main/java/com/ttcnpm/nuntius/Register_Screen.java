package com.ttcnpm.nuntius;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;

public class Register_Screen extends Activity {
    private EditText edtusename;
    private EditText edtpassword;
    private EditText edtconfirmpassword;
    private Button btnregister;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingbar;

   @Override
    protected  void onCreate (Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.register_screen);

       edtusename =(EditText) findViewById(R.id.edt_username);
       edtpassword = (EditText) findViewById(R.id.edt_password);
       edtconfirmpassword = (EditText) findViewById(R.id.edt_confirmpassword);
       btnregister = (Button) findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);

       btnregister.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               CreateNewAccount();
           }
       });

   }

    private void CreateNewAccount() {
       String email = edtusename.getText().toString().trim();
       String password = edtpassword.getText().toString().trim();
       String confirmpass = edtconfirmpassword.getText().toString().trim();

       if(TextUtils.isEmpty(email)){
           Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
       }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Vui lòng nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(confirmpass)){
            Toast.makeText(this, "Vui lòng xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
        }
        if (!confirmpass.equals(password)) {
            Toast.makeText(this, "Mật khẩu xác nhận không hợp lý ", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Đang tạo tài khoản");
            loadingbar.setMessage("Xin vui lòng đợi ... ");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                String email = user.getEmail();
                                String uid = user.getUid();

                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("name","");
                                hashMap.put("onlineStatus","online");
                                hashMap.put("status","");
                                hashMap.put("phone","");
                                hashMap.put("image","");
                                hashMap.put("gender","");
                                hashMap.put("city","");
                                hashMap.put("cover","");

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                SendUserToLoginActivity();
                                Toast.makeText(Register_Screen.this,"Tạo tài khoản thành công",Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                            else{
                                String mess;
                                mess = task.getException().toString();
                                Toast.makeText(Register_Screen.this,"Lỗi "+ mess,Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });

        }
    }
    private void SendUserToLoginActivity() {
        Intent intent = new Intent(Register_Screen.this,Login_Screen.class);
        startActivity(intent);
    }
}


