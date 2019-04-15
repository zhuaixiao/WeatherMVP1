package com.example.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.adapter.WeatherRVAdapter;
import com.example.test.adapter.WeatherforecastItem;
import com.example.test.contract.MainContract;
import com.example.test.json.Weather;
import com.example.test.presenter.MainPresenterImpl;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MainContract.View {


    private Button locality_backup;
    private TextView locality_text;
    private TextView now_cond;
    private TextView now_tmp;
    private TextView now_date;
    private CardView bannerview;
    private RecyclerView forecast_recycler;
    private TextView aqi_text;
    private TextView pm25_text;
    private TextView suggestion_comf;
    private TextView suggestion_sport;
    private TextView suggestion_cw;
    private SmartRefreshLayout smartrefreshlayout;
    private LinearLayout locality_linear;
    private ListView locality_listview;
    private List<String> imagesLiet;
    private List<String> localityInfo;
    private List<WeatherforecastItem> forecastInfo;
    private WeatherRVAdapter weatherRVAdapter;
    private ArrayAdapter<String> adapter;
    private MainContract.Presenter presenter;
    private static final String IMAGEURL1 = "http://cn.bing.com/th?id=OHR.GTNPBeaver_ZH-CN3940626643_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg";
    private static final String IMAGEURL2 = "http://cn.bing.com/th?id=OHR.WallaceMonument_ZH-CN4008495741_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg";
    private static final String IMAGEURL3 = "http://cn.bing.com/th?id=OHR.BlueTide_ZH-CN4055424992_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg";
    private static final String IMAGEURL4 = "http://cn.bing.com/th?id=OHR.SibWrestling_ZH-CN4106007210_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg";
    private static final String TAG = "222";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate:重新创建 " + isTaskRoot());
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: 销毁" + isTaskRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBannerImages();
    }

    /**
     * 在activity中调用 moveTaskToBack (boolean nonRoot)方法即可将activity 退到后台，注意不是finish()退出。
     * 参数说明：
     * 参数为false——代表只有当前activity是task根，指应用启动的第一个activity时，才有效;
     * 参数为true——则忽略这个限制，任何activity都可以有效。
     * 可以解决retrofit2.0+rxjava内存溢出
     */
    @Override
    public void finish() {
//        super.finish();
        moveTaskToBack(true);
    }

    private void init() {

        presenter = new MainPresenterImpl(this);
        bindView();
        questWeather();
    }

    //    绑定控件
    private void bindView() {
        locality_backup = findViewById(R.id.locality_backup);
        locality_text = findViewById(R.id.locality_text);
        now_cond = findViewById(R.id.now_cond);
        now_tmp = findViewById(R.id.now_tmp);
        now_date = findViewById(R.id.now_date);
        bannerview = findViewById(R.id.bannerview);
        forecast_recycler = findViewById(R.id.forecast_recycler);
        aqi_text = findViewById(R.id.aqi_text);
        pm25_text = findViewById(R.id.pm25_text);
        suggestion_comf = findViewById(R.id.suggestion_comf);
        suggestion_sport = findViewById(R.id.suggestion_sport);
        suggestion_cw = findViewById(R.id.suggestion_cw);
        smartrefreshlayout = findViewById(R.id.smartrefreshlayout);
        locality_linear = findViewById(R.id.locality_linear);
        locality_listview = findViewById(R.id.locality_listview);
        localityInfo = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, localityInfo);
        locality_listview.setAdapter(adapter);
        forecastInfo = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(forecastInfo);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        forecast_recycler.setLayoutManager(manager);
        forecast_recycler.setAdapter(weatherRVAdapter);
        locality_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.more_black_24dp, 0);
        locality_listview.setOnItemClickListener(onItemClickListener);
        locality_backup.setOnClickListener(onClickListener);
        locality_text.setOnClickListener(listener);
        smartrefreshlayout.setOnRefreshListener(refreshListener);
    }

    //    初始化Banner图片
    private void initBannerImages() {
        imagesLiet = new ArrayList<>();
        imagesLiet.add(IMAGEURL1);
        imagesLiet.add(IMAGEURL2);
        imagesLiet.add(IMAGEURL3);
        imagesLiet.add(IMAGEURL4);
//        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
//            @Override
//            public void accept(Boolean aBoolean) throws Exception {
//                if (aBoolean) {
//                } else {
//                    Toast.makeText(MainActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    //ListView中Item点击处理
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            questLocality(i, 0);
            locality_backup.setVisibility(View.VISIBLE);
        }
    };
    //actionbar返回按钮点击处理
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            questLocality(2, 1);
        }
    };
    //actionbar中text点击处理
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            locality_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.less_black_24dp, 0);
            questLocality(2, 2);
        }
    };
    //下拉刷新处理
    OnRefreshListener refreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            questWeather();
        }
    };

    //显示天气
    private void showWeatherInfo(Weather w) {
        locality_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.more_black_24dp, 0);
        smartrefreshlayout.finishRefresh();
        locality_linear.setVisibility(View.GONE);
        locality_backup.setVisibility(View.GONE);
        //初始化Banner
        Banner banner = new Banner(this);
        bannerview.addView(banner);
        banner.setImages(imagesLiet).setImageLoader(new GlideImageLoader()).start();
        if (w.getHeWeather().get(0).getStatus().equals("ok")) {
            locality_text.setText(w.getHeWeather().get(0).getBasic().getCity());
            locality_backup.setVisibility(View.GONE);
            now_cond.setText(w.getHeWeather().get(0).getNow().getCond_txt());
            now_tmp.setText(w.getHeWeather().get(0).getNow().getTmp() + "℃");
            now_date.setText(w.getHeWeather().get(0).getUpdate().getLoc());
            aqi_text.setText(w.getHeWeather().get(0).getAqi().getCity().getAqi());
            pm25_text.setText(w.getHeWeather().get(0).getAqi().getCity().getPm25());
            suggestion_comf.setText(w.getHeWeather().get(0).getSuggestion().getComf().getTxt());
            suggestion_sport.setText(w.getHeWeather().get(0).getSuggestion().getSport().getTxt());
            suggestion_cw.setText(w.getHeWeather().get(0).getSuggestion().getCw().getTxt());
            forecastInfo.clear();
            for (int i = 0; i < w.getHeWeather().get(0).getDaily_forecast().size(); i++) {
                WeatherforecastItem item = new WeatherforecastItem();
                item.setCond(w.getHeWeather().get(0).getDaily_forecast().get(i).getCond().getTxt_d());
                item.setDate(w.getHeWeather().get(0).getDaily_forecast().get(i).getDate());
                item.setTmpmax(w.getHeWeather().get(0).getDaily_forecast().get(i).getTmp().getMax());
                item.setTmpmin(w.getHeWeather().get(0).getDaily_forecast().get(i).getTmp().getMin());
                forecastInfo.add(item);
            }
            weatherRVAdapter.notifyDataSetChanged();
            forecast_recycler.scrollToPosition(0);
        }

    }

    //显示地址信息
    private void showLocalityInfo(List<String> l) {
        locality_linear.setVisibility(View.VISIBLE);
        localityInfo.clear();
        localityInfo.addAll(l);
        adapter.notifyDataSetChanged();
        locality_listview.setSelection(0);
    }


    @Override
    public void questLocality(int position, int flag) {
        presenter.questLocality(position, flag);
    }

    @Override
    public void questWeather() {
        presenter.questWeather();
    }

    @Override
    public void showLocality(List<String> l) {
        showLocalityInfo(l);
    }

    @Override
    public void showWeather(Weather w) {
        showWeatherInfo(w);
    }

    @Override
    public void onError(Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
