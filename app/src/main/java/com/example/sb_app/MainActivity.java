package com.example.sb_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ortiz.touchview.TouchImageView;

import org.w3c.dom.Document;


import javax.xml.parsers.*;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
//-------------------------------------------------
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//-------------------------------------------------

public class MainActivity extends AppCompatActivity {
    //전역 변수//////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton search_btn = (ImageButton)findViewById(R.id.searching_btn);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_searching.class);
                startActivity(intent);
            }
        });

        main();

        // xml에 정의한 이미지뷰 찾고
        TouchImageView mImageView = (TouchImageView) findViewById(R.id.subway);



    }

    //----------------------------------------------------- 파싱할때 필요
    private static String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if (nValue == null)
            return null;
        return nValue.getNodeValue();
    }
    //-----------------------------------------------------

    private void station_info_output(station st) {
        LinearLayout upline;
        LinearLayout dnline;


        upline = findViewById(R.id.upLine);
        dnline = findViewById(R.id.dnLine);

        //정보를 보여주기 전에 화면 정리
        upline.removeAllViews();
        dnline.removeAllViews();

        try {
            //도착 시간 배열의 갯수 만큼 화면에 출력해주기
            for (int i = 0; i < st.getArrive_time().length; i++) {
                //TextView를 만들어 LinearLayout의 LayoutParam 속성을 추가해주고 해당 레이아웃에 추가함
                TextView textview = new TextView(getApplicationContext());
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                //textview에 도착예정시간을 추가해줌
                textview.setText(st.getArrive_time()[i]);

                //textview에 속성추가를 해줌
                textview.setLayoutParams(param);
                textview.setId(i);
                textview.setTextSize(12);
                textview.setTypeface(null, Typeface.BOLD);
                textview.setBackgroundColor(Color.rgb(100, 100, 100));

                //상행인지 하행인지 판단하여 출력해줌
                switch (st.getUpdnLine()[i]) {
                    case "상행":
                        upline.addView(textview);
                        break;
                    case "하행":
                        dnline.addView(textview);
                        break;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            //st.run();
        }
    }

    //main 함수 부분 시작!
    private void main() {
        FrameLayout myButton_picture = (FrameLayout) findViewById(R.id.gildong_jpg);
        TextView myView = (TextView) findViewById(R.id.info_context);
        Intent intent = getIntent();

        //클릭시 역 설정
        //station st = new station("길동");
        station st = new station(intent.getStringExtra("stationName"));
        //그 역 출력
        myView.setText(st.getStation_name());

        //스레드 시작 - api로부터 정보를 읽어옴
        st.start();

        //스레드 종료될때까지 대기 - 스레드 안에 화면 표시 코드(이하 코드들)를 추가하니 작동이 되지 않음
        while (st.getState() != Thread.State.TERMINATED){} ;

        //station 정보 출력
        station_info_output(st);

        myButton_picture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //클릭시 역 설정
                //station st = new station("길동");
                station st = new station(intent.getStringExtra("stationName"));
                //그 역 출력
                myView.setText(st.getStation_name());

                //스레드 시작 - api로부터 정보를 읽어옴
                st.start();

                //스레드 종료될때까지 대기 - 스레드 안에 화면 표시 코드(이하 코드들)를 추가하니 작동이 되지 않음
                while (st.getState() != Thread.State.TERMINATED){} ;

                //station 정보 출력
                station_info_output(st);

    }
        });



    }


    private class station extends Thread {
        //변수////////////////////////
        //url
        private String st_url = "http://swopenapi.seoul.go.kr/api/subway/746b524f59746c7337327742727956/xml/realtimeStationArrival/0/10/";
        //주석 - xml 변수명

        //역이름 - subwayId
        private String station_name;
        //도착 예정 시간 - arvMsg2
        private String[] arrive_time = null;
        //역id - statnId
        private int station_id;
        //즐겨찾기
        private int favorite;
        //호선 정보 - subwayList
        private int[] line_id;
        //상행하행 - updnLine
        private String[] updnLine = null;
        //종착역 - trainLineNm
        private String endPoint;


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
            //DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //factory.setNamespaceAware(true);
            //----------------------------------------------------
            //https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=nonamed0000&logNo=220988048654 여기 참고함
            // Log.d("doc", "run: ");


            try {
                // parsing할 url 지정(API 키 포함해서)
                DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
                Log.d("doc", "run:0 ");
                Document doc = dBuilder.parse(st_url);
                Log.d("doc", "run:1 ");
                // root tag
                doc.getDocumentElement().normalize();
                Log.d("root_element", "Root element :" + doc.getDocumentElement().getNodeName());

                // 파싱할 tag
                NodeList nList = doc.getElementsByTagName("row");

                //배열에 동적할당
                arrive_time = new String[nList.getLength()];
                updnLine = new String[nList.getLength()];

                //파싱한 데이터를 읽어옴
                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        Log.d("yesss", "######################");
                        Log.d("yesss", "역이름: " + getTagValue("statnNm", eElement));
                        Log.d("yesss", "도착 예정 시간 : " + getTagValue("arvlMsg2", eElement));
                        Log.d("yesss", "역 id : " + getTagValue("statnId", eElement));
                        Log.d("yesss", "호선 정보 : " + getTagValue("subwayList", eElement));
                        Log.d("yesss", "updnLine : " + getTagValue("updnLine", eElement));
                        Log.d("yesss", "종착역 : " + getTagValue("trainLineNm", eElement));


                        //변수 대입
                        setStation_name(getTagValue("statnNm", eElement));
                        setArrive_time(getTagValue("arvlMsg2", eElement), temp);
                        setStation_id(Integer.parseInt(getTagValue("statnId", eElement)));
                        setUpdnLine(getTagValue("updnLine", eElement), temp);
                        setEndPoint(getTagValue("trainLineNm", eElement));


                    }    // for end

                }    // if end
            } catch (Exception e) {
                e.printStackTrace();
            }    // try~catch end
            //-------------------------------------------------

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

        public void setArrive_time(String arrive_time, int i) {
            this.arrive_time[i] = arrive_time;
            Log.d("hello", this.arrive_time[i]);
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

        public String[] getUpdnLine() {
            return updnLine;
        }

        public void setUpdnLine(String updnLine, int i) {
            this.updnLine[i] = updnLine;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }
        //////////////////////////////////
    }
}