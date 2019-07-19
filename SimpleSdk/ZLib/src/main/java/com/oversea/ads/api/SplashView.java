package com.oversea.ads.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.oversea.ads.AdsSDK;
import com.oversea.ads.easyio.ResDownloader;
import com.oversea.ui.supportx.Nullable;
import com.oversea.ads.base.AdErrorBase;
import com.oversea.ads.base.AdListenerBase;
import com.oversea.ads.cfg.Cfg;
import com.oversea.ads.loaders.SplashLoader;
import com.oversea.ads.util.DensityUtil;
import com.xad.engine.interfaces.IParseCallBack;
import com.xad.engine.interfaces.impl.BaseEngineControl;

import java.util.HashMap;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.ImageView.ScaleType.FIT_XY;
import static com.oversea.ads.easyio.ResDownloader.Task.Type.ANIMATION;

public class SplashView extends RelativeLayout {
    public interface EventListener {
        void onShow();
    }
    ImageView imageView;
    Button mJumpButton;
    NativeAd mNativeAdBase;

    AdListener mAdListener;

    String mTextFormat = "跳过(%ds)";

    int mJumpSecond;

    private CountDownTimer countDownTimer;
    private BaseEngineControl mControl;
    private ResDownloader mDownloader;
    private TemplateModel mTemplateModel;

    private View mEngineView;
    private int mEngineViewWidth, mEngineViewHeight;
    private float mScale, mTransX, mTransY;
    private boolean mIsParsed = false;
    private boolean mIsAttach = false;
    private boolean mIsResume = false;
    private boolean mIsNotified = false;
    private EventListener mEventListener;

    private HashMap<String, View> mSubView = new HashMap<>();

    private void createCountDownTimer(int second) {
        countDownTimer = new CountDownTimer((second * 1000) + 400, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mJumpButton.setText(String.format(mTextFormat, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                mJumpButton.setText(String.format(mTextFormat, 0));
                try {
                    mAdListener.onAdJump();
                } finally {
                    mAdListener.onAdClose();
                }
            }
        };
    }

    private OnClickListener mJumpLister = new OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            try {
                mAdListener.onAdJump();
            } finally {
                mAdListener.onAdClose();
            }
        }
    };

    private OnClickListener mClickAdLister = new OnClickListener() {
        @Override
        public void onClick(android.view.View v) {
            try {
                mAdListener.onAdJump();
                onClickAction();
            } finally {
                mAdListener.onAdClose();
            }
        }
    };

    public void setTextFormat(String mTextFormat) {
        this.mTextFormat = mTextFormat;
    }

    public void setAdListener(AdListener adListener) {
        this.mAdListener = adListener;
    }

    public SplashView(Context context) {
        super(context);
        init();
    }

    public SplashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("NewApi")
    private void init() {
        setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        mControl = new BaseEngineControl(getContext());

        // add ImageView
        imageView = ZImageView.create(getContext(), FIT_XY, MATCH_PARENT, MATCH_PARENT);
        addView(imageView, generateDefaultLayoutParams());
        RelativeLayout.LayoutParams lp = (LayoutParams) imageView.getLayoutParams();
        lp.addRule(CENTER_IN_PARENT);

        // add jump button
        mJumpButton = new Button(getContext());
        mJumpButton.setMinHeight(DensityUtil.dp(getContext(), 30));
        mJumpButton.setGravity(Gravity.CENTER);
        mJumpButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mJumpButton.setTextColor(Color.parseColor("#ffffff"));
        mJumpButton.setVisibility(INVISIBLE);
        addView(mJumpButton, generateDefaultLayoutParams());
        RelativeLayout.LayoutParams btnLp = (LayoutParams) mJumpButton.getLayoutParams();
        btnLp.width = DensityUtil.dip2px(getContext(), 60);
        btnLp.height = DensityUtil.dip2px(getContext(), 35);
        btnLp.topMargin = DensityUtil.dp(getContext(), 20);
        btnLp.rightMargin = DensityUtil.dp(getContext(), 20);
        btnLp.addRule(ALIGN_PARENT_TOP);
        btnLp.addRule(ALIGN_PARENT_RIGHT);

        mJumpButton.setOnClickListener(mJumpLister);

        setOnClickListener(mClickAdLister);

    }


    /**
     * 几秒后跳转
     */
    public void start(int second) {
        mJumpSecond = second;
        if(mJumpSecond > 0) {
            createCountDownTimer(second);
        }

        final SplashLoader adzk = new SplashLoader(getContext(), Cfg.PLACEMENT_SPLASH);
        adzk.setAdListener(new AdListenerBase() {

            @Override
            public void onError(NativeAd adBase, AdErrorBase adErrorBase) {
                mAdListener.onAdFailed();
            }

            @Override
            public void onAdLoaded(NativeAd adBase) {
                mNativeAdBase = adBase;
                if (!TextUtils.isEmpty(adBase.getTemplateUrl())) {
                    download(adBase);
                } else if (mAdListener.onAdLoading(adBase)){
                    onShow(false);
                } else {
                    loadFullImage(adBase);
                }

                adzk.destroy();
            }
        });
        adzk.loadAd();

    }

    private void loadFullImage(NativeAd adBase) {
        AdsSDK.getImage(adBase.getUrl().toString(), new ResDownloader.ResCallback() {
            @Override
            public void onFinish(ResDownloader downloader, ResDownloader.Task task) {
                if (task.success) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(task.data);
                        getImageView().setImageBitmap(bitmap);
                        onShow(false);
                    } catch (Throwable e) {
                        onAdError();
                        e.printStackTrace();
                    }
                } else {
                    onAdError();
                }

            }
        });
    }

    private void onShow(boolean isTemplate) {
        if(!isTemplate) {
            imageView.setVisibility(VISIBLE);
        }

        if (mJumpSecond > 0) {
            mJumpButton.setVisibility(VISIBLE);
        }
        mJumpButton.setBackgroundDrawable(DrawableKit.createRectangle(getContext(), Color.parseColor("#55c4c4c4")));

        mNativeAdBase.adShowing(true);
        mAdListener.onAdPresent();
        startCountDownTimer();
    }

    private void onAdError() {
        mAdListener.onAdFailed();
    }

    public void onStart(){
        mControl.onResume();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        try {
            if (mEngineViewWidth != 0 && mEngineViewHeight != 0) {
                mEngineView.measure(MeasureSpec.makeMeasureSpec(mEngineViewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mEngineViewHeight, MeasureSpec.EXACTLY));

                float wRatio = (float)width / mEngineViewWidth;
                float hRatio = (float)height / mEngineViewHeight;
                if (wRatio < hRatio) {
                    mScale = hRatio;
                    mTransX = -(mEngineViewWidth * mScale - width) / 2f;
                    mTransY = 0;
                } else {
                    mScale = wRatio;
                    mTransX = 0;
                    mTransY = -(mEngineViewHeight * mScale - height) / 2f;
                }
            }
        } catch (Throwable e) {
            // NOOP
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mEngineViewWidth != 0 && mEngineViewHeight != 0) {
            mEngineView.layout(0, 0, mEngineViewWidth, mEngineViewHeight);

            mEngineView.setScaleX(mScale);
            mEngineView.setScaleY(mScale);
            mEngineView.setTranslationX(mTransX);
            mEngineView.setTranslationY(mTransY);
        }
    }

    public void addSubView(String key, View subView) {
        mSubView.put(key, subView);
        tryAddEngineSubView(key, subView);
    }
    private void refreshSubView() {
        try {
            for(HashMap.Entry<String, View> entry : mSubView.entrySet()) {
                tryAddEngineSubView(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
        }
    }
    private void tryAddEngineSubView(String name, View view) {
        if (view == null || view.getParent() != null) {
            return;
        }
        if (mIsParsed && mControl != null && mEngineView != null) {
            FrameLayout frame = (FrameLayout)mControl.getEngine().getViewByName(name);
            if (frame != null) {
                frame.addView(view);
            }
        }
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void onResume() {

    }

    private void startCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.start();
        }
    }

    public void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        mControl.onPause();
    }

    public void onDestroy() {
        if(mNativeAdBase != null) {
            mNativeAdBase.destroy();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (mDownloader != null) {
            mDownloader.destroy();
        }
        try {
            mControl.onDestroy();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void onClickAction() {
        if(mNativeAdBase != null) {
            mNativeAdBase.onClickAction();
        }
    }



    void download(NativeAd adBase) {
        ResDownloader.Task task = new ResDownloader.Task(adBase.getTemplateUrl(), ANIMATION);
        ResDownloader.Task taskIcon = new ResDownloader.Task(adBase.getAdIcon().getUrl(), ResDownloader.Task.Type.IMAGE);
        mTemplateModel = new TemplateModel(adBase, task, taskIcon);
        mDownloader = new ResDownloader();

        mDownloader.setResCallback(new ResDownloader.ResCallback() {
            @Override
            public void onFinish(ResDownloader downloader, ResDownloader.Task task) {
                Log.d("Download", " task.success: " + task.success + " path: " + task.data);
                mTemplateModel.onFinish(task);
                if (task.success) {
                    refreshAdData();
                } else if(task.type == ANIMATION){
                    if (mAdListener.onAdLoading(mNativeAdBase)){
                        onShow(false);
                    } else {
                        loadFullImage(mNativeAdBase);
                    }
                }
                if (mTemplateModel.isTaskComplete()) {
                    ResDownloader.safeClose(downloader);
                }
            }
        });
        mDownloader.execute(task, taskIcon);

    }



    private void refreshAdData() {
        TemplateModel mModel = mTemplateModel;
        if (mModel == null) {
            return;
        }
        if (mEngineView == null && !TextUtils.isEmpty(mModel.getTemplatePath())) {
//            String xmlPath = mModel.getTemplatePath();
//            Log.d("xmlPath", "refreshAdData: " + xmlPath);
            String xmlPath = "/data/user/0/com.zn.simplesdk/app_anim_res/-1383328988";
            mEngineView =  mControl.getEngine().loadViewAsync(xmlPath, new IParseCallBack() {
                @Override
                public void onParseEnd() {
                    if (mControl == null || mControl.getEngine() == null) {
                        return;
                    }
                    mEngineViewWidth = (int)mControl.getEngine().getScaleVariableValue("ad_width");
                    mEngineViewHeight = (int)mControl.getEngine().getScaleVariableValue("ad_height");
                    Log.d("Download", "onParseEnd w=" + mEngineViewWidth
                            + "h=" + mEngineViewHeight);
                    mEngineView.setPivotX(0f);
                    mEngineView.setPivotY(0f);
                    mIsParsed = true;
                    requestLayout();
                    refreshAdData();
                    onShow(true);
                }
            });
            addView(mEngineView, 0);
            RelativeLayout.LayoutParams lp = (LayoutParams) mEngineView.getLayoutParams();
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;
            lp.addRule(CENTER_IN_PARENT);
        }
        if (mEngineView != null && mIsParsed) {
            tryUpdateEngineImage("ad_logo", mModel.getIconPath());
            refreshSubView();
        }

        if (mIsAttach && !mIsResume) {
            mControl.onResume();
            mIsResume = true;
        }
        if (mEngineView != null && !mIsNotified && mEventListener != null) {
            mIsNotified = true;
            mEventListener.onShow();
        }
    }

    private void tryUpdateEngineImage(String name, String path) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(path)) {
            return;
        }
        if (mIsParsed && mEngineView != null) {
            View image = mControl.getEngine().getViewByName(name);
            if (image != null) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    if (bitmap != null) {
                        mControl.getEngine().setBitmap(image, path, bitmap);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
