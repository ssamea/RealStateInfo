package com.example.realstateinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    EditText edit;
    TextView text;
    TextView aptname;
    XmlPullParser xpp;

    String data;
    private Spinner spinnerCity, spinnerSigungu, spinnerDong;
    private ArrayAdapter<String> arrayAdapter;
    public static final String EXTRA_ADDRESS = "address";
    String local_Code;
    String api_apart_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edit= (EditText)findViewById(R.id.edit);
        text= (TextView)findViewById(R.id.result);
        aptname=(TextView)findViewById(R.id.apt_name);

        spinnerCity = (Spinner)findViewById(R.id.spin_city);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String[])getResources().getStringArray(R.array.spinner_region));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(arrayAdapter);

        spinnerSigungu = (Spinner)findViewById(R.id.spin_sigungu);
        spinnerDong = (Spinner)findViewById(R.id.spin_dong);

        initAddressSpinner();

    }

    public void mOnClick(View view){
        switch (view.getId()){
            case R.id.btn_ok:
                if (spinnerCity.getSelectedItemPosition() == 0) {
                    Toast.makeText(view.getContext(),"???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                }

                else {
                    Intent localdata = new Intent();

                    if(spinnerDong.getSelectedItem() != null) {
                        String address = spinnerCity.getSelectedItem().toString() + " " + spinnerSigungu.getSelectedItem().toString() + " " + spinnerDong.getSelectedItem().toString();
                        localdata.putExtra(EXTRA_ADDRESS, address);
                    } else {
                        String address = spinnerCity.getSelectedItem().toString() + " " + spinnerSigungu.getSelectedItem().toString();
                        localdata.putExtra(EXTRA_ADDRESS, address);
                    }

                    setResult(RESULT_OK, localdata);

                    String text1 = spinnerCity.getSelectedItem().toString();
                    String text2 = spinnerSigungu.getSelectedItem().toString();
                    String text3 = spinnerDong.getSelectedItem().toString();


                    local_Code=getLocal_Code(text2);
                    //finish(); // ?????????

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            data=getXmlData();
                            api_apart_name=getXmlName();
                           // String apartName= edit.getText().toString();//EditText??? ????????? Text????????????


                            StringBuilder sb = new StringBuilder();
                            sb.append(api_apart_name);


                            //System.out.println(sb);
                            /*
                            String apartName= edit.getText().toString();//EditText??? ????????? Text????????????
                            if (apartName.contains(data)){
                                System.out.println("???????????????"+apartName);
                            }
                            else{
                                System.out.println("??????");
                            }

                             */

                            //Log.i("data ????????? ??????:",api_apart_name);
                            //Log.i("data ",data);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    text.setText(data);

                                }
                            });
                        }
                    }).start();
                }

                break;
        }
    }

    String getXmlName(){
        StringBuffer buffer=new StringBuffer();
       // String apartName= edit.getText().toString();//EditText??? ????????? Text????????????
        // String location = URLEncoder.encode(LAWD_CD);

        String queryUrl="http://openapi.molit.go.kr/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTradeDev?LAWD_CD="+local_Code+
                "&DEAL_YMD=202107&serviceKey=aOKXvfshZJPd1moqnd%2FPfzkE5I5SGKANDf26uGzQZFOje1JSgylw99l8Hc5L3rib%2FR3PE0PFUFpUZLAPvsg24A%3D%3D\n";


        try{
            URL url= new URL(queryUrl);//???????????? ??? ?????? url??? URL ????????? ??????.
            InputStream is= url.openStream(); //url????????? ??????????????? ??????

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();//xml????????? ??????  SaxParser ??? Wrapping ?????? ??????
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream ???????????? xml ????????????

            String tag;

            xpp.next();

            int eventType= xpp.getEventType();
            //Log.d("eventType ???:", String.valueOf(eventType));
            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("?????? ??????...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//?????? ?????? ????????????
                        //Log.d("tag ???:", String.valueOf(tag));

                        if(tag.equals("item")) ;// ????????? ????????????

                    else if(tag.equals("?????????")){
                        //buffer.append("????????? :");
                        xpp.next();
                        buffer.append(xpp.getText());//description ????????? TEXT ???????????? ?????????????????? ??????
                        buffer.append("\n");//????????? ?????? ??????
                    }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName(); //?????? ?????? ????????????

                        if(tag.equals("item")) buffer.append("\n");// ????????? ??????????????????..?????????
                        break;
                }

                eventType= xpp.next();
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        buffer.append("?????? ???\n");
        return buffer.toString();//StringBuffer ????????? ?????? ??????
    }

    String getXmlData(){
        StringBuffer buffer=new StringBuffer();
        String apartName= edit.getText().toString();//EditText??? ????????? Text????????????

        SimpleDateFormat real_time = new SimpleDateFormat("yyyyMM"); // ??????
        Date time = new Date();
        String DEAL_YMD = real_time.format(time); // ?????? ??????
       // Log.i("base_date: ",DEAL_YMD);

        String queryUrl="http://openapi.molit.go.kr/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTradeDev?LAWD_CD="+local_Code+
                "&DEAL_YMD="+DEAL_YMD+"&serviceKey=aOKXvfshZJPd1moqnd%2FPfzkE5I5SGKANDf26uGzQZFOje1JSgylw99l8Hc5L3rib%2FR3PE0PFUFpUZLAPvsg24A%3D%3D\n";


        try{
            URL url= new URL(queryUrl);//???????????? ??? ?????? url??? URL ????????? ??????.
            InputStream is= url.openStream(); //url????????? ??????????????? ??????

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();//xml????????? ??????  SaxParser ??? Wrapping ?????? ??????
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream ???????????? xml ????????????

            String tag;

            xpp.next();

            int eventType= xpp.getEventType();
           // Log.d("eventType ???:", String.valueOf(eventType));
            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("?????? ??????...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//?????? ?????? ????????????
                        //Log.d("tag ???:", String.valueOf(tag));

                        if(tag.equals("item")) ;// ????????? ????????????

                        else if(tag.equals("????????????")){
                            buffer.append("???????????? : ");
                            xpp.next();
                            buffer.append(xpp.getText());//title ????????? TEXT ???????????? ?????????????????? ??????
                            buffer.append("\n"); //????????? ?????? ??????
                        }
                        else if(tag.equals("?????????")){
                            buffer.append("????????? : ");
                            xpp.next();
                            buffer.append(xpp.getText());//category ????????? TEXT ???????????? ?????????????????? ??????
                            buffer.append("\n");//????????? ?????? ??????
                        }
                        else if(tag.equals("?????????")){
                            buffer.append("????????? :");
                            xpp.next();
                            buffer.append(xpp.getText());//description ????????? TEXT ???????????? ?????????????????? ??????
                            buffer.append("\n");//????????? ?????? ??????
                            //aptname.setText(xpp.getText());

                        }
                        else if(tag.equals("????????????")){
                            buffer.append("?????? ?????? :");
                            xpp.next();
                            buffer.append(xpp.getText());//telephone ????????? TEXT ???????????? ?????????????????? ??????
                            buffer.append("\n");//????????? ?????? ??????
                        }
                        else if(tag.equals("???")){
                            buffer.append("??? :");
                            xpp.next();
                            buffer.append(xpp.getText());//address ????????? TEXT ???????????? ?????????????????? ??????
                            buffer.append("\n");//????????? ?????? ??????
                        }
                        else if(tag.equals("????????????")){
                            buffer.append("?????? ?????? :");
                            xpp.next();
                            buffer.append(xpp.getText());//mapx ????????? TEXT ???????????? ?????????????????? ??????
                            buffer.append("\n"); //????????? ?????? ??????
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName(); //?????? ?????? ????????????

                        if(tag.equals("item")) buffer.append("\n");// ????????? ??????????????????..?????????
                        break;
                }

                eventType= xpp.next();
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        buffer.append("?????? ???\n");
        return buffer.toString();//StringBuffer ????????? ?????? ??????

    }//getXmlData method....

    private void initAddressSpinner() {
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ?????????, ?????? ???????????? ???????????????.
                switch (position) {
                    case 0:
                        spinnerSigungu.setAdapter(null);
                        break;
                    case 1:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_seoul);
                        break;
                    case 2:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_busan);
                        break;
                    case 3:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_daegu);
                        break;
                    case 4:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_incheon);
                        break;
                    case 5:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_gwangju);
                        break;
                    case 6:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_daejeon);
                        break;
                    case 7:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_ulsan);
                        break;
                    case 8:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_sejong);
                        break;
                    case 9:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_gyeonggi);
                        break;
                    case 10:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_gangwon);
                        break;
                    case 11:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_chung_buk);
                        break;
                    case 12:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_chung_nam);

                        break;
                    case 13:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_jeon_buk);
                        break;
                    case 14:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_jeon_nam);
                        break;
                    case 15:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_gyeong_buk);
                        break;
                    case 16:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_gyeong_nam);
                        break;
                    case 17:
                        setSigunguSpinnerAdapterItem(R.array.spinner_region_jeju);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ??????????????? ?????????
                if(spinnerCity.getSelectedItemPosition() == 1 && spinnerSigungu.getSelectedItemPosition() > -1) {
                    switch(position) {
                        //25
                        case 0:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gangnam);
                            break;
                        case 1:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gangdong);
                            break;
                        case 2:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gangbuk);
                            break;
                        case 3:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gangseo);
                            break;
                        case 4:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gwanak);
                            break;
                        case 5:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_gwangjin);
                            break;
                        case 6:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_guro);
                            break;
                        case 7:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_geumcheon);
                            break;
                        case 8:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_nowon);
                            break;
                        case 9:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_dobong);
                            break;
                        case 10:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_dongdaemun);
                            break;
                        case 11:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_dongjag);
                            break;
                        case 12:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_mapo);
                            break;
                        case 13:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_seodaemun);
                            break;
                        case 14:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_seocho);
                            break;
                        case 15:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_seongdong);
                            break;
                        case 16:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_seongbuk);
                            break;
                        case 17:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_songpa);
                            break;
                        case 18:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_yangcheon);
                            break;
                        case 19:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_yeongdeungpo);
                            break;
                        case 20:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_yongsan);
                            break;
                        case 21:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_eunpyeong);
                            break;
                        case 22:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_jongno);
                            break;
                        case 23:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_jung);
                            break;
                        case 24:
                            setDongSpinnerAdapterItem(R.array.spinner_region_seoul_jungnanggu);
                            break;
                    }
                } else {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setSigunguSpinnerAdapterItem(int array_resource) {
        if (arrayAdapter != null) {
            spinnerSigungu.setAdapter(null);
            arrayAdapter = null;
        }

        if (spinnerCity.getSelectedItemPosition() > 1) {
            spinnerDong.setAdapter(null);
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String[])getResources().getStringArray(array_resource));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSigungu.setAdapter(arrayAdapter);
    }

    private void setDongSpinnerAdapterItem(int array_resource) {
        if (arrayAdapter != null) {
            spinnerDong.setAdapter(null);
            arrayAdapter = null;
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String[])getResources().getStringArray(array_resource));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDong.setAdapter(arrayAdapter);
    }

    protected String getLocal_Code(String sigungu){
        // ?????? ??????
        if (sigungu.equals("?????????")) {
            local_Code = "1111"; // ????????? ?????????????????????
        }

        else if (sigungu.equals("?????????")) {
            local_Code = "11680"; // ????????? ?????????????????????
        }

        else if (sigungu.equals("?????????")) {
            local_Code = "11740"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11305"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11500"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11620"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11215"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11530"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11545"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11350"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11320"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("????????????")) {
            local_Code = "11230"; // ????????? ?????????????????????
        }


        else if (sigungu.equals("?????????")) {
            local_Code = "11590"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11440"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("????????????")) {
            local_Code = "11410"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11650"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11200"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11290"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11710"; // ????????? ?????????????????????
        }

        else if (sigungu.equals("?????????")) {
            local_Code = "11470"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("????????????")) {
            local_Code = "11560"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11170"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11380"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("??????")) {
            local_Code = "11140"; // ????????? ?????????????????????
        }
        else if (sigungu.equals("?????????")) {
            local_Code = "11260"; // ????????? ?????????????????????
        }

        return local_Code;
    }
}