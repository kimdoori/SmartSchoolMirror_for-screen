package kr.hs.emirim.ssm.smartschoolmirror;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import kr.hs.emirim.ssm.smartschoolmirror.date.GetDate;
import kr.hs.emirim.ssm.smartschoolmirror.schoolInfo.School;
import kr.hs.emirim.ssm.smartschoolmirror.schoolInfo.SchoolException;
import kr.hs.emirim.ssm.smartschoolmirror.weather.GetWeather;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends Activity{



    private TimerTask time_update_second;
    private TimerTask weather_update_second;

    private TextView date_text;
    private TextView time_text;
    private TextView weather_temp_text;
    private TextView school_food_text;
    private TextView school_schedule_text;
    private TextView school_food_title;
    private TextView school_schedule_title;
    private ImageView weather_image;

    private final Handler timer_handler = new Handler();
    private final Handler weather_handler = new Handler();

    //음성
    private enum State {
        INITALIZING,
        LISTENING_TO_KEYPHRASE,
        CONFIRMING_KEYPHRASE,
        LISTENING_TO_ACTION,
        CONFIRMING_ACTION,
        TIMEOUT
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView  giphy = (ImageView)findViewById(R.id.gif_image);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(giphy);
        Glide.with(this).load(R.drawable.giphy).into(gifImage);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("NotoSansCJKkr-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        date_text = (TextView) findViewById(R.id.time_text);
        time_text = (TextView) findViewById(R.id.date_text);
        weather_temp_text=(TextView) findViewById(R.id.weather_text);
        school_food_text=(TextView) findViewById(R.id.school_food_text);
        school_schedule_text=(TextView) findViewById(R.id.school_schedule_text);
        school_food_title=(TextView) findViewById(R.id.school_food_title);
        school_schedule_title=(TextView) findViewById(R.id.school_schedule_title);
        weather_image=(ImageView) findViewById(R.id.weather_image);


        ButterKnife.bind(this);

        time_updater_Start();
        weather_updater_Start();


        getSchoolInfo();


    }


    //급식과 일정 업데이트
    public void getSchoolInfo(){
        School api_food = new School(School.Type.HIGH, School.Region.SEOUL, "B100000439");
        School api_schedule = new School(School.Type.HIGH, School.Region.SEOUL, "B100000639");

        try {
            api_food.getMonthlyMenu(school_food_title,school_food_text,GetDate.getYear(), GetDate.getMonth());
            api_schedule.getMonthlySchedule(school_schedule_title,school_schedule_text,GetDate.getYear(), GetDate.getMonth());

        } catch (SchoolException e) {
            Log.e("에러",e.toString());
        }
    }




    //시간업데이트
    public void time_updater_Start() {

        time_update_second = new TimerTask() {
            @Override
            public void run() {
                time_Update();
            }
        };
        Timer timer = new Timer();
        timer.schedule(time_update_second, 0, 1000);
    }

    protected void time_Update() {
        Runnable updater = new Runnable() {
            public void run() {
                GetDate.updateDate(date_text,time_text);
            }
        };
        timer_handler.post(updater);
    }


    //날씨 업데이트
    public void weather_updater_Start() {

        weather_update_second = new TimerTask() {
            @Override
            public void run() {
                weather_Update();
            }
        };
        Timer timer = new Timer();
        timer.schedule(weather_update_second, 0, 1800000);//1800000 //30분마다 갱신
    }



    protected void weather_Update() {
        Runnable updater = new Runnable() {
            public void run() {
                GetWeather.getWeather(getCurrentFocus(),weather_temp_text);
                GetWeather.getWeatherIcon(getCurrentFocus(),weather_image);

            }
        };
        weather_handler.post(updater);
    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


//지원

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
