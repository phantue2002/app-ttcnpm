package com.ttcnpm.nuntius;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Splash_Screen extends Activity {
    private Button btndangky;
    private Button btndangnhap;
    @Override
    protected void onCreate (Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.splash_screen);
        btndangky=(Button) findViewById(R.id.btnDangKy);
        btndangnhap = (Button) findViewById(R.id.btnDangNhap);
        btndangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Splash_Screen.this,Register_Screen.class);
                startActivity(intent);
            }
        }
        );
        btndangnhap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Splash_Screen.this,Login_Screen.class);
                startActivity(intent);
            }
        });
}
}
