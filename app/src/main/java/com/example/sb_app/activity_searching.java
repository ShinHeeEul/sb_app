package com.example.sb_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class activity_searching extends AppCompatActivity {

    private ArrayList<String> list;          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private SearchAdapter adapter;      // 리스트뷰에 연결할 아답터
    private ArrayList<String> arraylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        editSearch = (EditText) findViewById(R.id.editSearch);
        listView = (ListView) findViewById(R.id.listView);

        // 리스트를 생성한다.
        list = new ArrayList<String>();

        station_info();

        // 검색에 사용할 데이터을 미리 저장한다.
        //ettingList();

        // 리스트의 모든 데이터를 arraylist에 복사한다.// list 복사본을 만든다.
        arraylist = new ArrayList<String>();
        arraylist.addAll(list);

        // 리스트에 연동될 아답터를 생성한다.
        adapter = new SearchAdapter(list, this);

        // 리스트뷰에 아답터를 연결한다.
        listView.setAdapter(adapter);

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("clicked", "onItemClick: " + list.get(i)
                                            + " int i = " + i
                                            + "long l = " + l);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("stationName", list.get(i));

                startActivity(intent);

            }
        });


    }



    // 검색을 수행하는 메소드
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            list.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < arraylist.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arraylist.get(i).toLowerCase().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    list.add(arraylist.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }

    /*// 검색에 사용될 데이터를 리스트에 추가한다.
    private void settingList(){
        list.add("채수빈");
        list.add("박지현");
        //map에 제대로 역정보가 담겼는지 확인 - map<역이름, 역id>
    }*/

    //역정보 가져오기 - 파일 위치는 res/raw
    public void station_info() {
        //fileinputstream으로 txt 정보 읽어옴
        InputStream file = getResources().openRawResource(R.raw.subway_station);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String data = null;
        try {
            //파일 읽기
            int i = file.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = file.read();
            }
            //읽어온 파일 String으로 바꾸기
            data = new String(byteArrayOutputStream.toByteArray(), "utf-8");
            int rowindex = 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //역정보 split해서 list에 추가
        String[] stn = data.split("\n");
        for (int i = 0; i < stn.length - 1; i++) {
            String[] tmp = stn[i].split("\t");

            list.add(tmp[2]);
        }

        //중복 제거를 위해 HashSet에 넣었다빼기
        HashSet<String> tmp = new HashSet<String>(list);
        list = new ArrayList<String>(tmp);

    }
}