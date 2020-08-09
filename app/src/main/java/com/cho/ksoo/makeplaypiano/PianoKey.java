package com.cho.ksoo.makeplaypiano;

import android.graphics.RectF;

public class PianoKey {

    private int sound;
    private RectF rect;
    private boolean down;

    public PianoKey(RectF rect, int sound) {
        this.sound = sound;
        this.rect = rect;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public RectF getRect() {
        return rect;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

}
