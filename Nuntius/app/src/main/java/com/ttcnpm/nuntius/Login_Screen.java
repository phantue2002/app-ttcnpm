package com.ttcnpm.nuntius;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;

public class Login_Screen extends Activity {
    private EditText edtusernamelogin;
    private EditText edtpasswordlogin;
    private TextView forgotpass;
    private Button btnloginlogin;
    private Button btnregisterlogin;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        loadingBar = new ProgressDialog(this);
        edtusernamelogin = (EditText) findViewById(R.id.edt_usernamelogin);
        edtpasswordlogin = (EditText) findViewById(R.id.edt_passwordlogin);
        btnloginlogin = (Button) findViewById(R.id.btn_loginlogin);
        btnregisterlogin = (Button) findViewById(R.id.btn_registerlogin);
        forgotpass = (TextView) findViewById(R.id.forgotpass);

        btnloginlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();

            }
        });

        btnregisterlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_Screen.this, Register_Screen.class);
                startActivity(intent);
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Screen.this, Forgot_Password_Screen.class);
                startActivity(intent);
            }
        });

    }

    private void AllowUserToLogin() {
        String email = edtusernamelogin.getText().toString().trim();
        String password = edtpasswordlogin.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập lại mật khẩu", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Đang đăng nhập");
            loadingBar.setMessage("Xin vui lòng đợi ... ");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login_Screen.this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                SendUserToHomeScreen();
                            }
                            else{
                                String mess;
                                mess = task.getException().toString();
                                Toast.makeText(Login_Screen.this,"Lỗi "+ mess,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    };

    private void SendUserToHomeScreen() {
        // chuyển đến màn hình Home
        Intent intent = new Intent (Login_Screen.this,dashboard.class);
        startActivity(intent);
    }
    @Override
    protected void onStart(){
        super.onStart();
        if (currentUser!=null){
            SendUserToHomeScreen();
        }
    }
}