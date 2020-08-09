package com.cho.ksoo.makeplaypiano;

import android.app.Application;
import java.util.Vector;

public class PianoApp extends Application {

    public static int musicListCount = 50;
    public static MusicList musicList[] = new MusicList[musicListCount];

    public static String modeMakePlay = "PLAY";
    public static String chordPlay = "ALL";
    public static int playMusicId = 0;
    public static String playKeySign = "";
    public static String playTimeSign = "";
    public static float playSpeed = 3.0f;
    public static boolean isPlayingMusic = false;

    public static String playMusicTitle = "";
    public static String musicTitleEng = "";
    public static String musicDescr = "";
    public static String composerName = "";

    public static int musicArrayCount = 500;

    public static MusicSheet musicTable[] = new MusicSheet[musicArrayCount];

    public static String modeLock = "UNLOCK";
    public static String makeMusicSheet = "HIGH";
    public static boolean stopMusicFlag = false;
    public static boolean helpFlag = false;

    public static int currPlaySeq = 0;

    public static int noteLength = 500;
    public static int restLength = 500;

    public static int musicSheetHeight = 500;
    public static int controlWidth = 300;
    public static int btnHighNoteXPos = 20;

    //상대좌표 결정 속성값
    public static int musicSheetWidth = 0;
    public static int displayCount = 0;
    public static int btnHighNoteYPos = 0;
    public static int btnControlXPos = 0;
    public static int btnControlYPos = 0;

    public static Vector<PianoKey> whites = new Vector<>();
    public static Vector<PianoKey> blacks = new Vector<>();

    public Vector<PianoKey> getWhites() {
        return whites;
    }
    public Vector<PianoKey> getBlacks() {
        return blacks;
    }

    public PianoKey keyForCoords(float x, float y) {

        for (PianoKey key : blacks) {
            if (key.getRect().contains(x,y)) {
                return key;
            }
        }

        for (PianoKey key : whites) {
            if (key.getRect().contains(x,y)) {
                return key;
            }
        }

        return null;
    }

    public void setKeyDown(int note) {

        for (PianoKey key : blacks) {
            if (key.getSound() == note) {
                key.setDown(true);
            }
        }

        for (PianoKey key : whites) {
            if (key.getSound() == note) {
                key.setDown(true);
            }
        }
    }

    public void setKeyUp(int note) {

        for (PianoKey key : blacks) {
            if (key.getSound() == note) {
                key.setDown(false);
            }
        }

        for (PianoKey key : whites) {
            if (key.getSound() == note) {
                key.setDown(false);
            }
        }
    }

    public void clearKeyDown() {

        for (PianoKey key : blacks) {
            key.setDown(false);
        }

        for (PianoKey key : whites) {
            key.setDown(false);
        }
    }

}
