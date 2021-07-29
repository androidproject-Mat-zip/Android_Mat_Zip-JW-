package com.example.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MyPageActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set custom action bar
        ActionBar actionBar = getSupportActionBar();
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customActionBar = LayoutInflater.from(this).inflate(R.layout.custom_actionbar,null);
        actionBar.setCustomView(customActionBar);

        setContentView(R.layout.activity_mypage);
        Intent searchIntent = getIntent();
        TextView version = findViewById(R.id.version);
        TextView contact = findViewById(R.id.contact);
        Button form = findViewById(R.id.form);


        //set information on textviews
        version.append("\n   ver.1.0.0");
        contact.append("\n   노장우: ssk01017@gmail.com\n   정지연: poptanza@g.skku.edu\n   주정윤: yoon913612@gmail.com");
        form.setText("※건의/문의는 여기를 눌러주세요");

        //if form button is clicked, it shows google form to gather idea of users
        form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forms.gle/jr2MtEi5xYLBPz8d8"));
                startActivity(intent);
            }
        });
    }
}
