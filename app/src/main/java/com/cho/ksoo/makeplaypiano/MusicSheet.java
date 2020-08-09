package com.cho.ksoo.makeplaypiano;

import android.text.TextUtils;

public class MusicSheet {

    //musicTable[0] =  new int[]{1000, 0, 0, 0, 0, 0, 1000, 1, 19, 0, 0, 0};

    private int lowNoteLength;
    private int lowPlayFlag;
    private String lowNote1;
    private String lowNote2;
    private String lowNote3;
    private String lowNote4;
    private int lowNoteNo1;
    private int lowNoteNo2;
    private int lowNoteNo3;
    private int lowNoteNo4;
    private String lowNoteWav1;
    private String lowNoteWav2;
    private String lowNoteWav3;
    private String lowNoteWav4;
    private String lowNoteTie;

    private int highNoteLength;
    private int highPlayFlag;
    private String highNote1;
    private String highNote2;
    private String highNote3;
    private String highNote4;
    private int highNoteNo1;
    private int highNoteNo2;
    private int highNoteNo3;
    private int highNoteNo4;
    private String highNoteWav1;
    private String highNoteWav2;
    private String highNoteWav3;
    private String highNoteWav4;
    private String highNoteTie;

    public MusicSheet(  int lowNoteLength,
                        int lowPlayFlag,
                        String lowNote1,
                        String lowNote2,
                        String lowNote3,
                        String lowNote4,
                        String lowNoteTie,
                        int highNoteLength,
                        int highPlayFlag,
                        String highNote1,
                        String highNote2,
                        String highNote3,
                        String highNote4,
                        String highNoteTie
                   ) {

        // ------------------------------------
        // 낮은 음자리표
        // ------------------------------------
        this.lowNoteNo1 = 0;
        this.lowNoteNo2 = 0;
        this.lowNoteNo3 = 0;
        this.lowNoteNo4 = 0;

        this.lowNoteWav1 = "";
        this.lowNoteWav2 = "";
        this.lowNoteWav3 = "";
        this.lowNoteWav4 = "";

        // PlayFlag - 1 음표, 0 쉼표
        if ( lowPlayFlag == 1) {

            if (!TextUtils.isEmpty(lowNote1)) {
                this.lowNoteNo1 = SoundMap.getSoundNo(lowNote1);
                this.lowNoteWav1 = lowNote1+".wav";
            }

            if (!TextUtils.isEmpty(lowNote2)) {
                this.lowNoteNo2 = SoundMap.getSoundNo(lowNote2);
                this.lowNoteWav2 = lowNote2+".wav";
            }

            if (!TextUtils.isEmpty(lowNote3)) {
                this.lowNoteNo3 = SoundMap.getSoundNo(lowNote3);
                this.lowNoteWav3 = lowNote3+".wav";
            }

            if (!TextUtils.isEmpty(lowNote4)) {
                this.lowNoteNo4 = SoundMap.getSoundNo(lowNote4);
                this.lowNoteWav4 = lowNote4+".wav";
            }

       }

        this.lowNoteLength = lowNoteLength;
        this.lowPlayFlag = lowPlayFlag;
        this.lowNote1 = lowNote1;
        this.lowNote2 = lowNote2;
        this.lowNote3 = lowNote3;
        this.lowNote4 = lowNote4;
        this.lowNoteTie = lowNoteTie;

        // --------------------------------------------------------------------------------------
        // 높은 음자리표
        // --------------------------------------------------------------------------------------
        this.highNoteNo1 = 0;
        this.highNoteNo2 = 0;
        this.highNoteNo3 = 0;
        this.highNoteNo4 = 0;

        this.highNoteWav1 = "";
        this.highNoteWav2 = "";
        this.highNoteWav3 = "";
        this.highNoteWav4 = "";

        // PlayFlag - 1 음표, 0 쉼표
        if ( highPlayFlag == 1) {

            if (!TextUtils.isEmpty(highNote1)) {
                this.highNoteNo1 = SoundMap.getSoundNo(highNote1);
                this.highNoteWav1 = highNote1+".wav";
            }

            if (!TextUtils.isEmpty(highNote2)) {
                this.highNoteNo2 = SoundMap.getSoundNo(highNote2);
                this.highNoteWav2 = highNote2+".wav";
            }

            if (!TextUtils.isEmpty(highNote3)) {
                this.highNoteNo3 = SoundMap.getSoundNo(highNote3);
                this.highNoteWav3 = highNote3+".wav";
            }

            if (!TextUtils.isEmpty(highNote4)) {
                this.highNoteNo4 = SoundMap.getSoundNo(highNote4);
                this.highNoteWav4 = highNote4+".wav";
            }

        }

        this.highNoteLength = highNoteLength;
        this.highPlayFlag = highPlayFlag;
        this.highNote1 = highNote1;
        this.highNote2 = highNote2;
        this.highNote3 = highNote3;
        this.highNote4 = highNote4;
        this.highNoteTie = highNoteTie;

    }

    public int getLowNoteLength() {
        return lowNoteLength;
    }
    public int getLowPlayFlag() {
        return lowPlayFlag;
    }
    public String getLowNote1() {
        return lowNote1;
    }
    public String getLowNote2() {
        return lowNote2;
    }
    public String getLowNote3() {
        return lowNote3;
    }
    public String getLowNote4() {
        return lowNote4;
    }

    public int getHighNoteLength() {
        return highNoteLength;
    }
    public int getHighPlayFlag() {
        return highPlayFlag;
    }
    public String getHighNote1() {
        return highNote1;
    }
    public String getHighNote2() {
        return highNote2;
    }
    public String getHighNote3() {
        return highNote3;
    }
    public String getHighNote4() {
        return highNote4;
    }

    public int getLowNoteNo1() {
        return lowNoteNo1;
    }
    public int getLowNoteNo2() {
        return lowNoteNo2;
    }
    public int getLowNoteNo3() {
        return lowNoteNo3;
    }
    public int getLowNoteNo4() { return lowNoteNo4; }

    public int getHighNoteNo1() { return highNoteNo1; }
    public int getHighNoteNo2() { return highNoteNo2; }
    public int getHighNoteNo3() { return highNoteNo3; }
    public int getHighNoteNo4() { return highNoteNo4; }

    public String getLowNoteTie() { return lowNoteTie; }
    public String getHighNoteTie() { return highNoteTie; }

    public void setLowNoteTie(String lowNoteTie) {
        this.lowNoteTie = lowNoteTie;
    }

    public void setHighNoteTie(String highNoteTie) {
        this.highNoteTie = highNoteTie;
    }

    // ---------------------------------
    // Getter - Wav File
    // ---------------------------------
    public String getLowNoteWav1() { return lowNoteWav1; }
    public String getLowNoteWav2() { return lowNoteWav2; }
    public String getLowNoteWav3() { return lowNoteWav3; }
    public String getLowNoteWav4() { return lowNoteWav4; }

    public String getHighNoteWav1() { return highNoteWav1; }
    public String getHighNoteWav2() { return highNoteWav2; }
    public String getHighNoteWav3() { return highNoteWav3; }
    public String getHighNoteWav4() { return highNoteWav4; }

}
