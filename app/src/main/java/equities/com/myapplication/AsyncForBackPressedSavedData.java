package equities.com.myapplication;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import java.lang.ref.WeakReference;
import static equities.com.myapplication.Constructor_App_Variables.aequity_exchanges;
import static equities.com.myapplication.Constructor_App_Variables.exchange_list;
import static equities.com.myapplication.Constructor_App_Variables.graph_change;
import static equities.com.myapplication.Constructor_App_Variables.graph_date;
import static equities.com.myapplication.Constructor_App_Variables.graph_high;
import static equities.com.myapplication.Constructor_App_Variables.graph_volume;
import static equities.com.myapplication.Constructor_App_Variables.stocktwits_feedItems;

public class AsyncForBackPressedSavedData extends AsyncTask<Integer, Integer, String> {

        private WeakReference<Activity_Markets_Main> activityReference;
        AsyncForBackPressedSavedData(Activity_Markets_Main context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Activity_Markets_Main activity = activityReference.get();
            ViewPager pager = activity.findViewById(R.id.viewpager);

            if (pager.getVisibility()== View.VISIBLE){
                activity.finishAffinity();}else{
                if (activity.mInterstitialAd.isLoaded()) {
                    activity.mInterstitialAd.show();
                }else{
                    //mInterstitialAd.setAdUnitId("ca-app-pub-6566728316210720/4471280326");
                    AdRequest adRequest = new AdRequest.Builder().build();
                    activity.mInterstitialAd.loadAd(adRequest);
                    activity.mInterstitialAd.show();
                }
                activity.mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        //("Ad is showing");
                        activity.mInterstitialAd.show();
                    }
                    @Override
                    public void onAdClosed() {pager.setVisibility(View.VISIBLE);
                        ViewPager market_pager = activity.findViewById(R.id.market_pager);
                        market_pager.setVisibility(View.GONE);
                    }
                });
        }}

        @Override
        protected String doInBackground(Integer... params) {
            //Activity_Markets_Main activity = activityReference.get();
           // ArrayList aa = activity.check_saved.getType();
           // if(aa.size()>0) {
              // // activity.getSavedEquities();
            //}

            return "task finished";
        }
        @Override
        protected void onPostExecute(String result) {
            Activity_Markets_Main activity = activityReference.get();
            ViewPager pager = activity.findViewById(R.id.viewpager);
            if (activity == null || activity.isFinishing()) return;
            graph_change.clear();
            exchange_list.clear();
            aequity_exchanges.clear();
            stocktwits_feedItems.clear();
            graph_date.clear();
            graph_high.clear();
            graph_volume.clear();
            pager.setVisibility(View.VISIBLE);
            ViewPager market_pager = activity.findViewById(R.id.market_pager);
            BottomNavigationView t = activity.findViewById(R.id.activity_tabs);
            t.setVisibility(View.VISIBLE);
            market_pager.setVisibility(View.GONE);
            TabLayout tabs = activity.findViewById(R.id.tabs);
            //activity.setupWorldMarketsViewPager(pager);
            tabs.setupWithViewPager(pager);
            BottomNavigationView activity_tabs =activity.findViewById(R.id.activity_tabs);

            activity_tabs.setItemIconTintList(null);
            activity_tabs.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override

                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            activity.setupWorldMarketsViewPager(pager);
                            break;
                        case R.id.stock:
                            activity.setupStockPager(pager);
                            break;
                        case R.id.crypto:
                            activity.setupCryptoPager(pager);
                            break;
                    }
                    return true;
                }
            });}





    }