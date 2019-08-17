package equities.com.myapplication;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import static equities.com.myapplication.Service_Main_Equities.*;


/**
 * Created by Julian Dinkins on 4/25/2018.
 */

public class Fragment_Market_Kings extends Fragment {
    TextView stock, crypto;
    private RecyclerView stockitems;
    private RecyclerView cryptoitems;
    Adapter_Stock_Equities crypto_adapter;
    Adapter_Stock_Equities stock_adapter;
    Timer mTimer;
    int t =0;
    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if(t>0) {
                                    getNewData();
                                    }
                                t=t+1;
                            }
                        }, 0);
                    }

                });
            }
        };
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,

                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_kings, container, false);
        stock = rootView.findViewById(R.id.stock);
        crypto = rootView.findViewById(R.id.crypto);
        stockitems= rootView.findViewById(R.id.stock_items);
        stockitems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //stock_adapter=new Adapter_Stock_Equities(getActivity(), "Stock_Kings", stock_kings_symbollist,stock_kings_namelist,stock_kings_changelist);
        stockitems.setAdapter(stock_adapter);
        cryptoitems= rootView.findViewById(R.id.crypto_items);
        cryptoitems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //crypto_adapter=new Adapter_Stock_Equities(getActivity(), "Crypto_Kings",crypto_kings_symbolist,crypto_kings_namelist,crypto_kings_marketcaplist);
        cryptoitems.setAdapter(crypto_adapter);
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Oregon.ttf");
        stock.setTypeface(custom_font);
        crypto.setTypeface(custom_font);
        stock.setPaintFlags(stock.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        crypto.setPaintFlags(stock.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(createTimerTask(),0,15000);

        return rootView;

    }
    public void getNewData(){
        new ASYNCUpdateKings().execute();

    }
    public class ASYNCUpdateKings extends AsyncTask<Integer, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Integer... integers) {
            Service_Main_Equities sme =new Service_Main_Equities();
            sme.clearKingsData();
            sme.getMarketKings();
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if(crypto_kings_namelist.size()>0||stock_kings_namelist.size()>0){
                setKingsUserVisibleHint(true);}else{
                mTimer = new Timer();
                mTimer.scheduleAtFixedRate(createTimerTask(),0,2000);
        }
    }}
    public void setKingsUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){

//            crypto_adapter.notifyDataSetChanged();
  //          stock_adapter.notifyDataSetChanged();
            //crypto_adapter=new Adapter_Stock_Equities(getActivity(), "Crypto_Kings",crypto_kings_symbolist,crypto_kings_namelist,crypto_kings_marketcaplist);
            cryptoitems.setAdapter(crypto_adapter);
            //stock_adapter=new Adapter_Stock_Equities(getActivity(), "Stock_Kings", stock_kings_symbollist,stock_kings_namelist,stock_kings_changelist);
            stockitems.setAdapter(stock_adapter);
        }}
}


