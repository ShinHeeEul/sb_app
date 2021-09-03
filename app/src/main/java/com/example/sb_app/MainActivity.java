package com.example.sb_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.util.HashMap;
public class MainActivity extends AppCompatActivity {

    //전역 변수//////////////
    //역정보가 담겨있는 map<역이름, 역id>
    HashMap<String,Integer> stn_info = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //station_info();
        main();
    }
    //main 함수 부분 시작!
    private void main() {
        FrameLayout myButton_picture = (FrameLayout) findViewById(R.id.gildong_jpg);
        TextView myView = (TextView) findViewById(R.id.info_context);

        myButton_picture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                station st = new station("길동");
                myView.setText("길동역 station id : " + stn_info.get("길동"));
                TextView test = (TextView) findViewById(R.id.test);
                test.setText(st.getStation_name());
            }
        });



    }

    public class station {
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
            st_url = st_url + name;
            try {
                URL url = new URL(st_url);
                HttpURLConnection urlconnection = null;
                urlconnection = (HttpURLConnection) url.openConnection();
                Log.d("Test","station0");

                urlconnection.setRequestMethod("GET");
                Log.d("Test","station1");
                BufferedReader br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(),"utf-8"));
                Log.d("Test","station2");
                String result = "hi";
                String line;
                Log.d("Test","station3");
                while((line = br.readLine()) != null) {
                    result = result + line + "\n";
                }

                Log.d("Test",result);
                Log.d("Test","1");
                Log.e("Test","23");
                station_name = result;

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Test", e.toString() );
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

        Log.i("hibugs", raw_info);
        //역정보 split해서 HashMap에 추가
        String[] stn = raw_info.split("\n");
        for(int i = 0; i < stn.length-1; i++) {
            String[] tmp = stn[i].split("\t");
            stn_info.put(tmp[2], Integer.parseInt(tmp[1]));
        }


    }
}