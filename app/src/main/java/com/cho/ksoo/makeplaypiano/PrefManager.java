package com.cho.ksoo.makeplaypiano;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    Context context;

    PrefManager(Context context) {
        this.context = context;
    }

    // ---------------------------------------------------
    //  User 정보
    // ---------------------------------------------------

    // User 정보 저장
    public void saveUser(int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", userId);
        editor.commit();
    }

    // User 정보 Clear
    public void removeUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_id");
        editor.commit();
    }

    // User 정보 ID
    public int getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", 0);
    }

    // User 정보 Clear Check
    public boolean isUserClear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        boolean isUserIdClear = (sharedPreferences.getInt("user_id", 0) == 0);
        return isUserIdClear;
    }

    // ---------------------------------------------------
    //  User Grade 정보
    // ---------------------------------------------------

    // User Grade 정보 저장
    public void saveUserGrade(String userGrade) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Grade", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_grade", userGrade);
        editor.commit();
    }

    // User Grade 정보 Clear
    public void removeUserGrade() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Grade", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_grade");
        editor.commit();
    }

    // User Grade 정보
    public String getUserGrade() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Grade", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_grade", " ");
    }
    // ---------------------------------------------------
    //  Music 정보
    // ---------------------------------------------------

    // Music 정보 저장
    public void saveMusic() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Music", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int playMusicId = ((PianoApp)context.getApplicationContext()).playMusicId;
        String playKeySign = ((PianoApp)context.getApplicationContext()).playKeySign;
        String playTimeSign = ((PianoApp)context.getApplicationContext()).playTimeSign;
        float playSpeed = ((PianoApp)context.getApplicationContext()).playSpeed;

        editor.putInt("music_id",playMusicId);
        editor.putString("music_key_sign",playKeySign);
        editor.putString("music_time_sign",playTimeSign);
        editor.putFloat("music_play_speed",playSpeed);

        editor.commit();
    }

    // 위치 정보 Clear
    public void removeMusic() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("Music", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("music_id");
        editor.remove("music_key_sign");
        editor.remove("music_time_sign");
        editor.remove("music_play_speed");

        editor.commit();

    }

    // Music 정보 가져오기
    public int getMusicId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Music", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("music_id", 0);
    }

    // Music 정보 가져오기
    public String getMusicKeySign() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Music", Context.MODE_PRIVATE);
        return sharedPreferences.getString("music_key_sign", "");
    }

    // Music 정보 가져오기
    public String getMusicTimeSign() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Music", Context.MODE_PRIVATE);
        return sharedPreferences.getString("music_time_sign", "");
    }

    // Music 정보 가져오기
    public Float getMusicPlaySpeed() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Music", Context.MODE_PRIVATE);
        return sharedPreferences.getFloat("music_play_speed", 3.0f);
    }

    // Music 정보 Clear 여부 Check
    public boolean isMusicClear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Music", Context.MODE_PRIVATE);
        boolean isMusicClear = (sharedPreferences.getInt("music_id", -1) == -1);
        return isMusicClear;
    }

}
