package equities.com.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static equities.com.myapplication.Constructor_App_Variables.crypto_market_news_feedItems;
import static equities.com.myapplication.Constructor_App_Variables.world_markets_news_feedItems;


/**
 * Created by Julian Dinkins on 4/25/2018.
 */

public class Fragment_Crypto_App_News extends Fragment {
    private RecyclerView all_news_feed;

    public Fragment_Crypto_App_News() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_news, container, false);

        all_news_feed=rootView.findViewById(R.id.all_news_feed);
        all_news_feed.setAdapter(new Adapter_Crypto_News_Feed(getActivity(), crypto_market_news_feedItems));
        all_news_feed.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return rootView;
    }




}

