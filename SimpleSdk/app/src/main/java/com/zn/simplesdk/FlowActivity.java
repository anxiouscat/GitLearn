package com.zn.simplesdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import com.oversea.ads.api.ImageFlowView;
import com.oversea.ads.api.NativeAd;
import com.oversea.ads.base.AdErrorBase;
import com.oversea.ads.base.AdsLoaderListener;
import com.oversea.ads.loaders.FlowLoader;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.zn.simplesdk.FlowViewAdapter.VIEWTYPE_ADS;

public class FlowActivity extends Activity {

    private RecyclerView mInfoflowListView;
    private FlowViewAdapter mFlowAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);

        initView();
        startLoad();
    }


    private void initView() {

        mInfoflowListView = findViewById(R.id.lv);
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mInfoflowListView.setLayoutManager(new LinearLayoutManager(this));
        mFlowAdapter = new FlowViewAdapter(this);
        mInfoflowListView.setAdapter(mFlowAdapter);

        String[] items = getResources().getStringArray(R.array.items);
        for (String item: items) {
            mFlowAdapter.getData().add(new FlowViewAdapter.Item(FlowAdapter.VIEWTYPE_INFO, item));
        }
        mFlowAdapter.notifyDataSetChanged();

    }

    private void startLoad() {
        loadFlow();
    }


    private void loadFlow() {
        final FlowLoader loader = new FlowLoader(this);
        loader.setListener(new AdsLoaderListener() {

            @Override
            public void onAdsLoaded(List<NativeAd> items) {
                final List<NativeAd> ads =  items;
                if( ads.size() == 0 ) {
                    return;
                }

                List<FlowViewAdapter.Item> data = mFlowAdapter.getData();
                final int gap = data.size() / ads.size();
                int index = gap;
                FlowViewAdapter.Item item;
                int type = VIEWTYPE_ADS;
                ImageFlowView view;
                for (NativeAd ad : ads) {
                    view = ImageFlowView.create(getBaseContext(), ImageView.ScaleType.FIT_XY);
                    view.setNativeAd(ad);
                    view.setCustomSize(480, WRAP_CONTENT);

                    item = new FlowViewAdapter.Item(type, view);
                    if (data.size() > index) {
                        data.add(index, item);
                        index += 2;
                    } else {
                        data.add(item);
                    }
                    mFlowAdapter.put(type, view);
                    type++;
                }

                mFlowAdapter.notifyDataSetChanged();

            }

            @Override
            public void onAdError(AdErrorBase adErrorBase) {
                Log.d("Demo","err " + adErrorBase.getErrorMessage());
                Toast.makeText(FlowActivity.this, "error, " + adErrorBase.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loader.loadAds();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
