package codingtribe.com;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static codingtribe.com.item_home.CatDbHelper;

public class item_predict extends AppCompatActivity {

    int year;
    int month;
    int date;

    ListView lv;
    Button btn_listChanDate;
    TextView text_listChanDate;
    DatePickerDialog dpd;
    int[] dateNowArr;

    ArrayList<PredictVO> arrayList;
    String result1;
    String array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_predict);

//predict 작성 시작
//서버에서 String 데이터를 전송 받을 때
//    private void receiveObject(JSONObject data) {
//
//        try {
//            mReceiveTv.setText(data.toString());
//            mReceiveNationTv.setText("nation : " + data.getString("nation"));
//            mReceiveNameTv.setText("name : " + data.getString("nation"));
//            mReceiveAddressTv.setText("address : " + data.getString("address"));
//            mReceiveAgeTv.setText("age : " + data.getString("age"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//
//        // 서버에서 배열 데이터를 전송 받을 때
//
//    private void receiveArray(String dataObject) {
//        recyclerView.setVisibility(View.VISIBLE);
//        objectResultLo.setVisibility(View.GONE);
//        mItems.clear();
//        try {
//// String 으로 들어온 값 JSONObject 로 1차 파싱
//            JSONObject wrapObject = new JSONObject(dataObject);
//
//// JSONObject 의 키 "list" 의 값들을 JSONArray 형태로 변환
//            JSONArray jsonArray = new JSONArray(wrapObject.getString("list"));
//            for (int i = 0; i < jsonArray.length(); i++) {
//// Array 에서 하나의 JSONObject 를 추출
//                JSONObject dataJsonObject = jsonArray.getJSONObject(i);
//// 추출한 Object 에서 필요한 데이터를 표시할 방법을 정해서 화면에 표시
//// 필자는 RecyclerView 로 데이터를 표시 함
//                mItems.add(new Item(dataJsonObject.getString("nation") + i, dataJsonObject.getString("name") + i,
//                        dataJsonObject.getString("address") + i, dataJsonObject.getString("age")));
//            }
//// Recycler Adapter 에서 데이터 변경 사항을 체크하라는 함수 호출
//            adapter.notifyDataSetChanged();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        if(getIntent().getStringExtra("resultString") == null){
            array = result1;
        }else {
            array = getIntent().getStringExtra("resultString");
        };


        Log.v("제발",array);
        lv = (ListView)findViewById(R.id.listView);
        btn_listChanDate = (Button)findViewById(R.id.listChanDate);
        text_listChanDate = (TextView)findViewById(R.id.listDate);

        Gson gson = new Gson();
        ArrayList<jsonVO> jsonArray = gson.fromJson(array, new TypeToken<ArrayList<jsonVO>>(){}.getType());

        for(jsonVO vo : jsonArray){
            Log.v("허허",vo.getCatID()+"");
        }


        arrayList = new ArrayList<PredictVO>();

        int id;
        String name;
        float prob;
        for(int i = 0; i<24;i++){
            id = jsonArray.get(i).getCatID();
            name = CatDbHelper.getCatName(id);
            prob = jsonArray.get(i).getCarProb()/0.01f;
                arrayList.add(new PredictVO(i+":00~",i+1+":00", name,prob));
        }

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        date = Calendar.getInstance().get(Calendar.DATE);

        Intent intent = getIntent();

        int check = intent.getIntExtra("year", 0);

        if(check != 0){
            year = intent.getIntExtra("year",2018);
            month= intent.getIntExtra("month",1);
            date = intent.getIntExtra("date",1);
        }
        dateNowArr = new int[]{year,month,date};
        text_listChanDate.setText(year + "년 " + (month + 1) + "월 " + date + "일");

        Calendar choiceDate = Calendar.getInstance();

        choiceDate.set(Calendar.YEAR,year);
        choiceDate.set(Calendar.MONTH,month);
        choiceDate.set(Calendar.DATE,date);

        btn_listChanDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd = new DatePickerDialog
                        (item_predict.this, // 현재화면의 제어권자
                                new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker view,
                                                          int year, int monthOfYear,int dayOfMonth) {
                                        Toast.makeText(getApplicationContext(),
                                                year+"년 "+(monthOfYear+1)+"월 "+dayOfMonth+"일 을 선택했습니다",
                                                Toast.LENGTH_SHORT).show();
                                        text_listChanDate.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                                        dateNowArr = new int[]{year,monthOfYear,dayOfMonth};
                                        Intent intent = new Intent(item_predict.this, item_predict.class);
                                        intent.putExtra("year",year);
                                        intent.putExtra("month",monthOfYear);
                                        intent.putExtra("date",dayOfMonth);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                        ActionDB actionDB = new ActionDB(getApplicationContext());
                                        ArrayList<ActionVO> actionArrayList = actionDB.getAllAction(getParent());
                                        ActPreProcessor act = new ActPreProcessor();

                                        String result;
                                        Calendar cal = Calendar.getInstance();

                                        String selectedDay = year+"-"+monthOfYear+1+"-"+dayOfMonth;
                                        Log.v("날짜라도",selectedDay);
                                        try {
                                            result = act.makeStatTable(actionArrayList);
                                            JsonSend jsonSend = new JsonSend("vision_write");
                                            result1 = jsonSend.execute(result+"`"+selectedDay, "vision_write").get();
                                            Log.v("허허",result1);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        intent.putExtra("resultString",result1);

                                        startActivity(intent);
                                        finish();

                                    }
                                }
                                , // 사용자가 날짜설정 후 다이얼로그 빠져나올때
                                //    호출할 리스너 등록
                                dateNowArr[0], dateNowArr[1], dateNowArr[2]); // 기본값 연월일
                dpd.show();
            }
        });

        final PredictAdapter adapter = new PredictAdapter(getApplicationContext(),R.layout.listpredict,arrayList);
        lv.setAdapter(adapter);

    }

    public class jsonVO{
        int catID;
        float carProb;

        public jsonVO(int catID, float carProb) {
            this.catID = catID;
            this.carProb = carProb;
        }

        public int getCatID() {
            return catID;
        }

        public void setCatID(int catID) {
            this.catID = catID;
        }

        public float getCarProb() {
            return carProb;
        }

        public void setCarProb(float carProb) {
            this.carProb = carProb;
        }
    }
}








