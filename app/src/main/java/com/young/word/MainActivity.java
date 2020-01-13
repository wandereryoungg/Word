package com.young.word;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.assetsbasedata.AssetsDatabaseManager;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.young.word.entity.CET4Entity;
import com.young.word.entity.CET4EntityDao;
import com.young.word.entity.DaoMaster;
import com.young.word.entity.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private TextView timeText, dateText, wordText, englishText;
    private ImageView playVoice;
    private String mMonth, mDay, mWay, mHour, mMinute;
    private RadioGroup radioGroup;
    private RadioButton radionOne, radioTwo, radioThree;
    private SpeechSynthesizer speechSynthesizer;
    private InitListener initListener = new InitListener() {
        @Override
        public void onInit(int i) {

        }
    };
    private SynthesizerListener synthesizerListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //用于记录答了几道题
    int j =0;
    List<Integer> list;
    List<CET4Entity> datas;
    int k;
    float x1 = 0;
    float y1 = 0;
    float x2 = 0;
    float y2 = 0;

    private Database db;
    private DaoMaster mDaoMaster, dbMaster;
    private DaoSession mDaoSession, dbSession;
    private CET4EntityDao questionDao, dbDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_main);
        init();
        setParam();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Calendar calendar = Calendar.getInstance();
        mMonth = String.valueOf(calendar.get(Calendar.MONTH)+1);
        mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if(calendar.get(Calendar.HOUR) <10){
            mHour = "0"+calendar.get(Calendar.HOUR);
        }else{
            mHour = String.valueOf(calendar.get(Calendar.HOUR));
        }
        if(calendar.get(Calendar.MINUTE) <10){
            mMinute = "0"+calendar.get(Calendar.MINUTE);
        }else{
            mMinute = String.valueOf(calendar.get(Calendar.MINUTE));
        }
        if("1".equals(mWay)){
            mWay = "天";
        }else if("2".equals(mWay)){
            mWay = "一";
        }else if("3".equals(mWay)){
            mWay = "二";
        }else if("4".equals(mWay)){
            mWay = "三";
        }else if("5".equals(mWay)){
            mWay = "四";
        }else if("6".equals(mWay)){
            mWay = "五";
        }else if("7".equals(mWay)){
            mWay = "六";
        }
        timeText.setText(mHour+":"+mMinute);
        dateText.setText(mMonth+"月"+mDay+"日     "+"星期"+mWay);
        getDBData();

        BaseApplication.addDestroyActivity(this,"mainActivity");
    }

    private void setParam() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID+"=5df0af51");
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(this, initListener);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
        speechSynthesizer.setParameter(SpeechConstant.SPEED,"50");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME,"50");
        speechSynthesizer.setParameter(SpeechConstant.PITCH,"50");
    }

    private void init() {
        sharedPreferences = getSharedPreferences("share",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        list = new ArrayList<>();
        Random random = new Random();
        int i;
        while(list.size() <10){
            i = random.nextInt(20);
            if(!list.contains(i)){
                list.add(i);
                Log.e("young","i="+i);
            }
        }
        km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");

        AssetsDatabaseManager.initManager(this);
        AssetsDatabaseManager manager = AssetsDatabaseManager.getManager();
        SQLiteDatabase db1 = manager.getDatabase("word.db");
        mDaoMaster = new DaoMaster(db1);
        mDaoSession = mDaoMaster.newSession();
        questionDao = mDaoSession.getCET4EntityDao();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"wrong.db",null);
        db = helper.getWritableDb();
        dbMaster = new DaoMaster(db);
        dbSession = dbMaster.newSession();
        dbDao = dbSession.getCET4EntityDao();

        timeText = findViewById(R.id.time_text);
        dateText = findViewById(R.id.date_text);
        wordText = findViewById(R.id.word_text);
        englishText = findViewById(R.id.english_text);
        playVoice = findViewById(R.id.play_voice);
        playVoice.setOnClickListener(this);
        radioGroup = findViewById(R.id.choose_group);
        radionOne = findViewById(R.id.choose_btn_one);
        radioTwo = findViewById(R.id.choose_btn_two);
        radioThree = findViewById(R.id.choose_btn_three);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_voice:
                String text = wordText.getText().toString();
                speechSynthesizer.startSpeaking(text, synthesizerListener);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.choose_btn_one:
                String msg1 = radionOne.getText().toString().substring(3);
                btnGetText(msg1,radionOne);
                break;
            case R.id.choose_btn_two:
                String msg2 = radioTwo.getText().toString().substring(3);
                btnGetText(msg2,radioTwo);
                break;
            case R.id.choose_btn_three:
                String msg3 = radioThree.getText().toString().substring(3);
                btnGetText(msg3,radioThree);
                break;
        }

    }

    private void saveWrongData(){
        String word = datas.get(k).getWord();
        String english = datas.get(k).getEnglish();
        String china = datas.get(k).getChina();
        String sign = datas.get(k).getSign();
        CET4Entity data = new CET4Entity(Long.valueOf(Long.valueOf(dbDao.count())),word,english,china,sign);
        dbDao.insertOrReplace(data);
    }

    private void btnGetText(String msg, RadioButton btn){
        if(msg.equals(datas.get(k).getChina())){
            wordText.setTextColor(Color.GREEN);
            englishText.setTextColor(Color.GREEN);
            btn.setTextColor(Color.GREEN);
        }else{
            wordText.setTextColor(Color.RED);
            englishText.setTextColor(Color.RED);
            btn.setTextColor(Color.RED);
            saveWrongData();
            int wrong = sharedPreferences.getInt("wrong",0);
            editor.putInt("wrong",wrong+1);
            editor.putString("wrongId",","+datas.get(j).getId());
            editor.commit();
        }
    }

    private void setTextColor(){
        radionOne.setChecked(false);
        radioTwo.setChecked(false);
        radioThree.setChecked(false);
        radionOne.setTextColor(Color.parseColor("#FFFFFF"));
        radioTwo.setTextColor(Color.parseColor("#FFFFFF"));
        radioThree.setTextColor(Color.parseColor("#FFFFFF"));
        wordText.setTextColor(Color.parseColor("#FFFFFF"));
        englishText.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void unlocked(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        kl.disableKeyguard();
        fileList();
    }

    private void getDBData(){
        datas = questionDao.queryBuilder().list();
        k = list.get(j);
        Log.e("young","j="+j);
        Log.e("young","k="+k);
        wordText.setText(datas.get(k).getWord());
        englishText.setText(datas.get(k).getEnglish());
        setChina(datas);
    }

    private void getNextData(){
        j++;
        int i = sharedPreferences.getInt("allNum",2);
        if(i>j){
            getDBData();
            setTextColor();
            int num  = sharedPreferences.getInt("alreadyStudy",0)+1;
            editor.putInt("alreadyStudy",num);
            editor.commit();
        }else{
            unlocked();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            x1 = event.getX();
            y1 = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP){
            x2 = event.getX();
            y2 = event.getY();
            if(y1-y2>200){
                int num = sharedPreferences.getInt("alreadyMastered",0)+1;
                editor.putInt("alreadyMastered",num);
                editor.commit();
                YoungToast.showTextToast("已掌握- -",MainActivity.this);
                getNextData();
            }else if(y2-y1 >200){
                YoungToast.showTextToast("待加入功能.....",MainActivity.this);
            }else if(x1-x2 >200){
                getNextData();
            }else if(x2-x1 >200){
                unlocked();
            }
        }
        return super.onTouchEvent(event);
    }

    private void setChina(List<CET4Entity> datas){
        Random random = new Random();
        List<Integer> listInt = new ArrayList<>();
        int i;
        while(listInt.size() <4){
            i = random.nextInt(20);
            if(!listInt.contains(i)){
                listInt.add(i);
            }
        }
        if(listInt.get(0)<7){
            radionOne.setText("A: "+datas.get(k).getChina());
            if(k-1>=0){
                radioTwo.setText("B: "+datas.get(k-1).getChina());
            }else{
                radioTwo.setText(datas.get(k+2).getChina());
            }
            if(k+1 <20){
                radioThree.setText("C: "+datas.get(k+1).getChina());
            }else{
                radioThree.setText("C: "+datas.get(k-1).getChina());
            }
        }else if(listInt.get(0)<14){
            radioTwo.setText("B: "+datas.get(k).getChina());
            if(k-1 >=0){
                radionOne.setText("A: "+datas.get(k-1).getChina());
            }else{
                radionOne.setText("A: "+datas.get(k+2).getChina());
            }
            if(k+1 <20){
                radioThree.setText("C: "+datas.get(k+1).getChina());
            }else{
                radioThree.setText("C: "+datas.get(k-1).getChina());
            }
        }else{
            radioThree.setText("C: "+datas.get(k).getChina());
            if(k-1>=0){
                radionOne.setText("B: "+datas.get(k-1).getChina());
            }else{
                radionOne.setText("B: "+datas.get(k+2).getChina());
            }
            if(k+1<20){
                radioThree.setText("A: "+datas.get(k+1).getChina());
            }else{
                radioThree.setText("A: "+datas.get(k-1));
            }
        }
    }


}
