package com.ttcnpm.nuntius;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Change_Password extends AppCompatActivity {
    private Button changepass;
    private EditText edtcurrent, edtnew, edtnewconf;
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__password);

        edtcurrent = (EditText) findViewById(R.id.edit_currentpass);
        edtnew = (EditText) findViewById(R.id.edit_newpass);
        edtnewconf = (EditText) findViewById(R.id.edit_confirmnewpass);

        dialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        changepass = (Button) findViewById(R.id.btn_change_pass_real);
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

    }

    private void changePassword() {
        String currentpass = edtcurrent.getText().toString().trim();
        String newpass = edtnew.getText().toString().trim();
        String confpass = edtnewconf.getText().toString().trim();
        if (TextUtils.isEmpty(currentpass)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(newpass)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(confpass)) {
            Toast.makeText(this, "Vui lòng xác nhận mật khẩu mới", Toast.LENGTH_SHORT).show();
        }
        if (!newpass.equals(confpass)) {
            Toast.makeText(this, "Mật khẩu xác nhận không hợp lý ", Toast.LENGTH_SHORT).show();
        } else {
            user = auth.getCurrentUser();
            auth.signInWithEmailAndPassword(user.getEmail(),currentpass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //mật khẩu hiện tại đúng
                                if (user != null) {
                                    dialog.setMessage("Đang đổi mật khẩu, xin đợi...");
                                    dialog.show();
                                    String newpass = edtnew.getText().toString().trim();
                                    user.updatePassword(newpass)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        Toast.makeText(Change_Password.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                        auth.signOut();
                                                        Intent intent = new Intent(Change_Password.this, Login_Screen.class);
                                                        startActivity(intent);
                                                    } else {
                                                        dialog.dismiss();
                                                        Toast.makeText(Change_Password.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }

                            } else {
                                Toast.makeText(Change_Password.this, "Mật khẩu hiện tại không hợp lý ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



        }

    }
}

