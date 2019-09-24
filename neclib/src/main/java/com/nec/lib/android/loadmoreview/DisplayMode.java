package com.nec.lib.android.loadmoreview;

/**
 * 列表显示模式：行、瀑布
 */
public enum DisplayMode {
    LINEAR(1), STAGGERED(2);
    public int value;
    DisplayMode(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public static DisplayMode fromValue(int value) {
        switch (value) {
            case 1:
                return LINEAR;
            case 2:
                return STAGGERED;
            default:
                return null;
        }
    }
}
