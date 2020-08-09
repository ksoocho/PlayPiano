package com.cho.ksoo.makeplaypiano;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;

public class PlayMusicSheet {

    private AudioSoundPlayer soundPlayer;
    private int codeLine;

    Context mContext;
    SharedPreferences mPrefs;

    int lowNoteLength;
    int lowPlayFlag;
    int lowNote1;
    int lowNote2;
    int lowNote3;
    int lowNote4;
    String lowNoteTie;

    int curr_lowKeyNote1;
    int curr_lowKeyNote2;
    int curr_lowKeyNote3;
    int curr_lowKeyNote4;
    int prev_lowKeyNote1;
    int prev_lowKeyNote2;
    int prev_lowKeyNote3;
    int prev_lowKeyNote4;

    int highNoteLength;
    int highPlayFlag;
    int highNote1;
    int highNote2;
    int highNote3;
    int highNote4;
    String highNoteTie;

    int curr_highKeyNote1;
    int curr_highKeyNote2;
    int curr_highKeyNote3;
    int curr_highKeyNote4;

    int prev_highKeyNote1;
    int prev_highKeyNote2;
    int prev_highKeyNote3;
    int prev_highKeyNote4;

    int curDelayTime;

    int inx_high;
    int inx_low;


    int vArrayCount;

    // Constructor
    public PlayMusicSheet ( Context context, int codeLine) {

        this.codeLine = codeLine;
        this.mContext = context;

        soundPlayer = new AudioSoundPlayer(context);

        curDelayTime = 1000;

        inx_high = 0;
        inx_low = 0;

        highNote1  = 1;
        highNote2  = 1;
        highNote3  = 1;
        highNote4  = 1;
        highNoteTie = "";

        curr_highKeyNote1  = 1;
        curr_highKeyNote2  = 1;
        curr_highKeyNote3  = 1;
        curr_highKeyNote4  = 1;
        prev_highKeyNote1  = 1;
        prev_highKeyNote2  = 1;
        prev_highKeyNote3  = 1;
        prev_highKeyNote4  = 1;

        lowNote1  = 1;
        lowNote2  = 1;
        lowNote3  = 1;
        lowNote4  = 1;
        lowNoteTie = "";

        curr_lowKeyNote1  = 1;
        curr_lowKeyNote2  = 1;
        curr_lowKeyNote3  = 1;
        curr_lowKeyNote4  = 1;
        prev_lowKeyNote1  = 1;
        prev_lowKeyNote2  = 1;
        prev_lowKeyNote3  = 1;
        prev_lowKeyNote4  = 1;

        mPrefs = mContext.getSharedPreferences("playMusic", mContext.MODE_PRIVATE);
        vArrayCount = ((PianoApp)mContext.getApplicationContext()).musicArrayCount;

    }

    public void clearMusic () {

        for(int inx = 0; inx < vArrayCount; inx++) {
            ((PianoApp)mContext.getApplicationContext()).musicTable[inx] =  new MusicSheet(0, 0,   "", "", "", "", "", 0, 0, "", "", "", "","");
        }

    }

    public void saveMusicFile () {

        MusicSheetData[] musicSheetData = new MusicSheetData[vArrayCount];

        for(int inx = 0; inx < vArrayCount; inx++) {

            musicSheetData[inx] = new MusicSheetData();

            musicSheetData[inx].highNoteLength = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getHighNoteLength();
            musicSheetData[inx].highPlayFlag = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getHighPlayFlag();
            musicSheetData[inx].highNote1 = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getHighNote1();
            musicSheetData[inx].highNote2 = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getHighNote2();
            musicSheetData[inx].highNote3 = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getHighNote3();
            musicSheetData[inx].highNote4 = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getHighNote4();
            musicSheetData[inx].highNoteTie = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getHighNoteTie();

            musicSheetData[inx].lowNoteLength = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getLowNoteLength();
            musicSheetData[inx].lowPlayFlag = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getLowPlayFlag();
            musicSheetData[inx].lowNote1 = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getLowNote1();
            musicSheetData[inx].lowNote2 = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getLowNote2();
            musicSheetData[inx].lowNote3 = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getLowNote3();
            musicSheetData[inx].lowNote4 = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getLowNote4();
            musicSheetData[inx].lowNoteTie = ((PianoApp)mContext.getApplicationContext()).musicTable[inx].getLowNoteTie();

        }

        SharedPreferences.Editor ed = mPrefs.edit();
        Gson gson = new Gson();
        ed.putString("myMusicSheet", gson.toJson(musicSheetData));
        ed.commit();

    }

    public void loadMusicFile () {

        try {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();

            JsonArray arr = parser.parse(mPrefs.getString("myMusicSheet", null)).getAsJsonArray();

            MusicSheetData[] musicSheetData = new MusicSheetData[vArrayCount];

            int i = 0;

            for (JsonElement jsonElement : arr) {
                int inx = i;
                musicSheetData[inx] = new MusicSheetData();
                musicSheetData[inx] = gson.fromJson(jsonElement, MusicSheetData.class);
                i++;
            }

            for (int inx = 0; inx < vArrayCount; inx++) {

                int highNoteLength = musicSheetData[inx].highNoteLength;
                int highPlayFlag = musicSheetData[inx].highPlayFlag;
                String highNote1 = musicSheetData[inx].highNote1;
                String highNote2 = musicSheetData[inx].highNote2;
                String highNote3 = musicSheetData[inx].highNote3;
                String highNote4 = musicSheetData[inx].highNote4;
                String highNoteTie = musicSheetData[inx].highNoteTie;

                int lowNoteLength = musicSheetData[inx].lowNoteLength;
                int lowPlayFlag = musicSheetData[inx].lowPlayFlag;
                String lowNote1 = musicSheetData[inx].lowNote1;
                String lowNote2 = musicSheetData[inx].lowNote2;
                String lowNote3 = musicSheetData[inx].lowNote3;
                String lowNote4 = musicSheetData[inx].lowNote4;
                String lowNoteTie = musicSheetData[inx].lowNoteTie;

                ((PianoApp) mContext.getApplicationContext()).musicTable[inx] = new MusicSheet(
                        lowNoteLength, lowPlayFlag, lowNote1, lowNote2, lowNote3, lowNote4, lowNoteTie,
                        highNoteLength, highPlayFlag, highNote1, highNote2, highNote3, highNote4, highNoteTie);
            }

        } catch (Exception e) {
            clearMusic();
        }

    }

    public void loadMusic () {

        String vPlayKeySign = ((PianoApp)mContext.getApplicationContext()).playKeySign;

        ((PianoApp)mContext.getApplicationContext()).musicTable[0] =  new MusicSheet(1000, 0,   "", "", "", "", "",1000, 1, "G5", "", "", "","");

        ((PianoApp)mContext.getApplicationContext()).musicTable[1] =  new MusicSheet( 0, 2,   "", "", "", "", "", 0, 2, "", "", "", "","");

        ((PianoApp)mContext.getApplicationContext()).musicTable[2] =  new MusicSheet(2000, 1, "C4", "", "", "", "", 2000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[3] =  new MusicSheet(2000, 1, "G4", "", "", "", "", 1000, 1, "B5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[4] =  new MusicSheet(  0, 0,   "", "", "", "", "", 1000, 1, "C6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[5] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[6] =  new MusicSheet(2000, 1, "B3", "", "", "", "", 2000, 1, "D6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[7] =  new MusicSheet(2000, 1, "G4", "", "", "", "", 1000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[8] =  new MusicSheet(  0, 0,   "", "", "", "", "", 1000, 1, "D6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[9] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[10] =  new MusicSheet(2000, 1, "A3", "", "", "", "", 2000, 1, "E6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[11] =  new MusicSheet(2000, 1, "E4", "", "", "", "", 1000, 1, "F6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[12] =  new MusicSheet(  0, 0,   "", "", "", "", "", 1000, 1, "E6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[13] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[14] =  new MusicSheet(4000, 1, "C4", "F4", "", "", "", 2000, 1, "A5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[15] =  new MusicSheet(  0, 0,   "",   "", "", "", "", 1000, 1, "D6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[16] =  new MusicSheet( 0, 0,   "",   "", "", "", "", 1000, 1, "D6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[17] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[18] =  new MusicSheet(2000, 1, "C4", "", "", "", "", 2000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[19] =  new MusicSheet(2000, 1, "G4", "", "", "", "", 1000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[20] =  new MusicSheet(  0, 0,   "", "", "", "", "", 1000, 1, "C6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[21] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[22] =  new MusicSheet(2000, 1, "B3", "", "", "", "", 2000, 1, "B5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[23] =  new MusicSheet(2000, 1, "G4", "", "", "", "", 1000, 1, "A5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[24] =  new MusicSheet(  0, 0,   "", "", "", "", "", 1000, 1, "B5", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[25] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[26] =  new MusicSheet(2000, 1, "C4", "", "", "", "", 4000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[27] =  new MusicSheet(2000, 1, "G4", "", "", "", "",    0, 0,   "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[28] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[29] =  new MusicSheet(2000, 1, "C4", "", "", "", "", 2000, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[30] =  new MusicSheet(1000, 0,   "", "", "", "", "", 1000, 1, "G5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[31] =  new MusicSheet(1000, 0,   "", "", "", "", "", 1000, 1, "G5", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[32] =  new MusicSheet(   0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[33] =  new MusicSheet(2000, 1, "C4", "", "", "", "", 2000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[34] =  new MusicSheet(2000, 1, "G4", "", "", "", "", 1000, 1, "B5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[35] =  new MusicSheet( 0, 0,   "", "", "", "", "", 1000, 1, "C6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[36] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[37] =  new MusicSheet(2000, 1, "B3", "", "", "", "", 2000, 1, "D6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[38] =  new MusicSheet(2000, 1, "G4", "", "", "", "", 1000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[39] =  new MusicSheet(0, 0,   "", "", "", "", "", 1000, 1, "D6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[40] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[41] =  new MusicSheet(2000, 1, "A3", "", "", "", "", 2000, 1, "E6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[42] =  new MusicSheet(2000, 1, "E4", "", "", "", "", 1000, 1, "F6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[43] =  new MusicSheet(  0, 0,   "", "", "", "", "", 1000, 1, "E6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[44] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[45] =  new MusicSheet(4000, 1, "C4", "F4", "", "", "", 2000, 1, "A5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[46] =  new MusicSheet(  0, 0, "E4",   "", "", "", "", 1000, 1, "D6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[47] =  new MusicSheet( 0, 0,   "",   "", "", "", "", 1000, 1, "D6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[48] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[49] =  new MusicSheet(2000, 1, "C4", "", "", "", "", 2000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[50] =  new MusicSheet(2000, 1, "G4", "", "", "", "", 1000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[51] =  new MusicSheet(  0, 0,   "", "", "", "", "", 1000, 1, "C6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[52] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[53] =  new MusicSheet(2000, 1, "B3", "", "", "", "", 2000, 1, "B5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[54] =  new MusicSheet(2000, 1, "G4", "", "", "", "", 1000, 1, "A5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[55] =  new MusicSheet(  0, 0,   "", "", "", "", "", 1000, 1, "B5", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[56] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[57] =  new MusicSheet(2000, 1, "C4", "", "", "", "", 4000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[58] =  new MusicSheet(2000, 1, "G4", "", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[59] =  new MusicSheet(  0, 0,   "", "", "", "", "",    0, 0, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[60] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[61] =  new MusicSheet(2000, 1, "C4", "", "", "", "", 2000, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[62] =  new MusicSheet(1000, 0,   "", "", "", "", "", 1000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[63] =  new MusicSheet(1000, 0,   "", "", "", "", "", 1000, 1, "E6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[64] =  new MusicSheet( 0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[65] =  new MusicSheet(1000, 1, "C4",   "", "", "", "", 2000, 1, "G6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[66] =  new MusicSheet(1000, 1, "E4", "G4", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[67] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 1000, 1, "E6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[68] =  new MusicSheet(1000, 1, "E4", "G4", "", "", "", 1000, 1, "D6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[69] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[70] =  new MusicSheet(1000, 1, "C4",   "", "", "", "", 2000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[71] =  new MusicSheet(1000, 1, "E4", "A4", "", "", "",    0, 0,   "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[72] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 1000, 1, "B5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[73] =  new MusicSheet(1000, 1, "E4", "A4", "", "", "", 1000, 1, "C6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[74] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[75] =  new MusicSheet(1000, 1, "D4",   "", "", "", "", 1000, 1, "D6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[76] =  new MusicSheet(1000, 1, "Gb4", "A4", "", "", "", 1000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[77] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 1000, 1, "B5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[78] =  new MusicSheet(1000, 1, "Gb4", "A4", "", "", "", 1000, 1, "A5", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[79] =  new MusicSheet(   0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[80] =  new MusicSheet(1000, 1, "B3",   "", "", "", "", 2000, 1, "G5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[81] =  new MusicSheet(1000, 1, "D4", "G4", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[82] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 1000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[83] =  new MusicSheet(1000, 1, "D4", "G4", "", "", "", 1000, 1, "E6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[84] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[85] =  new MusicSheet(1000, 1, "C4",   "", "", "", "", 2000, 1, "G6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[86] =  new MusicSheet(1000, 1, "E4", "G4", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[87] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 1000, 1, "E6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[88] =  new MusicSheet(1000, 1, "E4", "G4", "", "", "", 1000, 1, "D6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[89] =  new MusicSheet(   0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[90] =  new MusicSheet(1000, 1, "C4",   "", "", "", "", 2000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[91] =  new MusicSheet(1000, 1, "E4", "A4", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[92] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 1000, 1, "B5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[93] =  new MusicSheet(1000, 1, "E4", "A4", "", "", "", 1000, 1, "C6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[94] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[95] =  new MusicSheet(1000, 1, "D4",   "", "", "", "", 4000, 1, "D6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[96] =  new MusicSheet(1000, 1, "Gb4", "A4", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[97] =  new MusicSheet(1000, 0,   "",   "", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[98] =  new MusicSheet(1000, 1, "Gb4", "A4", "", "", "",    0, 0, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[99] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[100] =  new MusicSheet(1000, 1, "B3", "", "", "", "",   2000, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[101] =  new MusicSheet(1000, 1, "D4", "G4", "", "", "", 0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[102] =  new MusicSheet(1000, 1, "D4", "G4", "", "", "", 1000, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[103] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 1000, 1, "G5", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[104] =  new MusicSheet(   0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[105] =  new MusicSheet(1000, 1, "C4", "G4", "", "", "", 1000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[106] =  new MusicSheet(1000, 1, "C4", "G4", "", "", "", 1000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[107] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 2000, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[108] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 0, 0, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[109] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[110] =  new MusicSheet(4000, 1, "D4", "G4", "", "", "",  2000, 1, "D6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[111] =  new MusicSheet(  0, 0,   "",   "", "", "", "",  2000, 0, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[112] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[113] =  new MusicSheet(1000, 1, "C4",   "", "", "", "", 1000, 1, "E6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[114] =  new MusicSheet(1000, 1, "E4", "Bb4", "", "", "", 1000, 1, "E6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[115] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 1000, 1, "F6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[116] =  new MusicSheet(1000, 1, "E4", "Bb4", "", "", "", 1000, 1, "E6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[117] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[118] =  new MusicSheet(1000, 1, "C4",   "", "", "", "", 2000, 1, "A5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[119] =  new MusicSheet(1000, 1, "F4", "A4", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[120] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 1000, 1, "D6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[121] =  new MusicSheet(1000, 1, "F4", "A4", "", "", "", 1000, 1, "D6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[122] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[123] =  new MusicSheet(1000, 1, "C4",   "", "", "", "", 2000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[124] =  new MusicSheet(1000, 1, "E4", "G4", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[125] =  new MusicSheet(1000, 0,   "",   "", "", "", "", 1000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[126] =  new MusicSheet(1000, 1, "E4", "G4", "", "", "", 1000, 1, "C6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[127] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[128] =  new MusicSheet(1000, 1, "B3", "", "", "", "",    2000, 1, "B5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[129] =  new MusicSheet(1000, 1, "D4", "G4", "", "", "",     0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[130] =  new MusicSheet(1000, 0,  "",   "", "", "", "",  1000, 1, "A5", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[131] =  new MusicSheet(1000, 1, "D4", "G4", "", "", "", 1000, 1, "B5", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[132] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[133] =  new MusicSheet(1000, 1, "C4",   "", "", "", "", 4000, 1, "C6", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[134] =  new MusicSheet(1000, 1, "E4", "G4", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[135] =  new MusicSheet(1000, 0,   "",   "", "", "", "",    0, 0, "", "", "", "", "");
        ((PianoApp)mContext.getApplicationContext()).musicTable[136] =  new MusicSheet(1000, 1, "E4", "G4", "", "", "",    0, 0, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[137] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[138] =  new MusicSheet(2000, 1, "C4", "G4", "", "", "", 2000, 1, "C6", "", "", "", "");

        ((PianoApp)mContext.getApplicationContext()).musicTable[139] =  new MusicSheet(  0, 2,   "", "", "", "", "", 0, 2, "", "", "", "", "");

    }

    /*
     *  Method Name : playMusic
     *  Description : 음 재생 시작
     * */
    public void playMusic () {

        // Play Start
        if (!getPlayingMusic()) {

            ((PianoApp)mContext.getApplicationContext()).stopMusicFlag = false;

            setPlayingMusic(true);

            inx_high = ((PianoApp)mContext.getApplicationContext()).currPlaySeq;
            inx_low = ((PianoApp)mContext.getApplicationContext()).currPlaySeq;

            String v_chord_play = ((PianoApp)mContext.getApplicationContext()).chordPlay;

            // High Music Chord
            if ( v_chord_play.equals("ALL") || v_chord_play.equals("REV") || v_chord_play.equals("HIGH") ) {
                playHighNote(1000);
            }

            // Low Music Chord
            if ( v_chord_play.equals("ALL") || v_chord_play.equals("LOW") ) {
                playLowNote(1000);
            }

        } else {
            ((PianoApp)mContext.getApplicationContext()).stopMusicFlag = true;

            setPlayingMusic(false);
            ((PianoApp)mContext.getApplicationContext()).clearKeyDown();
        }

    }

    private void playHighNote(int delayTime) {

        highHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                float playSpeed = ((PianoApp)mContext.getApplicationContext()).playSpeed;
                String v_chord_play = ((PianoApp)mContext.getApplicationContext()).chordPlay;

                highNoteLength = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_high].getHighNoteLength();
                highPlayFlag  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_high].getHighPlayFlag();
                highNote1  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_high].getHighNoteNo1();
                highNote2  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_high].getHighNoteNo2();
                highNote3  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_high].getHighNoteNo3();
                highNote4  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_high].getHighNoteNo4();
                highNoteTie  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_high].getHighNoteTie();

                int highPlayLength = (int)Math.round(getHighNoteLength (inx_high, highNoteLength, highPlayFlag, highNote1, highNote2, highNote3, highNote4 ,highNoteTie)/playSpeed);
                int highDisplayLength = (int)Math.round(highNoteLength/playSpeed);

                if ( highNoteLength > 0 ) {
                    ((PianoApp) mContext.getApplicationContext()).currPlaySeq = inx_high;
                }

                if (prev_highKeyNote1 > 1) ((PianoApp)mContext.getApplicationContext()).setKeyUp(prev_highKeyNote1);
                if (prev_highKeyNote2 > 1) ((PianoApp)mContext.getApplicationContext()).setKeyUp(prev_highKeyNote2);
                if (prev_highKeyNote3 > 1) ((PianoApp)mContext.getApplicationContext()).setKeyUp(prev_highKeyNote3);
                if (prev_highKeyNote4 > 1) ((PianoApp)mContext.getApplicationContext()).setKeyUp(prev_highKeyNote4);

                if ( highPlayFlag == 1) {
                    if (highNote1 > 0) {
                        if (soundPlayer.isNotePlaying(highNote1)) {
                            soundPlayer.stopNote(highNote1);
                        }
                        soundPlayer.playNote( highNote1 , highPlayLength);

                        curr_highKeyNote1 = SoundMap.getKeyNo(SoundMap.getSoundNote(highNote1));
                        ((PianoApp)mContext.getApplicationContext()).setKeyDown(curr_highKeyNote1);
                        prev_highKeyNote1 = curr_highKeyNote1;
                    }

                    if (highNote2 > 0) {
                        if (soundPlayer.isNotePlaying(highNote2)) {
                            soundPlayer.stopNote(highNote2);
                        }
                        soundPlayer.playNote( highNote2 , highPlayLength);

                        curr_highKeyNote2 = SoundMap.getKeyNo(SoundMap.getSoundNote(highNote2));
                        ((PianoApp)mContext.getApplicationContext()).setKeyDown(curr_highKeyNote2);
                        prev_highKeyNote2 = curr_highKeyNote2;
                    }

                    if (highNote3 > 0) {
                        if (soundPlayer.isNotePlaying(highNote3)) {
                            soundPlayer.stopNote(highNote3);
                        }
                        soundPlayer.playNote( highNote3 , highPlayLength);

                        curr_highKeyNote3 = SoundMap.getKeyNo(SoundMap.getSoundNote(highNote3));
                        ((PianoApp)mContext.getApplicationContext()).setKeyDown(curr_highKeyNote3);
                        prev_highKeyNote3 = curr_highKeyNote3;
                    }

                    if (highNote4 > 0) {
                        if (soundPlayer.isNotePlaying(highNote4)) {
                            soundPlayer.stopNote(highNote4);
                        }
                        soundPlayer.playNote( highNote4 , highPlayLength);

                        curr_highKeyNote4 = SoundMap.getKeyNo(SoundMap.getSoundNote(highNote4));
                        ((PianoApp)mContext.getApplicationContext()).setKeyDown(curr_highKeyNote4);
                        prev_highKeyNote4 = curr_highKeyNote4;
                    }
                }

                if(v_chord_play.equals("REV")){
                    inx_high--;
                } else {
                    inx_high++;
                }

                if (inx_high >= 0 && codeLine > inx_high && !((PianoApp)mContext.getApplicationContext()).stopMusicFlag) {
                        highHandler.postDelayed(this, highDisplayLength);
                } else {
                    setPlayingMusic(false);
                    ((PianoApp)mContext.getApplicationContext()).clearKeyDown();
                }

             }

        }, delayTime);
    }

    private void playLowNote(int delayTime) {

        lowHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                float playSpeed = ((PianoApp)mContext.getApplicationContext()).playSpeed;
                String v_chord_play = ((PianoApp)mContext.getApplicationContext()).chordPlay;

                lowNoteLength = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_low].getLowNoteLength();
                lowPlayFlag  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_low].getLowPlayFlag();
                lowNote1  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_low].getLowNoteNo1();
                lowNote2  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_low].getLowNoteNo2();
                lowNote3  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_low].getLowNoteNo3();
                lowNote4  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_low].getLowNoteNo4();
                lowNoteTie  = ((PianoApp)mContext.getApplicationContext()).musicTable[inx_low].getLowNoteTie();

                int lowPlayLength = (int)Math.round(getLowNoteLength (inx_low, lowNoteLength, lowPlayFlag, lowNote1, lowNote2, lowNote3, lowNote4 ,lowNoteTie)/playSpeed);
                int lowDisplayLength = (int)Math.round(lowNoteLength/playSpeed);

                if (lowNoteLength > 0) {
                    if (((PianoApp) mContext.getApplicationContext()).currPlaySeq < inx_low) {
                        ((PianoApp) mContext.getApplicationContext()).currPlaySeq = inx_low;
                    }
                }

                if (prev_lowKeyNote1 > 1) ((PianoApp)mContext.getApplicationContext()).setKeyUp(prev_lowKeyNote1);
                if (prev_lowKeyNote2 > 1) ((PianoApp)mContext.getApplicationContext()).setKeyUp(prev_lowKeyNote2);
                if (prev_lowKeyNote3 > 1) ((PianoApp)mContext.getApplicationContext()).setKeyUp(prev_lowKeyNote3);
                if (prev_lowKeyNote4 > 1) ((PianoApp)mContext.getApplicationContext()).setKeyUp(prev_lowKeyNote4);

                if ( lowPlayFlag == 1) {
                    if (lowNote1 > 0) {

                        if (soundPlayer.isNotePlaying(lowNote1)) {
                            soundPlayer.stopNote(lowNote1);
                        }
                        soundPlayer.playNote( lowNote1 , lowPlayLength);

                        curr_lowKeyNote1 = SoundMap.getKeyNo(SoundMap.getSoundNote(lowNote1));
                        ((PianoApp)mContext.getApplicationContext()).setKeyDown(curr_lowKeyNote1);
                        prev_lowKeyNote1 = curr_lowKeyNote1;
                    }

                    if (lowNote2 > 0) {
                        if (soundPlayer.isNotePlaying(lowNote2)) {
                            soundPlayer.stopNote(lowNote2);
                        }
                        soundPlayer.playNote( lowNote2 , lowPlayLength);

                        curr_lowKeyNote2 = SoundMap.getKeyNo(SoundMap.getSoundNote(lowNote2));
                        ((PianoApp)mContext.getApplicationContext()).setKeyDown(curr_lowKeyNote2);
                        prev_lowKeyNote2 = curr_lowKeyNote2;
                    }

                    if (lowNote3 > 0) {
                        if (soundPlayer.isNotePlaying(lowNote3)) {
                            soundPlayer.stopNote(lowNote3);
                        }
                        soundPlayer.playNote( lowNote3 , lowPlayLength);

                        curr_lowKeyNote3 = SoundMap.getKeyNo(SoundMap.getSoundNote(lowNote3));
                        ((PianoApp)mContext.getApplicationContext()).setKeyDown(curr_lowKeyNote3);
                        prev_lowKeyNote3 = curr_lowKeyNote3;
                    }

                    if (lowNote4 > 0) {
                        if (soundPlayer.isNotePlaying(lowNote4)) {
                            soundPlayer.stopNote(lowNote4);
                        }
                        soundPlayer.playNote( lowNote4 , lowPlayLength);

                        curr_lowKeyNote4 = SoundMap.getKeyNo(SoundMap.getSoundNote(lowNote4));
                        ((PianoApp)mContext.getApplicationContext()).setKeyDown(curr_lowKeyNote4);
                        prev_lowKeyNote4 = curr_lowKeyNote4;
                    }
                }

                if(v_chord_play.equals("REV")){
                    inx_low --;
                } else {
                    inx_low ++;
                }

                if ( inx_low >= 0 && codeLine > inx_low && !((PianoApp)mContext.getApplicationContext()).stopMusicFlag) {
                    lowHandler.postDelayed(this, lowDisplayLength);
                } else {
                    setPlayingMusic(false);
                    ((PianoApp)mContext.getApplicationContext()).clearKeyDown();
                }

            }

        }, delayTime);
    }

    private Handler highHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private Handler lowHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    public boolean getPlayingMusic() {
        return ((PianoApp)mContext.getApplicationContext()).isPlayingMusic;
    }

    public void setPlayingMusic(boolean playingMusic) {
        ((PianoApp)mContext.getApplicationContext()).isPlayingMusic = playingMusic;
    }

    public boolean isEmpty(Object obj) {
        if (obj == null) { return true; }
        if ((obj instanceof String) && (((String)obj).trim().length() == 0)) { return true; }
        if (obj instanceof Map) { return ((Map<?, ?>)obj).isEmpty(); }
        if (obj instanceof List) { return ((List<?>)obj).isEmpty(); }
        if (obj instanceof Object[]) { return (((Object[])obj).length == 0); }

        return false;
    }

    private int getLowNoteLength ( int p_inx
                                 , int p_note_length
                                 , int p_play_flag
                                 , int p_note_no1
                                 , int p_note_no2
                                 , int p_note_no3
                                 , int p_note_no4
                                 , String p_note_tie
                                ) {

        // ----------------------------------
        // 기본 Check
        // ----------------------------------
        if ( p_inx == 0 ) {
            // 첫번째 악보
            return p_note_length;
        } else {
            if ( p_play_flag == 1) {
                // Tie 아닌 경우
                if (p_note_tie == null || !p_note_tie.equals("T")) {
                    return p_note_length;
                }
            } else {
                return p_note_length;
            }
        }

        // ----------------------------------
        // 이전 악보가 Tie 인지 Check
        // 마디 감안
        // ----------------------------------
        boolean v_tie_flag = false;

        for ( int jnx = p_inx-1; jnx > 0; jnx --) {

            int v_note_length = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getLowNoteLength();
            int v_play_flag  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getLowPlayFlag();
            int v_note_no1  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getLowNoteNo1();
            int v_note_no2  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getLowNoteNo2();
            int v_note_no3  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getLowNoteNo3();
            int v_note_no4  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getLowNoteNo4();
            String v_note_tie  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getLowNoteTie();

            //  마디인 경우 건너뛰기
            if (v_play_flag == 2) {
                continue;
            } else {

                if (v_note_tie == null) v_note_tie = "";

                if ( v_note_length > 0 &&
                     v_play_flag == 1 &&
                     v_play_flag == p_play_flag &&
                     v_note_no1 == p_note_no1 &&
                     //v_note_no2 == p_note_no2 &&
                     //v_note_no3 == p_note_no3 &&
                     //v_note_no4 == p_note_no4 &&
                     v_note_tie.equals("T")) {
                    v_tie_flag = true;
                }
                break;
            }
        }  // for end

        // 이전악보가 Tie 경우
        if (v_tie_flag) {
            return 0;
        }

        // ----------------------------------
        // 다음악보가 Tie 경우
        // 음표길이 Sum
        // ----------------------------------
        int vArrayCount = ((PianoApp)mContext.getApplicationContext()).musicTable.length;

        int v_note_length_sum = p_note_length;

        for ( int knx = p_inx+1; knx < vArrayCount; knx --) {

            int v_note_length = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getLowNoteLength();
            int v_play_flag  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getLowPlayFlag();
            int v_note_no1  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getLowNoteNo1();
            int v_note_no2  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getLowNoteNo2();
            int v_note_no3  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getLowNoteNo3();
            int v_note_no4  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getLowNoteNo4();
            String v_note_tie  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getLowNoteTie();

            //  마디인 경우 건너뛰기
            if (v_play_flag == 2) {
                continue;
            } else {

                if (v_note_tie == null) v_note_tie = "";

                if ( v_note_length > 0 &&
                        v_play_flag == 1 &&
                        v_play_flag == p_play_flag &&
                        v_note_no1 == p_note_no1 &&
                        //v_note_no2 == p_note_no2 &&
                        //v_note_no3 == p_note_no3 &&
                        //v_note_no4 == p_note_no4 &&
                        v_note_tie.equals("T")) {
                    v_note_length_sum = v_note_length_sum + v_note_length;
                } else {
                    break;
                }

            }
        }  // for end

        return v_note_length_sum;

    }

    private int getHighNoteLength ( int p_inx
            , int p_note_length
            , int p_play_flag
            , int p_note_no1
            , int p_note_no2
            , int p_note_no3
            , int p_note_no4
            , String p_note_tie
    ) {

        // ----------------------------------
        // 기본 Check
        // ----------------------------------
        if ( p_inx == 0 ) {
            // 첫번째 악보
            return p_note_length;
        } else {
            if ( p_play_flag == 1) {
                // Tie 아닌 경우
                if (p_note_tie == null || !p_note_tie.equals("T")) {
                    return p_note_length;
                }
            } else {
                return p_note_length;
            }
        }

        // ----------------------------------
        // 이전 악보가 Tie 인지 Check - 마디 감안
        // ----------------------------------
        boolean v_tie_flag = false;

        for ( int jnx = p_inx-1; jnx > 0; jnx --) {

            int v_note_length = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getHighNoteLength();
            int v_play_flag  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getHighPlayFlag();
            int v_note_no1  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getHighNoteNo1();
            int v_note_no2  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getHighNoteNo2();
            int v_note_no3  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getHighNoteNo3();
            int v_note_no4  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getHighNoteNo4();
            String v_note_tie  = ((PianoApp)mContext.getApplicationContext()).musicTable[jnx].getHighNoteTie();

            //  마디인 경우 건너뛰기
            if (v_play_flag == 2) {
                continue;
            } else {

                if (v_note_tie == null) v_note_tie = "";

                if ( v_note_length > 0 &&
                        v_play_flag == 1 &&
                        v_play_flag == p_play_flag &&
                        v_note_no1 == p_note_no1 &&
                        //v_note_no2 == p_note_no2 &&
                        //v_note_no3 == p_note_no3 &&
                        //v_note_no4 == p_note_no4 &&
                        v_note_tie.equals("T")) {
                    v_tie_flag = true;
                }
                break;
            }
        }  // for end

        // 이전악보가 Tie 경우
        if (v_tie_flag) {
            return 0;
        }

        // ----------------------------------
        // 다음악보가 Tie 경우 - 음표길이 Sum
        // ----------------------------------
        int vArrayCount = ((PianoApp)mContext.getApplicationContext()).musicTable.length;

        int v_note_length_sum = p_note_length;

        for ( int knx = p_inx+1; knx < vArrayCount; knx --) {

            int v_note_length = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getHighNoteLength();
            int v_play_flag  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getHighPlayFlag();
            int v_note_no1  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getHighNoteNo1();
            int v_note_no2  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getHighNoteNo2();
            int v_note_no3  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getHighNoteNo3();
            int v_note_no4  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getHighNoteNo4();
            String v_note_tie  = ((PianoApp)mContext.getApplicationContext()).musicTable[knx].getHighNoteTie();

            //  마디인 경우 건너뛰기
            if (v_play_flag == 2) {
                continue;
            } else {

                if (v_note_tie == null) v_note_tie = "";

                // 연속 Tie 경우에도 합한다.
                if ( v_note_length > 0 &&
                        v_play_flag == 1 &&
                        v_play_flag == p_play_flag &&
                        v_note_no1 == p_note_no1 &&
                        //v_note_no2 == p_note_no2 &&
                        //v_note_no3 == p_note_no3 &&
                        //v_note_no4 == p_note_no4 &&
                        v_note_tie.equals("T")) {
                    v_note_length_sum = v_note_length_sum + v_note_length;
                } else {
                    // Tie 아닌 경우 빠져 나오기
                    break;
                }

            }
        }  // for end

        return v_note_length_sum;

    }

}
