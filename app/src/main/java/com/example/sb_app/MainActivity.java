package com.example.sb_app;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;


import javax.xml.parsers.*;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mImageView;


    //전역 변수//////////////
    //역정보가 담겨있는 map<역이름, 역id>
    HashMap<String,Integer> stn_info = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //station_info();
        main();

        // xml에 정의한 이미지뷰 찾고
        mImageView=(ImageView)findViewById(R.id.subway);

        // 스케일제스쳐 디텍터 인스턴스
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //변수로 선언해 놓은 ScaleGestureDetector
       /* switch(event.getActionMasked()) {
            case MotionEvent.ACTION_BUTTON_PRESS:

        }*/
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            // ScaleGestureDetector에서 factor를 받아 변수로 선언한 factor에 넣고
            mScaleFactor *= scaleGestureDetector.getScaleFactor();

            // 최대 10배, 최소 10배 줌 한계 설정
            mScaleFactor = Math.max(1.0f,
                    Math.min(mScaleFactor, 10.0f));

            // 이미지뷰 스케일에 적용
            mImageView.setPivotX(scaleGestureDetector.getFocusX());
            mImageView.setPivotY(scaleGestureDetector.getFocusY() - 255);
            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);


            FrameLayout myButton_picture = (FrameLayout) findViewById(R.id.gildong_jpg);
            myButton_picture.setScaleX(mScaleFactor);
            myButton_picture.setScaleY(mScaleFactor);
            return true;
        }
    }

    //main 함수 부분 시작!
    private void main() {
        FrameLayout myButton_picture = (FrameLayout) findViewById(R.id.gildong_jpg);
        TextView myView = (TextView) findViewById(R.id.info_context);

        myButton_picture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                station st = new station("길동");
                myView.setText("길동역 station id : " + stn_info.get("길동"));
                st.start();
                TextView test = (TextView) findViewById(R.id.test);
                test.setText(st.getStation_name());
            }
        });



    }

    public class station extends Thread{
        //변수////////////////////////
        //url
        private String st_url = "http://swopenapi.seoul.go.kr/api/subway/746b524f59746c7337327742727956/xml/realtimeStationArrival/0/10/";
        //주석 - xml 변수명

        //역이름 - subwayId
        private String station_name;
        //도착 예정 시간 - recptnDt
        private String[] arrive_time;
        //역id - statnId
        private int station_id;
        //즐겨찾기
        private int favorite;
        //호선 정보 - subwayList
        private int[] line_id;

        ////////////////////////////


        //생성자/////////////////////
        //역 정보 가져옴
        public station() {
            //st_url = st_url + "/강동";
            this("강동");
        }

        public station(String name) {
            station_name = name;
            st_url = st_url + name;
        }

        public void run() {

            //DocumentBuilderFactory 생성
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder;
            Document doc = null;


            try {
                URL url = new URL(st_url);
                HttpURLConnection urlconnection = null;
                urlconnection = (HttpURLConnection) url.openConnection();

                urlconnection.setRequestMethod("GET");
                //Log.d("Test","12");
                BufferedReader br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(),"UTF-8"));
                // Log.d("Test","13");
                String result = "";
                String line;
                // Log.d("Test","14");
                while((line = br.readLine()) != null) {
                    result = result + line + "\n";
                }

                //Log.i("test","1");
                for(int i = 0; i < 10; i++) {
                    arrive_time[0] = (result.split("<recptnDt>")[1]);
                }


            } catch (Exception e) {
                e.printStackTrace();
                //Log.e("Test", e.toString());
            }
        }

        /////////////////////////////

        //함수////////////////////////


        public String getSt_url() {
            return st_url;
        }

        public void setSt_url(String st_url) {
            this.st_url = st_url;
        }

        public String getStation_name() {
            return station_name;
        }

        public void setStation_name(String station_name) {
            this.station_name = station_name;
        }

        public String[] getArrive_time() {
            return arrive_time;
        }

        public void setArrive_time(String[] arrive_time) {
            this.arrive_time = arrive_time;
        }

        public int getStation_id() {
            return station_id;
        }

        public void setStation_id(int station_id) {
            this.station_id = station_id;
        }

        public int getFavorite() {
            return favorite;
        }

        public void setFavorite(int favorite) {
            this.favorite = favorite;
        }

        public int[] getLine_id() {
            return line_id;
        }

        public void setLine_id(int[] line_id) {
            this.line_id = line_id;
        }
        //////////////////////////////////
    }


    public void station_info()  {
        //fileinputstream으로 txt 정보 읽어옴
        FileInputStream file = null;
        byte[] data = null;
        String raw_info = null;
        try {
            //파일 경로
            file = new FileInputStream("C:\\Users\\rlagy\\Documents\\GitHub\\sb_app\\app\\src\\main\\java\\com\\example\\sb_app\\subway_station.txt");

            data = new byte[file.available()];
            file.read(data);

            int rowindex = 0;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        if(data != null) {
            try {
                raw_info = new String (data, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

       // Log.i("hibugs", raw_info);
        //역정보 split해서 HashMap에 추가
        String[] stn = raw_info.split("\n");
        for(int i = 0; i < stn.length-1; i++) {
            String[] tmp = stn[i].split("\t");
            stn_info.put(tmp[2], Integer.parseInt(tmp[1]));
        }


    }
}