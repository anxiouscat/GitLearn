package com.oversea.ads.base;

/**
 * Created by a_zcg_000 on 2016/7/31.
 */
public class NativeAdViewType implements NativeAdViewTypeBase{
    private int mWidth;
    private int mHeight;
    public NativeAdViewType() {
        this.set(-1,-1);
    }
    protected NativeAdViewType(int width, int height) {
        this.set(width,height);
    }
    public void set(int width,int height) {
        this.mWidth = width;
        this.mHeight = height;
    }
    public NativeAdViewType HEIGHT_100() {
        this.set(-1,100);
        return this;
    }
    public NativeAdViewType HEIGHT_120() {
        this.set(-1,120);
        return this;
    }
    public NativeAdViewType HEIGHT_300() {
        this.set(-1,300);
        return this;
    }
    public NativeAdViewType HEIGHT_400() {
        this.set(-1,400);
        return this;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getValue() {
        switch(this.mHeight) {
            case 100:
                return 1;
            case 120:
                return 2;
            case 300:
                return 3;
            case 400:
                return 4;
            default:
                return -1;
        }
    }
    @Override
    public Object getObject() {
        return this;
    }
}
