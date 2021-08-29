package com.MatZip.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

public class MenuDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //get storeinfo that is to be shown to detail page from SeachActivity intent
        //and set those on textviews
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        TextView tx = (TextView) findViewById(R.id.textView3);
        tx.setText(bundle.getString("name"));
        TextView tx2 = (TextView) findViewById(R.id.textView4);
        tx2.setText(bundle.getString("add"));
        TextView tx3 = (TextView) findViewById(R.id.textView5);
        tx3.setText("◦전화번호: "+bundle.getString("num"));
        TextView tx4 = (TextView) findViewById(R.id.textView7);
        tx4.setText("◦운영시간: "+bundle.getString("opentime") +" ~ "+ bundle.getString("endtime"));
        TextView tx6 = (TextView) findViewById(R.id.textView8);
        tx6.setText("◦브레이크 타임: "+bundle.getString("breakT"));
        TextView tx8 = (TextView) findViewById(R.id.textView13);
        tx8.setText("◦정기휴일: "+bundle.getString("holiday"));
        TextView tx7 = (TextView) findViewById(R.id.textView11);
        tx7.setText("★추천메뉴: "+bundle.getString("recommend"));
        TextView tx9 = (TextView) findViewById(R.id.textView15);
        tx9.setText("◦카테고리: "+bundle.getString("category"));

        ImageView ima = findViewById((R.id.imageView2));

        ImageView ima2 = findViewById((R.id.imageView4));

        //open images of menu and show them (got images by url links in DB)
        //show error image if there is no image
        //show loading image if it takes time to get images
        Glide.with(this)
                .load(bundle.getString("ima"))
                .transform(new CenterCrop(), new RoundedCorners(30))
                .override(250,250)
                .error(R.drawable.nopicture)
                .placeholder(R.drawable.img_loading)
                .into(ima);

        Glide.with(this)
                .load(bundle.getString("ima2"))
                .transform(new CenterCrop(), new RoundedCorners(30))
                .override(250,250)
                .error(R.drawable.nopicture)
                .placeholder(R.drawable.img_loading)
                .into(ima2);

        tx.setSelected(true);      // 선택하기
    }

    //if calling button is clicked, directly shows calling page
    public void call(View v){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Intent I = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+bundle.getString("num")));
        startActivity(I);
    }

    //goes back(page just before)
    public void backI(View v){
        Intent intent = getIntent();
        finish();
    }



}