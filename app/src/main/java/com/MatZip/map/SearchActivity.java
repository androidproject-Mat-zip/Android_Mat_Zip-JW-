package com.MatZip.map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ArrayList<Menu> menuArrayList, filteredList;
    MenuAdapter menuAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    EditText search;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent searchIntent = getIntent();
        String searchKeyword = searchIntent.getStringExtra("key");
        //keyword passed over from MapActivity is saved as variable 'searchkeyword'
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        menuArrayList = new ArrayList<>();

        //connecting data base
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("StoreData").addSnapshotListener(new EventListener<QuerySnapshot>() { //파이어스토어 "StoreData" 불러옴
            @Override
            public void onEvent(@Nullable QuerySnapshot value,  @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen failed.", e);
                    return;
                }
                int count = value.size();
                menuArrayList.clear();
                String breaktime = "";
                String regular ="";
                String imag = null;
                for (QueryDocumentSnapshot doc : value){ //get data from DB and store them as ArrayList
                    if(doc.getBoolean("breaktimeistrue")){
                        breaktime = doc.getString("breaktime");
                    }
                    else
                        breaktime = "X";
                    if(doc.get("regularholiday") != ""){
                        regular = doc.getString("regularholiday");
                    }

                    else
                        regular = "X";

                    if(doc.get("name") != null){
                        menuArrayList.add(new Menu(doc.getString("name"), doc.getString("address"), doc.getString("telephone"),
                                doc.getString("starttime"),doc.getString("endtime"), breaktime, doc.getString("img1"),
                                doc.getString("img2"),doc.getString("menu"), regular, doc.getString("category")));
                    }
                }
                searchFilter(searchKeyword); // show store searched from MapActivity as soon as collecting has ended
            }
        });


        ImageButton clear = (ImageButton) findViewById(R.id.clearB);

        clear.setOnClickListener(new View.OnClickListener() { //set search box empty
            public void onClick(View v) {
                search.setText("");
            }
        });


        RecyclerView recyclerView = findViewById(R.id.recyclerView);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        filteredList = new ArrayList<>();
        menuAdapter = new MenuAdapter(menuArrayList,this);

        search = findViewById(R.id.search);
        search.append(searchKeyword);


        recyclerView.setAdapter(menuAdapter); //set the menu listview which shows the result of searching

        menuAdapter.setOnItemClickListener(new OnMenuItemClickListener() { //show the corresponding detail page of the keyword
            @Override
            public void onItemClick(MenuAdapter.ViewHolder holder, View v, int Position) {
                Menu item = menuAdapter.getItem(Position);
                showAc(item.getName(),item.getAdd(),item.getNum(),item.getOpentime(),item.getEndtime(),
                        item.getBreakT(),item.getIma(),item.getIma2(),item.getRecommend(),item.getHoliday(), item.getCate());

            }
        });

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    String searchText = searchKeyword;
                    search.setText(searchText);
                }
            }
        });
        search.addTextChangedListener(new TextWatcher() { //detect the text changes in real time to implement search function
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //show clear icon(btn) to be invisible when search box is empty
                if(search.length() < 1){
                    clear.setVisibility(View.INVISIBLE);
                }
                else
                    clear.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) { //search with the keyword given by the search box
                String searchText = search.getText().toString();
                searchFilter(searchText);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void searchFilter(String searchText) { //search function applied to search box and so on
        filteredList.clear();

        for (int i = 0; i < menuArrayList.size(); i++) { //add to filteredList if keyword is included
            if (menuArrayList.get(i).getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(menuArrayList.get(i));
            }
        }

        menuAdapter.filterList(filteredList);

    }

    public void showAc(String name, String add, String num, String opentime, String endtime, String breakT, String ima, String ima2, String recommend, String holiday,String category) {
        //pass storeinfo to detail page(MenuDetail)

        Intent intent = new Intent(this, MenuDetail.class);

        intent.putExtra("name",name);
        intent.putExtra("add",add);
        intent.putExtra("num",num);
        intent.putExtra("opentime",opentime);
        intent.putExtra("endtime",endtime);
        intent.putExtra("breakT",breakT);
        intent.putExtra("ima",ima);
        intent.putExtra("ima2",ima2);
        intent.putExtra("recommend",recommend);
        intent.putExtra("holiday",holiday);
        intent.putExtra("category",category);

        startActivity(intent);
    }



}