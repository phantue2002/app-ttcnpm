package com.ttcnpm.nuntius;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Password_Screen extends Activity {

    private EditText emailforgotpass;
    private Button resetpass;
    private ProgressDialog loadingBar;



    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_screen);
        emailforgotpass = (EditText) findViewById(R.id.emailforgotpass);
        resetpass = (Button) findViewById(R.id.btnforgotpass);
        firebaseAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("Đang gửi tới email");
                loadingBar.setMessage("Xin vui lòng đợi ... ");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                firebaseAuth.sendPasswordResetEmail(emailforgotpass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(Forgot_Password_Screen.this,"Đã gửi link tới email",Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                                else{
                                    String mess;
                                    mess = task.getException().toString();
                                    Toast.makeText(Forgot_Password_Screen.this,"Lỗi "+ mess,Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
            }
        });

    }
}
