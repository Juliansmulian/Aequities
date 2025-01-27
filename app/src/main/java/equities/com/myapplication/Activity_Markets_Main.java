package equities.com.myapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;


import static equities.com.myapplication.Constructor_App_Variables.*;

public class Activity_Markets_Main extends AppCompatActivity {
    Database_Local_Aequities check_saved = new Database_Local_Aequities(Activity_Markets_Main.this);
    RequestQueue requestQueue;
    TextView txt;
    private TabLayout pagetabs;
    String[] main_page_stock_news_urls = new String[]{"https://www.msn.com/en-us/money","https://money.cnn.com/data/markets/","https://finance.yahoo.com/","https://www.google.com/finance","https://www.bloomberg.com/"};
    String[] main_page_crypto_news_urls = new String[]{"https://www.coindesk.com","https://cryptonews.com"};
    int[] worldMarketICONS = new int[]{R.drawable.direction_markets, R.drawable.direction_news, R.drawable.direction_youtube_video};
    int[] stockMarketICONS = new int[]{R.drawable.direction_down, R.drawable.direction_up, R.drawable.direction_kings};
    static Timer mTimer;
    int t =0;
    public TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if(t>0) {
                                    Constructor_App_Variables app_info =new Constructor_App_Variables();
                                    if(app_info.getMarketName().equalsIgnoreCase("empty")){
                                        Log.d("TAG", "Doing nothing");
                                    }else {
                                        new ASYNCUpdateFinancialData().execute();
                                        Log.d("TAG", "METHOD asyncUpdateFinancialData executed");
                                    }
                                    }
                                t=t+1;
                            }
                        }, 0);
                    }

                });
            }
        };
    }
    public class ASYNCUpdateFinancialData extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... integers) {
            Service_Chosen_Equity service_chosen_equity = new Service_Chosen_Equity();
            service_chosen_equity.updateFinancialData();
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if(Double.parseDouble(String.valueOf(graph_high.size()))>0){
                TextView a_price_change =findViewById(R.id.aequity_price_change);
                TextView a_price=findViewById(R.id.aequity_price);
                if(current_percentage_change.size()==0){ a_price_change.setText("Updating");}else{
                    a_price.setText(""+current_updated_price.get(0));
                    if (current_percentage_change.get(0).toString().contains("-")){
                        a_price_change.setTextColor(getResources().getColor(R.color.colorRed));
                    }else{a_price_change.setTextColor(getResources().getColor(R.color.colorGreen));}
                    a_price_change.setText(""+current_percentage_change.get(0));
                }}else{
                //DONT CHANGE ANYTHING
            }
        }

    }
    LinearLayout equityView;
    protected ArrayAdapter<String> ad;
    private static Toolbar toolbar;
    TableRow table_tabs;
    BottomNavigationView  activity_tabs;
    ProgressBar progress;
    public static ArrayList<String> searchview_arraylist = new ArrayList<>();
    public static ArrayList<String> aequity_symbol_arraylist = new ArrayList<>();
    public static ArrayList<String> aequity_name_arraylist = new ArrayList<>();
    public static ArrayList<String> aequity_type_arraylist = new ArrayList<>();
    public TabLayout tabs;
    private static final String TAG = "ActivityMain";
    static String fullScreen ="no";
    static Constructor_App_Variables ap_info = new Constructor_App_Variables();
    ImageView imageView;
    LinearLayout lin_lay;
    RelativeLayout progLayout;
    Animation centerLinear;
    boolean forward;
    static String repo;
    public static boolean db_exist =false;
    Context context =this;
    static InterstitialAd mInterstitialAd;
    static ViewPager pager;
    PagerAdapter_WorldMarkets adapter = new PagerAdapter_WorldMarkets(getSupportFragmentManager());
    String googleAdsauthentication = "ca-app-pub-6566728316210720/4471280326";

    @Override
    protected void onStart() {
        super.onStart();
        //Starting Car
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Put car in gear


    }

    @Override
    protected void onPause() {
        super.onPause();


        //Put car in neutral

    }

    @Override
    protected void onStop() {
        super.onStop();
        //Turn off car

    }


    protected void onRestart(){
        super.onRestart();
        //System.out.println("RESTARTING APPLICATION");
    }

    public void onBackPressed() {
        ViewPager pager = findViewById(R.id.viewpager);
        if (pager.getVisibility()==View.VISIBLE){
            finish();}else {
            equityView = findViewById(R.id.equityView);
            equityView.setVisibility(View.GONE);
            reloadAllData();
            Constructor_App_Variables app_info =new Constructor_App_Variables();
            app_info.setMarketName("empty");
            new AsyncOnClickEquity(this).cancel(true);
            new AsyncForBackPressedSavedData(Activity_Markets_Main.this).execute();
        }

    }


    public static String AssetJSONFile(String filename, Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();
        return new String(formArray);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, googleAdsauthentication);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(googleAdsauthentication);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        checkInternetConnection();
        get_exchange_info();
        setContentView(R.layout.splash);
        lin_lay =findViewById(R.id.lin_lay);
        centerLinear = AnimationUtils.loadAnimation(this, R.anim.center);
        imageView =findViewById(R.id.imageView);
        progress= findViewById(R.id.progressBar);
        txt = findViewById(R.id.output);
        new MainPageAsyncMethod(Activity_Markets_Main.this).execute();    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            Log.v(TAG, "Internet Connection Not Present");
            this.finishAffinity();
            return false;
        }
    }
    public static void get_exchange_info() {

        crypto_exchange_name.add("Binance");
        crypto_exchange_name.add("Hbus");
        crypto_exchange_name.add("Coinbase");
        crypto_exchange_name.add("Cryptopia");;
        crypto_exchange_name.add("CryptoBridge");


        crypto_exchange_url.add("https://www.binance.com/?ref=35795495");
        //Referral
        crypto_exchange_url.add("https://www.hbus.com/invite/inviteRank?invite_code=kgd23");
        //Referral
        crypto_exchange_url.add("https://www.coinbase.com/join/5a2cc6b6f3b80300ef643aa4");
        //Referral
        crypto_exchange_url.add("https://www.cryptopia.co.nz/Register?referrer=juliansmulian");
        //Referral
        crypto_exchange_url.add("https://crypto-bridge.org/");



        crypto_exchange_image.add("R.drawable.exchange_crypto_binance");
        crypto_exchange_image.add("R.drawable.exchange_crypto_hbus");
        crypto_exchange_image.add("R.drawable.exchange_crypto_coinbase");
        crypto_exchange_image.add("R.drawable.exchange_crypto_cryptopia");
        crypto_exchange_image.add("R.drawable.exchange_crypto_cryptobridge");


        stock_exchange_name.add("E*TRADE");
        stock_exchange_name.add("TD Ameritrade");
        stock_exchange_name.add("Merrill Edge");
        stock_exchange_name.add("Charles Schwab ");
        stock_exchange_name.add("Robinhood");

        stock_exchange_url.add("https://www.etrade.com");
        stock_exchange_url.add("https://www.tdameritrade.com/home.page");
        stock_exchange_url.add("https://www.merrilledge.com/");
        stock_exchange_url.add("https://www.schwab.com/");
        stock_exchange_url.add("https://www.robinhood.com/");

        stock_exchange_image.add("exchange_stock_etrade");
        stock_exchange_image.add("exchange_stock_tdameritrade");
        stock_exchange_image.add("exchange_stock_merrill_edge");
        stock_exchange_image.add("exchange_stock_schwab");
        stock_exchange_image.add("exchange_stock_robinhood");

        HashSet sen = new HashSet();
        sen.addAll(stock_exchange_name);
        stock_exchange_name.clear();
        stock_exchange_name.addAll(sen);

        HashSet seu = new HashSet();
        seu.addAll(stock_exchange_url);
        stock_exchange_url.clear();
        stock_exchange_url.addAll(seu);

        HashSet sei = new HashSet();
        sei.addAll(stock_exchange_image);
        stock_exchange_image.clear();
        stock_exchange_image.addAll(sei);


    }
    public class MainPageAsyncMethod extends AsyncTask<Integer, Integer, String> {

        private WeakReference<Activity_Markets_Main> activityReference;
        MainPageAsyncMethod(Activity_Markets_Main context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Constructor_News_Feed constructor_news_feed = new Constructor_News_Feed();
            //constructor_news_feed.setNews_search_term("World Markets");
        }
        @Override
        protected String doInBackground(Integer... params) {
            ArrayList aa = check_saved.getType();
            if(aa.size()>0){
                getSavedEquities();}
            return "task finished";
        }
        @Override
        protected void onPostExecute(String result) {
            new AsyncWorldMarketData(Activity_Markets_Main.this).execute();

        }

    }

    protected void setMainPage() {
        setContentView(R.layout.activity_main);
        setJSON_INFO();
        equityView = findViewById(R.id.equityView);
        equityView.setVisibility(View.GONE);
        toolbar = findViewById(R.id.toolbar);
        progLayout=findViewById(R.id.progLayout);
        openSearchView();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        pager = findViewById(R.id.viewpager);
        pager.setVisibility(View.VISIBLE);
        ViewPager market_pager = findViewById(R.id.market_pager);
        market_pager.setVisibility(View.GONE);
        table_tabs = findViewById(R.id.table_tabs);
        activity_tabs =findViewById(R.id.activity_tabs);
        pagetabs = findViewById(R.id.tabs);
        setupWorldMarketsViewPager(pager);
        pagetabs.setupWithViewPager(pager);
        if(pager.getVisibility()==View.VISIBLE){
            progLayout.setVisibility(View.GONE);
        }else {
            progLayout.setVisibility(View.VISIBLE);

        }
        new AsyncOtherAppData(Activity_Markets_Main.this).execute();
        activity_tabs.setItemIconTintList(null);
        activity_tabs.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        setupWorldMarketsViewPager(pager);
                        break;
                    case R.id.stock:
                        setupStockPager(pager);
                        break;
                    case R.id.crypto:
                        setupCryptoPager(pager);
                        break;
                }
                return true;
            }
        });

    }
    public void setupWorldMarketsViewPager(ViewPager viewPager) {
        viewPager.removeAllViews();
        viewPager.setAdapter(null);
        adapter.removeAllFrag();
        adapter.notifyDataSetChanged();
        adapter.addFrag(new Fragment_Saved(),"Main");
        adapter.addFrag(new Fragment_Markets(), getString(R.string.markets));
        adapter.addFrag(new Fragment_App_News(), getString(R.string.news));
        adapter.addFrag(new Fragment_Video(),getString(R.string.title_video));
        viewPager.setAdapter(adapter);
        //viewPager.setOffscreenPageLimit(7);

    }
    public void setupStockPager(ViewPager viewPager) {
        viewPager.removeAllViews();
        viewPager.setAdapter(null);
        adapter.removeAllFrag();
        adapter.notifyDataSetChanged();
        adapter.addFrag(new Fragment_Market_Kings_Stock(),getString(R.string.market_kings));
        adapter.addFrag(new Fragment_Winners_Stock(), getString(R.string.leaders));
        adapter.addFrag(new Fragment_Losers_Stock(), getString(R.string.losers));
        adapter.addFrag(new Fragment_Ipos(), getString(R.string.ipo));
        adapter.addFrag(new Fragment_Stock_App_News(), getString(R.string.news));
        //  Delete News and add  adapter.addFrag(new Fragment_Stock_Media(),getString(R.string.media));
        viewPager.setAdapter(adapter);

    }
    public void setupCryptoPager(ViewPager viewPager) {
        viewPager.removeAllViews();
        viewPager.setAdapter(null);
        adapter.removeAllFrag();
        adapter.notifyDataSetChanged();
        adapter.addFrag(new Fragment_Market_Kings_Crypto(), getString(R.string.market_kings));
        adapter.addFrag(new Fragment_Winners_Crypto(), getString(R.string.leaders));
        adapter.addFrag(new Fragment_Losers_Crypto(), getString(R.string.losers));
        adapter.addFrag(new Fragment_Masternodes(), "Mnodes");
        adapter.addFrag(new Fragment_Icos(), getString(R.string.ico));
        adapter.addFrag(new Fragment_Crypto_App_News(), getString(R.string.news));
    //  Delete News and add  adapter.addFrag(new Fragment_Crypto_Media(),getString(R.string.media));
        viewPager.setAdapter(adapter);

    }
    public void setupChosenViewPager(ViewPager viewPager) {
        PagerAdapter_WorldMarkets adapter = new PagerAdapter_WorldMarkets(getSupportFragmentManager());
        if (ap_info.getMarketType().equals("Index")||ap_info.getMarketType().equals("Stock")) {
            adapter.addFrag(new Fragment_Analysis(), getString(R.string.action_analysis));
            adapter.addFrag(new Fragment_News_Chosen(), getString(R.string.title_news));
            adapter.addFrag(new Fragment_Video(), getString(R.string.title_video));
            adapter.addFrag(new Fragment_StockTwits(),getString(R.string.stocktwits));
        }else {
            adapter.addFrag(new Fragment_Analysis(), getString(R.string.action_analysis));
            adapter.addFrag(new Fragment_News_Chosen(), getString(R.string.title_news));
            adapter.addFrag(new Fragment_Video(), getString(R.string.title_video));
            adapter.addFrag(new Fragment_Exchanges(), getString(R.string.title_exchanges));
            adapter.addFrag(new Fragment_StockTwits(), getString(R.string.stocktwits));

        }
        viewPager.setAdapter(adapter);
    }

    public void setJSON_INFO() {
        try {
            String jsonLocation = AssetJSONFile("rd.json", Activity_Markets_Main.this);
            JSONObject obj = new JSONObject(jsonLocation);

            JSONArray Arry = obj.getJSONArray("ALL");

            for (int i = 0; i < Arry.length(); i++) {
                JSONArray childJsonArray = Arry.getJSONArray(i);
                String sym = childJsonArray.getString(0);
                aequity_symbol_arraylist.add(sym);
                String nam = childJsonArray.getString(1);
                aequity_name_arraylist.add(nam);
                String typ = childJsonArray.getString(2);
                aequity_type_arraylist.add(typ);
                searchview_arraylist.add(sym + "  " + nam + "  " + typ);
                //Log.println(Log.INFO,"TAG",aequity_symbol_arraylist.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void reloadAllData() {
        PagerAdapter_WorldMarkets adapter = new PagerAdapter_WorldMarkets(getSupportFragmentManager());
        Fragment_Analysis fragment_analysis = new Fragment_Analysis();
        adapter.removeFrag(fragment_analysis, "Fragment_Analysis");
//        mTimer.cancel();
        graph_date.clear();
        graph_high.clear();
        graph_volume.clear();
        feedItems.clear();
        stocktwits_feedItems.clear();
        image_video_url.clear();
        video_url.clear();
        video_title.clear();
    }

    public void openSearchView() {
        ad = new ArrayAdapter<String>(Activity_Markets_Main.this, R.layout.searchbar,
                R.id.searchtool, searchview_arraylist);
        AutoCompleteTextView chosen_searchView_item = findViewById(R.id.searchtool);
        chosen_searchView_item.setAdapter(ad);


        chosen_searchView_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchview_arraylist.clear();
                new AsyncOnClickEquity(Activity_Markets_Main.this).cancel(true);
                new ASYNCUpdateFinancialData().cancel(true);
                setJSON_INFO();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                ViewPager mp = findViewById(R.id.market_pager);
                mp.setVisibility(View.GONE);
                String[] split_marketinfo = ad.getItem(position).toString().split("  ");
                ap_info.setMarketSymbol(split_marketinfo[0]);
                ap_info.setMarketName(split_marketinfo[1]);
                ap_info.setMarketType(split_marketinfo[2]);
                chosen_searchView_item.onEditorAction(EditorInfo.IME_ACTION_DONE);
                new AsyncOnClickEquity(Activity_Markets_Main.this).execute();
                chosen_searchView_item.setText("");
                reloadAllData();

            }
        });
    }

    public void primaryGetCrypto_Method(){
        long startTime = System.nanoTime();
        String newString= null;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        if(ap_info.getMarketName().equalsIgnoreCase("XRP")){
            newString = "Ripple";
        }else {
            newString = ap_info.getMarketName();
            //("1 -"+newString);
        }if(newString.contains(" ")){
            newString =newString.replace(" ","-");
        }

        final String url = "https://api.coinmarketcap.com/v1/ticker/"+newString;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {


            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    JSONObject heroObject = null;
                    try {
                        heroObject = response.getJSONObject(i);
                        ap_info.setMarketSymbol(heroObject.getString("symbol"));
                        ap_info.setCurrent_Aequity_Price(heroObject.getString("price_usd").substring(0,6));
                        //volume_24 =heroObject.getString("24h_volume_usd");
                        DecimalFormat df = new DecimalFormat("0.00");
                        df.setMaximumFractionDigits(2);
                        String y = heroObject.getString("available_supply");
                        if(y !="null") {
                            double dd = Double.parseDouble(y);
                            y = df.format(dd);
                            int z = y.length();
                            long tt = 1000000000000L;
                            if (z < 10) {
                                y = String.valueOf(dd / 1000);
                                ap_info.setMarketSupply(y.substring(0, 5) + "TH");
                            }
                            if (z <= 12) {
                                y = String.valueOf(dd / 1000000);
                                ap_info.setMarketSupply(y.substring(0, 4) + "M");
                            }
                            if (z > 12) {
                                y = String.valueOf(dd / 1000000000);
                                try{
                                ap_info.setMarketSupply(y.substring(0, 5) + "B");}
                                catch (StringIndexOutOfBoundsException e){
                                    ap_info.setMarketSupply("UNKNOWN");
                                }
                            }
                            if (z > 15) {
                                y = String.valueOf(dd / tt);
                                ap_info.setMarketSupply(y.substring(0, 5) + "T");
                            }

                            ap_info.setCurrent_Aequity_Price_Change(heroObject.getString("percent_change_24h") + "%");
                            ap_info.setCurrent_volume(heroObject.getString("24h_volume_usd"));
                            String p =heroObject.getString("market_cap_usd");
                            double d = Double.parseDouble(p);
                            p =df.format(d);
                            int l =p.length();
                            long t = 1000000000000L;
                            if (l<10){p= String.valueOf(d/1000);
                                ap_info.setMarketCap(p.substring(0,5)+"TH");}
                            if (l<=12){p= String.valueOf(d/1000000);
                                ap_info.setMarketCap(p.substring(0,5)+"M");}
                            if (l>12){p= String.valueOf(d/1000000000);
                                ap_info.setMarketCap(p.substring(0,5)+"B");}
                            if (l>15){p= String.valueOf(d/t);
                                ap_info.setMarketCap(p.substring(0,5)+"T");}}
                        else{ap_info.setMarketSupply("Unknown");
                            ap_info.setMarketCap("Unknown");
                            ap_info.setCurrent_Aequity_Price_Change("Unknown");}

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    catch(Exception z){}


                }

                //btc_market_cap_change=

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //("Something ahppened An Error occured while making the request "+error);
            }
        });
        requestQueue.add(jsonArrayRequest);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        //("CRYPTO TIME IS " + duration / 1000000000 + " seconds");

    }

    public void primaryGetStock_Method(){
        String symbol =ap_info.getMarketSymbol();
        String apikey ="XBA42BUC2B6U6G5C";
        String url ="https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="+symbol+"&apikey="+apikey;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // your response
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    // get Time

                    JSONObject time = jsonObject.getJSONObject("Global Quote");
                    Iterator<String> iterator = time.keys();

                        String price = time.getString("05. price");
                        String change= time.getString("10. change percent");
                    DecimalFormat numberFormat = new DecimalFormat("#.00");
                    double d =Double.parseDouble(price);
                    String fclose=numberFormat.format(d);
                        ap_info.setCurrent_Aequity_Price(fclose);
                        ap_info.setCurrent_Aequity_Price_Change(change);





                } catch (JSONException e) {
                    //aLTERNATIVE METHOD
                    Service_Chosen_Equity service_chosen_equity = new Service_Chosen_Equity(Activity_Markets_Main.this);
                    service_chosen_equity.backUp();
                   // service_chosen_equity.updateFinancialData();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //("An Error occured while making the ST request");
            }
        });
        requestQueue.add(jsonObjectRequest);




    }

    public void primaryGetSocialMedia_Method(){
        String market_symbol=ap_info.getMarketSymbol();
        if (market_symbol.contains("%5E")){
        market_symbol =market_symbol.replace("%5E","");}
        if (ap_info.getMarketType().equals("Crypto")||(ap_info.getMarketType().equals("Cryptocurrency"))) {
            market_symbol =market_symbol+".X";
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://api.stocktwits.com/api/2/streams/symbol/"+market_symbol+".json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray responseJSONArray = response.getJSONArray("messages");
                    for (int i = 0; i < responseJSONArray.length(); i++) {
                        JSONObject user_info = (JSONObject) responseJSONArray.getJSONObject(i).get("user");
                        String message_time = (String) responseJSONArray.getJSONObject(i).get("created_at");
                        String message = (String) responseJSONArray.getJSONObject(i).get("body");
                        Iterator x = user_info.keys();
                        Constructor_Stock_Twits item = new Constructor_Stock_Twits();
                        item.setMessage_time(message_time);
                        item.setUser_name("" + user_info.get("username"));
                        String url = user_info.get("avatar_url_ssl").toString();
                        item.setUser_image_url(url);
                        item.setMessage(message);

                        String[] words = message.split("\\s+");


                        Pattern pattern = Patterns.WEB_URL;
                        for(String word : words)
                        {
                            if(pattern.matcher(word).find())
                            {
                                if(!word.toLowerCase().contains("http://") && !word.toLowerCase().contains("https://"))
                                {
                                    word = "http://" + word;
                                }
                                item.setMessage_link(word);

                            }
                        }

                        JSONArray jsonArray =new JSONArray();
                        while(x.hasNext()){
                            String key = (String) x.next();
                            jsonArray.put(user_info.get(key));

                        }


                    stocktwits_feedItems.add(item);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
//                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Constructor_Stock_Twits item = new Constructor_Stock_Twits();
                item.setMessage_time("No Social data for "+ap_info.getMarketSymbol());
                item.setUser_name("");
                item.setUser_image_url(" - ");
                item.setMessage("");
                stocktwits_feedItems.add(item);
                //("An Error occured while making the request");
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    public static void primaryGetCryptoExchange_Method() {
        Document doc = null;
        String name = ap_info.getMarketName();
        //("THIS IS THE MARKET NAME "+name);
        if (name.contains(" ")){
            name =name.replace(" ","-");
        }

        try {
            doc = Jsoup.connect("https://coinmarketcap.com/currencies/"+name+"/#markets").timeout(10 * 1000).get();
            Element tb = doc.getElementById("markets-table");
            Elements rows = null;
            if(tb!=null) {
                rows = tb.select("tr");
                for(int i =0; i<rows.size();i++){
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    for(int z =0; z<cols.size();z++) {
                        String line = cols.get(1).text();
                        if(!exchange_list.contains(line))
                            exchange_list.add(line);
                    }
                }
            }else{
                getWorldCoinIndex();
            }

            //("EXCHANGE LIST"+exchange_list);
        } catch (HttpStatusException x){
            //("There is no information about "+name +", so now we try world coinindex");
            getWorldCoinIndex();
        } catch (IOException e) {

            e.printStackTrace(); }
        HashSet<String> set = new HashSet<String>();

        for (int i = 0; i < crypto_exchange_name.size(); i++)
        {
            for (int j = 0; j < exchange_list.size(); j++)
            {
                if(crypto_exchange_name.get(i).equals(exchange_list.get(j)))
                {
                    aequity_exchanges.add(""+ crypto_exchange_name.get(i));
                }
            }
        }

        // return common elements.
        ////("Common element : "+(aequity_exchanges));




    }

    public static void getWorldCoinIndex(){
        Document doc = null;
        String name = ap_info.getMarketName();
        try {
            doc = Jsoup.connect("https://www.worldcoinindex.com/coin/"+name).timeout(10 * 1000).get();
            String s = doc.getElementsByClass("mob-exchange exchange").text();
            List<String> myList = new ArrayList<String>(Arrays.asList(s.split(" ")));
            for(int i=0; i<myList.size();i++){
                exchange_list.add(myList.get(i));
                ////(i+" "+exchange_list.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public static void historic_daily_percentage_change() {
        Double a=0.00;if(graph_high.size()>0) {
            for (int i = 0; i < graph_high.size(); i++) {
                if (graph_high.get(i) != null) {
                    if (graph_high.get(i) == "null") {
                        graph_high.get(i).toString().replace("null", "0.00");
                    }
                    a = new Double(graph_high.get(i).toString().replace(",", "").replace("<", "").replace("/", "").replace("-", ""));
                } else {
                    a = 0.00;
                }
                if (i > 0) {
                    int z = i - 1;
                    if (graph_high != null) {
                        double b = new Double(graph_high.get(z).toString().replace(",", "").replace("<", "").replace("/", ""));
                        double c = ((a - b) / a) * 100;
                        DecimalFormat numberFormat = new DecimalFormat("#0.00");
                        String add = numberFormat.format(c).replace("-", "");
                        graph_change.add(add);
                    } else {
                        graph_change.add("0");
                    }

                }
            }
        }

    }


    public void get_saved_stock_price_change(String taco){
        //String symbol =ap_info.getMarketSymbol();
        String apikey ="XBA42BUC2B6U6G5C";
        String url ="https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="+taco+"&apikey="+apikey;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    JSONObject time = jsonObject.getJSONObject("Global Quote");
                    Iterator<String> iterator = time.keys();
                    String change= time.getString("10. change percent");
                    change=change.replace("%","");
                    DecimalFormat numberFormat = new DecimalFormat("#.00");
                    double d =Double.parseDouble(change);
                    String fclose=numberFormat.format(d);
                    fclose=fclose+"%";
                    current_percentage_change.add(fclose);
                    String price= time.getString("05. price");
                    double e =Double.parseDouble(price);
                    String p=numberFormat.format(e);
                    current_saved_price.add(p);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //("An Error occured while making the ST request");
            }
        });
        requestQueue.add(jsonObjectRequest);




    }

    public void get_saved_crypto_price_change(String taco){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        if(taco.equalsIgnoreCase("XRP")){
            taco = "Ripple";
        }else {

        }if(taco.contains(" ")){
            taco =taco.replace(" ","-");
        }

        final String url = "https://api.coinmarketcap.com/v1/ticker/"+taco;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject heroObject = null;
                    try {
                        heroObject = response.getJSONObject(i);
                        current_percentage_change.add(heroObject.getString("percent_change_24h") + "%");
                        DecimalFormat numberFormat = new DecimalFormat("#.00");
                        double d =Double.parseDouble(heroObject.getString("price_usd"));
                        String fclose=numberFormat.format(d);
                        current_saved_price.add(fclose);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    catch(Exception z){}


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //("Something ahppened An Error occured while making the request "+error);
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public void getSavedEquities(){
        getTopNewsStories();
        ArrayList a = check_saved.getName();
        ArrayList b = check_saved.getSymbol();
        ArrayList c = check_saved.getType();
        for( int x=0;x<a.size();x++) {
            if (c.get(x).equals("Stock")||c.get(x).equals("Index")) {
                get_saved_stock_price_change(""+check_saved.getSymbol().get(x));
            } else {
                ArrayList d = check_saved.getName();
                if (d.size()>0) {
                   get_saved_crypto_price_change(""+d.get(x));


                }
            }

        }check_saved.close();
    }


    public void getTopNewsStories(){

        Double a =1.00;
        //Double.parseDouble(ap_info.getBitcoinPrice());
        Double b =2.00;
        //Double.parseDouble(ap_info.getNasdaqPrice());
        Random random = new Random();
        int stock = random.nextInt( 5);
        int crypto = random.nextInt(2);
        System.out.println("RANDOM CRYPTO STORY IS "+crypto);
        if (a>b){
            Document doc = null;
            try {
                doc = Jsoup.connect(main_page_stock_news_urls[stock]).timeout(10 * 1000).get();
                if(stock==0){}
                if(stock==1){}
                if(stock==2){}
                if(stock==3){}
                if(stock==4){}



            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(doc);
            //Log.println(Log.INFO,"TAG", String.valueOf(doc));

        }else{
            Document doc = null;
            try {
                doc = Jsoup.connect(main_page_crypto_news_urls[crypto]).timeout(10 * 1000).get();
                crypto=1;
                if(crypto==0){
                    Element f =doc.getElementById("featured-articles");
                    Element link = f.select("a").first();
                    Element image = link.select("img").first();
                    String url = image.absUrl("src");
                    top_story_image_url = url;
                    top_story_title =link.attr("title");
                    top_story_url = link.attr("href");
                    //System.out.println("LINK TITLE " + link.attr("title"));
                    //System.out.println("LINK URL " + link.attr("href"));
                    //System.out.println("LINK IMAGE URL " + url);
                }
                if(crypto==1){
                    int rand = random.nextInt( 3);
                    System.out.println("THIS IS RAND " + rand);
                    Element z = doc.getElementsByClass("cn-tile row article").get(rand);
                    Element link = z.select("a").first();
                    Element image = z.select("img").first();
                    String url = image.absUrl("src");
                    Element i = doc.getElementsByClass("props").get(rand+1);
                    String alink = i.select("a").get(1).text();
                    top_story_image_url = url;
                    top_story_title =alink;
                    top_story_url = main_page_crypto_news_urls[crypto]+link.attr("href");
                    //System.out.println("LINK IMAGE URL " + url);
                    //System.out.println("LINK URL " + main_page_crypto_news_urls[crypto]+link.attr("href"));
                    //System.out.println("LINK TITLE " + alink);
                }
/**
                if(crypto==2){
                    Elements e =doc.select("div.fsp");
                    Element link = e.select("a").first();
                    Element image = link.select("amp-img").first();
                    String url = image.absUrl("src");
                    top_story_image_url = url;
                    top_story_title =link.attr("title");
                    top_story_url = link.attr("href");
                    //System.out.println("LINK TITLE " + link.attr("title"));
                    //System.out.println("LINK URL " + link.attr("href"));
                    //System.out.println("LINK IMAGE URL " + url);

                    //System.out.println("hello "+link);
                }
   */
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Log.println(Log.INFO,"TAG", String.valueOf(doc));
        }


    }
}