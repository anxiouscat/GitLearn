package com.zn.simplesdk.motion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xad.engine.interfaces.IParseCallBack;
import com.xad.engine.interfaces.impl.BaseEngineControl;

import java.util.HashMap;

/**
 *  模版类广告展示组件
 */
public class TemplateView extends ViewGroup  {
    private static final String TAG = "TemplateView";
    private BaseEngineControl mControl;
    private View mEngineView;
    private int mEngineViewWidth, mEngineViewHeight;
    private float mScale, mTransX, mTransY;
    private boolean mIsParsed = false;
    private boolean mIsAttach = false;
    private boolean mIsResume = false;
    private boolean mIsNotified = false;
    private EventListener mEventListener;
    private TemplateModel mModel;

    private final Bitmap [] mBitmapCache = new Bitmap[TemplateModel.RES_MAX];
    private HashMap<String, View> mSubView = new HashMap<>();

    public TemplateView(Context context) {
        super(context);
        mControl = new BaseEngineControl(context);
    }

    public void setEventListener(EventListener listener) {
        mEventListener = listener;
    }

    public void load(TemplateModel model) {
        if (model == null) {
            return;
        }
        mModel = model;
        refreshAdData();
    }

    public void onStart(){
        mControl.onResume();
    }

    public void onPause() {
        mControl.onPause();
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

    private void tryUpdateEngineImage(String name, String path) {

        if (TextUtils.isEmpty(name) || mModel == null) {
            return;
        }

        if (mIsParsed && mEngineView != null) {
            View image = mControl.getEngine().getViewByName(name);
            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    //mBitmapCache[index] = bitmap;
                    mControl.getEngine().setBitmap(image, path, bitmap);
                }
            }
        }
    }

    private void tryUpdateEngineImage(String name, int index) {

        if (TextUtils.isEmpty(name) || mModel == null) {
            return;
        }
        String path = mModel.getTemplatePath();
        if (TextUtils.isEmpty(path)) {
            return;
        }

        if (mIsParsed && mEngineView != null) {
            View image = mControl.getEngine().getViewByName(name);
            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    mBitmapCache[index] = bitmap;
                    mControl.getEngine().setBitmap(image, path, bitmap);
                }
            }
        }
    }
    private void tryUpdateEngineString(String name, String value) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(value)){
            return;
        }
        if (mIsParsed && mEngineView != null) {
            View view = mControl.getEngine().getViewByName(name);
            if (view != null ) {
                mControl.getEngine().onStringExpressionChange(view, value);
            }
        }
    }

    private void refreshAdData() {
        if (mModel == null) {
            return;
        }
        if (mEngineView == null && !TextUtils.isEmpty(mModel.getTemplatePath())) {
            String xmlPath = mModel.getTemplatePath();
            mEngineView =  mControl.getEngine().loadViewAsync(xmlPath, new IParseCallBack() {
                @Override
                public void onParseEnd() {
                    if (mControl == null || mControl.getEngine() == null) {
                        return;
                    }
                    mEngineViewWidth = (int)mControl.getEngine().getScaleVariableValue("ad_width");
                    mEngineViewHeight = (int)mControl.getEngine().getScaleVariableValue("ad_height");
                    Log.d(TAG, "onParseEnd w=" + mEngineViewWidth
                            + "h=" + mEngineViewHeight);
                    mEngineView.setPivotX(0f);
                    mEngineView.setPivotY(0f);
                    mIsParsed = true;
                    requestLayout();
                    refreshAdData();
                }
            });
            addView(mEngineView);
        }
        if (mEngineView != null && mIsParsed) {
            tryUpdateEngineImage("ad_logo", mModel.getIconPath());
            //tryUpdateEngineImage("ad_logo", TemplateModel.RES_LOGO);
            //tryUpdateEngineImage("ad_image", TemplateModel.RES_IMAGE);
            //tryUpdateEngineString("ad_title", mAdInfo.mAdTitle);
            //tryUpdateEngineString("ad_description", mAdInfo.mAdBody);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
        try {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = MeasureSpec.getSize(heightMeasureSpec);
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
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mEngineViewWidth != 0 && mEngineViewHeight != 0) {
            mEngineView.layout(0, 0, mEngineViewWidth, mEngineViewHeight);

            mEngineView.setScaleX(mScale);
            mEngineView.setScaleY(mScale);
            mEngineView.setTranslationX(mTransX);
            mEngineView.setTranslationY(mTransY);
        }
    }

    private void reset() {
        try {
            try {
                mControl.onDestroy();
            } catch (Throwable e) {
                // NOOP
            }
            try {
                mControl.onDestroy();
            } catch (Exception e) {
                // NOOP
            }
            mControl = null;
            mIsResume = false;
            mIsAttach = false;
            mIsParsed = false;
            mIsNotified = false;
            mEngineViewHeight = 0;
            mEngineViewWidth = 0;

            for (int i = 0; i < mBitmapCache.length; ++i) {
                //mBitmapCache[i] = ResDownloader.Util.safeReleaseBitmap(mBitmapCache[i]);
            }
        } catch (Throwable e) {
            // NOOP
        }
    }

    public void release() {
        reset();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //release(); // 由于会attached=>detached=>attached, 这里屏蔽，保证外部会调用release
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttach = true;
        refreshAdData();
    }

    public interface EventListener {
        void onShow();
    }
}
