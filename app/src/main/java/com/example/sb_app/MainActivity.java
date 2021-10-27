package com.example.sb_app;

import static com.example.sb_app.R.color.white;
import static java.lang.Math.max;
import static java.lang.Math.min;

import android.animation.FloatArrayEvaluator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MicrophoneInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ortiz.touchview.*;

import org.w3c.dom.Document;


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

        init();
    }

//-------------------------------------------------

    //역 클릭시 이벤트 발생시키는 함수
    private void Click_station() {

        //이미지뷰 찾기
        TouchImageView mImageView = (TouchImageView) findViewById(R.id.subway);

        //setOntouchCoordinatesListener - 확대,축소, 클릭 등 이벤트가 발생할 때마다 실행되는 함수
        mImageView.setOnTouchCoordinatesListener(new OnTouchCoordinatesListener() {

            //view  - 현재 이미지뷰, motionEvent - 현재 발생한 이벤트(bitmap 좌표값), pointF - 현재 발생한 이벤트의 좌표값(drawable 좌표값)
            //현재 이미지의 좌표를 drawable 좌표로 변경하는 방법
            //mImageView.getDrawable().getIntrinsicWidth();
            @Override
            public void onTouchCoordinate(View view, MotionEvent motionEvent, PointF pointF) {

                //측정한 drawalbe 좌표값들로 사각형을 만듦
                RectF rectf = new RectF(3700.000F, 4303.3945F, 4000.223F, 4500.071F);
                //그 사각형에 현재 좌표가 포함되는지 확인
                if (rectf.contains(pointF.x, pointF.y)) {
                    create_station("사당");

                }
                //아닐 경우 창 비워줌
                else {
                    clear();
                }


                Log.d("yesss", "\npoint X : " + pointF.x +
                        "\npoint Y : " + pointF.y);
            }
        });
    }

//-------------------------------------------------

    //station 객체 생성하여 화면에 표출하는 함수
    //parameter : 역이름
    private void create_station(String st_name) {



        station st;

        //표함된다면 역 생성하여 출력
        if (st_name != null) {
            st = new station(st_name);
        } else {
            st = new station();
        }


        //스레드 시작 - api로부터 정보를 읽어옴
        st.start();

        //스레드 종료될때까지 대기 - 스레드 안에 화면 표시 코드(이하 코드들)를 추가하니 작동이 되지 않음
        while (st.getState() != Thread.State.TERMINATED) {
        }

        //화면 정리
        clear();
        //station 정보 출력
        station_info_output(st);
    }


//-------------------------------------------------

    //객체를 받아서 출력해주는 함수
    //parameter : station 객체
    private void station_info_output(station st) {
        LinearLayout upline;
        LinearLayout dnline;
        TextView myView = (TextView) findViewById(R.id.info_context);
        TextView upline_text = (TextView) findViewById(R.id.upline_text);
        TextView dnline_text = (TextView) findViewById(R.id.dnline_text);

        upline = findViewById(R.id.upLine);
        dnline = findViewById(R.id.dnLine);



        try {
            //그 역 출력
            myView.setText(st.getStation_name());

            //도착 시간 배열의 갯수 만큼 화면에 출력해주기

            for (int i = 0; i < st.getArrive_time().length; i++) {
                //TextView를 만들어 LinearLayout의 LayoutParam 속성을 추가해주고 해당 레이아웃에 추가함
                TextView textview = new TextView(getApplicationContext());
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                //textview에 도착예정시간을 추가해줌
                String[] Nextpoint = st.getEndNextPoint().split("-");
                textview.setText(Nextpoint[0] + " : " + st.getArrive_time()[i]);

                //textview에 속성추가를 해줌
                textview.setLayoutParams(param);
                textview.setId(i);
                textview.setTextSize(12);
                textview.setTypeface(null, Typeface.BOLD);
                textview.setBackgroundColor(Color.rgb(100, 100, 100));

                //상행인지 하행인지 판단하여 출력해줌
                switch (st.getUpdnLine()[i]) {
                    case "상행":
                        //upline_text.setText(st.getEndPoint());
                        //upline.addView(upline_text);
                        upline.addView(textview);

                        break;
                    case "하행":
                        //dnline_text.setText(st.getEndPoint());
                        //dnline.addView(dnline_text);
                        dnline.addView(textview);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //st.run();
        }
    }

    //-------------------------------------------------
    //화면 청소해주는 함수
    private void clear() {
        LinearLayout upline;
        LinearLayout dnline;
        TextView myView = (TextView) findViewById(R.id.info_context);


        upline = findViewById(R.id.upLine);
        dnline = findViewById(R.id.dnLine);

        //정보를 보여주기 전에 화면 정리
        upline.removeAllViews();
        dnline.removeAllViews();
        myView.setText("");
    }

    //main 들어가기 전에 준비해야될 것 - 검색화면
    private void init() {
        ImageButton search_btn = (ImageButton) findViewById(R.id.searching_btn);
        TextView myView = (TextView) findViewById(R.id.info_context);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_searching.class);
                startActivity(intent);

            }
        });

        main();
    }


//-------------------------------------------------

    //main 함수 부분 시작!
    private void main() {

        TextView myView = (TextView) findViewById(R.id.info_context);
        Intent intent = getIntent();

        //클릭시 역 설정
        Click_station();

        //검색 기능으로 역 검색
        create_station(intent.getStringExtra("stationName"));
    }


//-------------------------------------------------
//-------------------------------------------------

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
        //종착역 - bstatnNm
        private String endPoint;
        //종착역+다음역 - trainLineNm
        private String EndNextPoint;


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


        /////////////////////////////

        //함수////////////////////////

        //----------------------------------------------------- 파싱할때 필요
        private String getTagValue(String tag, Element eElement) {
            NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
            Node nValue = (Node) nlList.item(0);
            if (nValue == null)
                return null;
            return nValue.getNodeValue();
        }
        //-----------------------------------------------------

        //쓰레드 시작 부분으로 parsing하는 역할

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
                        Log.d("yesss", "종착역 + 다음역 : " + getTagValue("trainLineNm", eElement));
                        Log.d("yesss", "종착역 : " + getTagValue("bstatnNm", eElement));




                        //변수 대입
                        setStation_name(getTagValue("statnNm", eElement));
                        setArrive_time(getTagValue("arvlMsg2", eElement), temp);
                        setStation_id(Integer.parseInt(getTagValue("statnId", eElement)));
                        setUpdnLine(getTagValue("updnLine", eElement), temp);
                        setEndPoint(getTagValue("bstatnNm", eElement));
                        setEndNextPoint(getTagValue("trainLineNm",eElement));


                    }    // for end

                }    // if end
            } catch (Exception e) {
                e.printStackTrace();
            }    // try~catch end
            //-------------------------------------------------

        }


        //변수들 getter,setter////////////////////////////

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

        public String getEndNextPoint() {
            return EndNextPoint;
        }

        public void setEndNextPoint(String endNextPoint) {
            EndNextPoint = endNextPoint;
        }
        //////////////////////////////////
    }
}