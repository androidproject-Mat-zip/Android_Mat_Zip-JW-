package com.MatZip.map;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.MarkerIcons;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, Overlay.OnClickListener {

    private static NaverMap naverMap;
    protected MapFragment mapFragment;

    public String selected_category= "total";
    public String selected_region;

    public String[] drag_info;

    public double changed_lat;
    public double changed_lng;
    public double changed_zoom;

    public int unchanged=1;

    Menu storeInfo;
    ArrayList<Menu> storeSet_total;
    ArrayList<Marker> markerSet;
    ArrayList<Menu> storeSet;

    String prev_storeName="sample";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        markerSet = new ArrayList<>();
        storeSet = new ArrayList<>();
        storeSet_total = new ArrayList<>();


        ActionBar actionBar = getSupportActionBar();
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        View customActionBar = LayoutInflater.from(this).inflate(R.layout.custom_actionbar,null);
        actionBar.setCustomView(customActionBar);


        //map fragment setting

        String pincode = "##should get from Naver Cloud Platform##";

        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(pincode));
        setContentView(R.layout.activity_map);

        FragmentManager fm = getSupportFragmentManager();
        mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);


        //get the data from the firebase.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("StoreData").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen failed.", e);
                    return;
                }
                String breaktime = "";
                String regular ="";

                for (QueryDocumentSnapshot doc : value){
                    if (doc.getBoolean("breaktimeistrue")!=null&&doc.getBoolean("breaktimeistrue")) {
                        breaktime = doc.getString("breaktime");
                    } else
                        breaktime = "X";
                    if (doc.get("regularholiday")!=null&&doc.get("regularholiday") != "") {
                        regular = doc.getString("regularholiday");
                    } else
                        regular = "X";
                    if (doc.get("name") != null) {
                        storeSet_total.add(new Menu(doc.getString("name"), doc.getString("address"), doc.getString("telephone"),
                                doc.getString("starttime"), doc.getString("endtime"), breaktime, doc.getString("img1"),
                                doc.getString("img2"), doc.getString("menu"), regular, doc.getDouble("latitude"),
                                doc.getDouble("longitude"), doc.getString("category")));
                        //initially get all the data from the firebase
                        //storeInfo that we need will be obtained from this arrayList(storeSet_total)
                        refresh();
                    }

                }
            }
        });


        //spinner_category setting
        String[] items_category = {"모든 맛집", "한식", "양식", "중식", "일식", "분식",
                "동남아식", "야식", "회/조개구이집", "술집", "카페/디저트", "샐러드", "etc"};

        Spinner spnr_category = findViewById(R.id.categoryList);
        ArrayAdapter<String> adapter_category = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items_category){

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/nanumsquare_acb.ttf");
                ((TextView) v).setTypeface(externalFont);

                return v;
            }
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/nanumsquare_acb.ttf");
                ((TextView) v).setTypeface(externalFont);
                return v;
            }
        };

        adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_category.setAdapter(adapter_category);
        spnr_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    selected_category = items_category[position];

                    //clear previous markers from the map
                    for(int i=0;i<markerSet.size();i++){
                        markerSet.get(i).setMap(null);
                    }
                    markerSet.clear();//initiate
                    storeSet.clear();//initiate


                    //find the matching stores
                    for(int i=0;i<storeSet_total.size();i++){
                        if(storeSet_total.get(i).cate.equals(selected_category)){
                            storeSet.add(storeSet_total.get(i));
                        }
                    }

                }

                else{

                    //clear previous markers from the map
                    for(int i=0;i<markerSet.size();i++){
                        markerSet.get(i).setMap(null);
                    }
                    markerSet.clear();//initiate
                    storeSet.clear();//initiate


                    //find the every store
                    for(int i=0;i<storeSet_total.size();i++){
                        storeSet.add(storeSet_total.get(i));
                    }

                }
                //Toast.makeText(getApplicationContext(), storeSet_total.size() + "개의 전체 가게 "+ storeSet.size() + "개의 찾은 가게 ", Toast.LENGTH_SHORT).show();

                //set the markers with proper latitudes and longitudes
                for(int i=0;i<storeSet.size();i++){
                    Marker marker = new Marker();
                    marker.setPosition(new LatLng(storeSet.get(i).lat, storeSet.get(i).lng));
                    markerSet.add(marker);
                }

                refresh();//to set the markers on the map by calling the method "onMapReady"
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_category = "not selected";
            }
        });

        //spinner_region setting
        String[] items_region = {"지역 선택", "서울특별시", "부산광역시", "인천광역시", "대구광역시", "광주광역시",
                "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원도", "충청북도", "충청남도", "전라북도",
                "전라남도", "경상북도", "경상남도", "제주특별자치도"};

        Spinner spnr_region = findViewById(R.id.regionList);
        ArrayAdapter<String> adapter_region = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items_region){

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/nanumsquare_acb.ttf");
                ((TextView) v).setTypeface(externalFont);

                return v;
            }
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/nanumsquare_acb.ttf");
                ((TextView) v).setTypeface(externalFont);
                return v;
            }
        };


        adapter_region.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_region.setAdapter(adapter_region);
        spnr_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    selected_region = items_region[position];
                    unchanged=0;
                    Toast.makeText(getApplicationContext(), selected_region + " 내의 맛집을 찾습니다···", Toast.LENGTH_SHORT).show();
                }
                else{
                    selected_region = "not selected";
                    unchanged=1;
                }
                refreshMap(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_region = "not selected";
            }
        });

        //spinner size setting
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            ListPopupWindow window1 = (ListPopupWindow)popup.get(spnr_category);
            ListPopupWindow window2 = (ListPopupWindow)popup.get(spnr_region);
            window1.setHeight(600);
            window2.setHeight(600);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //search function
        ImageView img_search = findViewById(R.id.searchIcon);
        //when clicked, method 'search' will be called


        //drag_info setting
        //which will be displayed on the sliding up panel below.
        String location= "오늘도 함께";
        String mainMenu= "어마어마한 맛집을";
        String phNum= "찾아볼까요?";

        drag_info = new String[]{location, mainMenu, phNum} ;
        ArrayAdapter adapter_drag = new ArrayAdapter(this, android.R.layout.simple_list_item_1, drag_info){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/nanumsquare_aceb.ttf");
                ((TextView) v).setTypeface(externalFont);

                return v;
            }
        };
        ListView listview_dragInfo = (ListView) findViewById(R.id.infoView);
        listview_dragInfo.setAdapter(adapter_drag);


        Intent intent_mypage = new Intent(this, MyPageActivity.class);
        Button btnMypage = findViewById(R.id.mypage);
        btnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent_mypage.putExtra("mypage","mypage");
                startActivity(intent_mypage);
            }
        });


        Intent intent = new Intent(this, MenuDetail.class);
        Button btnMoreInfo = findViewById(R.id.details);
        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //storeInfo has been set when the matching marker was clicked(onClick method).
                if(storeInfo!=null){
                    if(prev_storeName!=""){
                        //send storeinfo to detail page
                        intent.putExtra("ima",storeInfo.getIma());
                        intent.putExtra("ima2",storeInfo.getIma2());
                        intent.putExtra("name",storeInfo.getName());
                        intent.putExtra("add",storeInfo.getAdd());
                        intent.putExtra("num",storeInfo.getNum());
                        intent.putExtra("opentime",storeInfo.getOpentime());
                        intent.putExtra("endtime",storeInfo.getEndtime());
                        intent.putExtra("breakT",storeInfo.getBreakT());
                        intent.putExtra("recommend",storeInfo.getRecommend());
                        intent.putExtra("holiday",storeInfo.getHoliday());
                        intent.putExtra("category",storeInfo.getCate());
                        startActivity(intent);
                    }

                }

            }

        });

        SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.panel);
        slidingUpPanelLayout.setAnchorPoint(0.70f);
        //slidingUpPanelLayout.setAnchorPoint(0.50f);
        refresh();
    }


    public void search(View view){
        //move to Search page if keyword exists.
        String keyword;
        EditText search_keyword = findViewById(R.id.searchName);
        keyword = search_keyword.getText().toString();
        if ( keyword.length() == 0 ) {
            return;
        } else {
            search_keyword.setText("");
            //send search keyword to search page
            Intent searchIntent = new Intent(this, SearchActivity.class);
            searchIntent.putExtra("key", keyword );
            startActivity(searchIntent);
        }

    }

    //method to reload or reset the map by calling the method 'onMapReady'
    public void refresh() {
        mapFragment.getMapAsync(this);
    }

    //change the zoom level and focus of camera of the map
    //and call the method 'onMapReady' to reflect such changes
    //called when searching region was changed by selecting one from the 'spinner of region'.
    public void refreshMap(int location) {
        double lat=0;
        double lng=0;
        int zoom=0;
        switch (location){
            //the location(number) is same to the position of regions in 'spinner of region'.
            case 0:
                break;
            case 1: //서울
                lat=37.56670337994092;
                lng=126.97827769117319;
                break;
            case 2: //부산
                lat=35.16279131157764;
                lng=129.0531428903312;
                break;
            case 3: //인천
                lat=37.47530981758167;
                lng=126.63216890930761;
                break;
            case 4: //대구
                lat=35.85295430030442;
                lng=128.56415189986518;
                break;
            case 5: //광주
                lat=35.15568698411686;
                lng=126.83526414350017;
                break;
            case 6: //대전
                lat=36.33983706083922;
                lng=127.39410448225958;
                break;
            case 7: //울산
                lat=35.54334038205928;
                lng=129.3303116526885;
                break;
            case 8: //세종
                lat=36.50274939485238;
                lng=127.2617372772972;
                break;
            case 9: //경기도
                lat=37.521380446799505;
                lng=127.22648477603475;
                break;
            case 10: //강원도
                lat=37.72009426338668;
                lng=128.45850437372758;
                break;
            case 11: //충청북도
                lat=36.76710955420391;
                lng=127.81839395532916;
                break;
            case 12: //충청남도
                lat=36.56572163448709;
                lng=126.87890282115612;
                break;
            case 13: //전라북도
                lat=35.79367998440018;
                lng=127.13799354938766;
                break;
            case 14: //전라남도
                lat=34.97728289418079;
                lng=126.9998635731112;
                break;
            case 15: //경상북도
                lat=36.32023605674789;
                lng=128.88645643319023;
                break;
            case 16: //경상남도
                lat=35.25265601924392;
                lng=128.30787623948623;
                break;
            case 17: //제주도
                lat=33.39140432446389;
                lng=126.56184633325132;
                break;

        }

        //bigger the zoom level is, more magnified the map is
        if(location<9){
            zoom=11;
        }
        else{
            zoom=8;
        }

        changed_lat = lat;
        changed_lng = lng;
        changed_zoom = zoom;
        mapFragment.getMapAsync(this);

    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        if(unchanged==1){ //to initial setting
            naverMap.setMapType(NaverMap.MapType.Navi);
            naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);
            CameraPosition cameraPosition = new CameraPosition(
                    new LatLng(37.5670135, 126.9783740), 12, 0, 0);
            naverMap.setCameraPosition(cameraPosition);

            if(selected_category.equals("total")) {
                //clear previous markers from the map
                for (int i = 0; i < markerSet.size(); i++) {
                    markerSet.get(i).setMap(null);
                }
                markerSet.clear();//initiate
                storeSet.clear();//initiate


                //find the every store
                for (int i = 0; i < storeSet_total.size(); i++) {
                    storeSet.add(storeSet_total.get(i));
                }
                //set the markers with proper latitudes and longitudes
                for (int i = 0; i < storeSet.size(); i++) {
                    Marker marker = new Marker();
                    marker.setPosition(new LatLng(storeSet.get(i).lat, storeSet.get(i).lng));
                    markerSet.add(marker);
                }
            }

        }
        else if (unchanged==3){
            //don't change anything about map settings
        }

        else{
            //change zoom level and focus of the map.
            //when user selected the region to search.
            //changed_lat, changed_lng, changed_zoom is set by the method 'refreshMap'

            naverMap.setMapType(NaverMap.MapType.Navi);
            naverMap.setLayerGroupEnabled(naverMap.LAYER_GROUP_BUILDING, true);
            CameraPosition cameraPosition = new CameraPosition(
                    new LatLng(changed_lat, changed_lng), changed_zoom, 0, 0);
            naverMap.setCameraPosition(cameraPosition);

        }

        //set markers into initial settings(no caption, Red icon) and set those on the map
        for(int i=0;i<markerSet.size();i++){
            markerSet.get(i).setIcon(MarkerIcons.RED);
            markerSet.get(i).setCaptionText("");
            markerSet.get(i).setOnClickListener(this);
            markerSet.get(i).setMap(naverMap);
        }

    }



    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        //when Marker is clicked
        if(overlay instanceof Marker){

            unchanged=3; //not to change the zoom level and camera position of the map

            int isClicked_before = 0;
            if( ((Marker) overlay).getIcon() == MarkerIcons.BLUE){
                isClicked_before = 1;
            }

            refresh();//to set markers into initial settings(red, no caption)

            for(int i=0;i<storeSet.size();i++){
                if((Double.valueOf(storeSet.get(i).lat) == ((Marker) overlay).getPosition().latitude)
                        && (Double.valueOf(storeSet.get(i).lng) == ((Marker) overlay).getPosition().longitude) ) {
                    //if lat&lng of marker equals lat&lng of store, show the info of store to sliding up panel below.

                    if(isClicked_before==1){//(((Marker) overlay).getCaptionText().length()>0)
                        //if the marker has already been clicked(=if marker is the blue one)
                        ((Marker) overlay).setIcon(MarkerIcons.RED);
                        ((Marker) overlay).setCaptionText("");
                        //set the marker with no caption and set it red.

                        //set the sliding up panel below to initial settings
                        TextView storeName = findViewById(R.id.store_name);
                        storeName.setText("장소를 선택해주세요");
                        drag_info = new String[]{"      마커를 선택해주세요!"} ;
                        ArrayAdapter adapter_drag = new ArrayAdapter(this, android.R.layout.simple_list_item_1, drag_info){
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);
                                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/nanumsquare_aceb.ttf");
                                ((TextView) v).setTypeface(externalFont);
                                return v;
                            }
                        };
                        ListView listview_dragInfo = (ListView) findViewById(R.id.infoView);
                        listview_dragInfo.setAdapter(adapter_drag);

                        prev_storeName = "";
                        //to identify whether a current marker was clicked just before this click next time.
                        return false;
                    }
                    else{
                        ((Marker) overlay).setIcon(MarkerIcons.BLUE);
                        ((Marker) overlay).setCaptionText(storeSet.get(i).name);
                        ((Marker) overlay).setCaptionRequestedWidth(200);
                        ((Marker) overlay).setCaptionAligns(Align.Top);
                        ((Marker) overlay).setCaptionMinZoom(6);
                        ((Marker) overlay).setCaptionMaxZoom(19);

                        prev_storeName = storeSet.get(i).name;
                        //to identify whether a current marker was clicked just before this click  next time.
                    }

                    String name = storeSet.get(i).name;
                    String location = storeSet.get(i).add;
                    String recommended = storeSet.get(i).recommend;
                    String phNum = storeSet.get(i).num;
                    String cate = storeSet.get(i).cate;

                    storeInfo = storeSet.get(i);//to pass the selected store info to MenuDetail page.

                    TextView storeName = findViewById(R.id.store_name);
                    storeName.setText(name);
                    drag_info = new String[]{"위치: "+location, "전화번호: "+phNum, "["+ cate + "] " +recommended} ;
                    ArrayAdapter adapter_drag = new ArrayAdapter(this, android.R.layout.simple_list_item_1, drag_info){
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);
                            Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/nanumsquare_aceb.ttf");
                            ((TextView) v).setTypeface(externalFont);
                            return v;
                        }
                    };
                    ListView listview_dragInfo = (ListView) findViewById(R.id.infoView);
                    listview_dragInfo.setAdapter(adapter_drag);

                }
            }

        }



        return false;
    }
}