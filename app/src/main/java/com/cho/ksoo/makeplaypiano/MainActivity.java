package com.cho.ksoo.makeplaypiano;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    PianoView pianoView;
    PlayMusicSheet playSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 화면을 landscape(가로) 화면으로 고정하고 싶은 경우
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main);

        // User ID 없으면 User ID 생성
        int vUserId = 0;

        if (new PrefManager(this).isUserClear()) {
            new MainActivity.PostAsyncUserSave().execute(new AsyncUserParam("0","NEW","NEW","NEW"));;
        } else {
            // Music User 정보 가져오기
            vUserId = new PrefManager(this).getUserId();
            new MainActivity.PostAsyncMusicUserInfo().execute(new AsyncUserParam(String.valueOf(vUserId),"","",""));;
        }

        pianoView = (PianoView) findViewById(R.id.pianoView);

        // ----------------------------------------
        // 악보 List 가져오기 - OPEN + User ID별
        // ----------------------------------------
        for (int inx =0; inx < 50; inx++) {
            ((PianoApp)this.getApplicationContext()).musicList[inx] =  new MusicList("","","","");
        }

        new PostAsyncMusicList().execute(String.valueOf(vUserId));

        // ----------------------------------------
        // Default 악보
        // ----------------------------------------
        int vMusicArrayCount = ((PianoApp)this.getApplicationContext()).musicArrayCount;

        // Music Table Initialize
        for (int inx =0; inx < vMusicArrayCount; inx++) {
            ((PianoApp)this.getApplicationContext()).musicTable[inx] =  new MusicSheet(
                    0, 0,   "", "", "", "", "",
                    0, 0, "", "", "", "", "");
        }

        // Play Music 초기화
       playSheet = new PlayMusicSheet( this, vMusicArrayCount);

        // 피아노 자동 연주 모드 처리 - 지정된 곡을 연주한다.
        // Load Music Sheet
        playSheet.loadMusic();

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        //if ( ((PianoApp)this.getApplicationContext()).modeMakePlay.equals("MAKE")) {
        //    new PrefManager(this).saveMusic();
        //    playSheet.saveMusicFile();
        //    Toast.makeText(this, "악보 임시저장",Toast.LENGTH_LONG).show();
        //}

        //Toast.makeText(this, "onPause",Toast.LENGTH_LONG).show();

        // -----------------------------------------
        // 앱 종료
        // -----------------------------------------
        //ActivityCompat.finishAffinity(this);
        //System.runFinalizersOnExit(true);
        //System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {

        Toast.makeText(this, "onBackPressed",Toast.LENGTH_LONG).show();
        ActivityCompat.finishAffinity(this);
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

    // -----------------------------------------------------------------
    // Option Menu 보이기 - onCreateOptionsMenu
    // -----------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        String v_mode = ((PianoApp)this.getApplicationContext()).modeMakePlay;

        String v_user_grade = new PrefManager(this).getUserGrade();

        menu.findItem(R.id.menu_list).setVisible(false);
        menu.findItem(R.id.menu_sound).setVisible(false);
        menu.findItem(R.id.menu_tune).setVisible(false);
        menu.findItem(R.id.menu_clear).setVisible(false);
        menu.findItem(R.id.menu_make).setVisible(false);
        menu.findItem(R.id.menu_save).setVisible(false);
        menu.findItem(R.id.menu_upload).setVisible(false);

        if (v_mode.equals("PLAY")) {

            menu.findItem(R.id.menu_list).setVisible(true);
            menu.findItem(R.id.menu_sound).setVisible(true);

            if ( v_user_grade.equals("ADMIN")) {
                menu.findItem(R.id.menu_tune).setVisible(true);
            }

        } else {

            menu.findItem(R.id.menu_clear).setVisible(true);
            menu.findItem(R.id.menu_make).setVisible(true);
            menu.findItem(R.id.menu_save).setVisible(true);
            menu.findItem(R.id.menu_upload).setVisible(true);

        }

        return super.onCreateOptionsMenu(menu);
    }

    // -----------------------------------------------------------------
    // Option Menu 선택한 경우 - onOptionsItemSelected
    //    -. 모드변경 - 피아노 연주모드 / 자동재생모드
    //    -. 피아노 곡 지정 화면 이동 ( 연주속도 )
    //    -. 지정된 곡 피아노 연주
    //    -. 피아노 연주 정지
    // -----------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        float vPlaySpeed = ((PianoApp)this.getApplicationContext()).playSpeed;

        int curId = item.getItemId();

        switch (curId) {
            case R.id.menu_play:
                // -----------------------------------------
                // Play Music
                // -----------------------------------------
                playSheet.playMusic();

                break;

            case R.id.menu_list:
                // -----------------------------------------
                // 악보목록가져오기
                // -----------------------------------------
                getMusicList();

                break;

            case R.id.menu_sound:
                // -----------------------------------------
                // 악보가져오기 (Random)
                // -----------------------------------------
                getMusicSheet();

                break;

            case R.id.menu_tune:
                // -----------------------------------------
                // 악보 Tune
                // -----------------------------------------
                // -----------------------------------------
                // 악보 Clear
                // -----------------------------------------
                AlertDialog.Builder dialogTune = new AlertDialog.Builder(this);
                dialogTune.setCancelable(false);
                dialogTune.setTitle("악보편곡");
                dialogTune.setMessage("임시저장악보는 없어집니다. 악보를 편곡하시겠습니까?");

                dialogTune.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        //악보편곡
                        tuneMusicSheet();

                    }
                });

                dialogTune.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "취소",Toast.LENGTH_LONG).show();
                    }
                });

                final AlertDialog alertTune = dialogTune.create();
                alertTune.show();

                break;

            case R.id.menu_clear:

                // -----------------------------------------
                // 악보 Clear
                // -----------------------------------------
                AlertDialog.Builder dialogClear = new AlertDialog.Builder(this);
                dialogClear.setCancelable(false);
                dialogClear.setTitle("악보지우기");
                dialogClear.setMessage("악보를 새로 입력하시겠습니까?");

                dialogClear.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        clearMusicSheet();
                    }
                });

                dialogClear.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "취소",Toast.LENGTH_LONG).show();
                    }
                });

                final AlertDialog alertClear = dialogClear.create();
                alertClear.show();

                break;

            case R.id.menu_make:

                // -----------------------------------------
                // 악보 Clear
                // -----------------------------------------
                AlertDialog.Builder dialogLoad = new AlertDialog.Builder(this);
                dialogLoad.setCancelable(false);
                dialogLoad.setTitle("악보열기");
                dialogLoad.setMessage("임시저장악보를 가져오시겠습니까?");

                dialogLoad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        loadMusicSheet();
                    }
                });

                dialogLoad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "취소",Toast.LENGTH_LONG).show();
                    }
                });

                final AlertDialog alertLoad = dialogLoad.create();
                alertLoad.show();

                break;

            case R.id.menu_save:

                // -----------------------------------------
                // Save Music Sheet ( to File )
                // -----------------------------------------

                if ( ((PianoApp)this.getApplicationContext()).modeMakePlay.equals("MAKE")) {

                    new PrefManager(this).saveMusic();
                    playSheet.saveMusicFile();

                    Toast.makeText(this, "악보 임시저장",Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.menu_upload:

                // -----------------------------------------
                // Music Sheet Upload
                // -----------------------------------------

                 if ( ((PianoApp)this.getApplicationContext()).modeMakePlay.equals("MAKE")) {

                      AlertDialog.Builder dialogUpload = new AlertDialog.Builder(this);
                      dialogUpload.setCancelable(false);
                      dialogUpload.setTitle("악보 업로드");
                      dialogUpload.setMessage("악보를 서버에 업로드 하시겠습니까?");

                      dialogUpload.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int id) {

                              // UploadActivity 호출
                              Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                              startActivityForResult(intent, 5);  // Music Upload

                          }
                      });

                      dialogUpload.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              Toast.makeText(MainActivity.this, "취소",Toast.LENGTH_LONG).show();
                          }
                      });

                      final AlertDialog alertUpload = dialogUpload.create();
                      alertUpload.show();

                 }

                break;

            case R.id.menu_chord:

                // -----------------------------------------
                // Play Chord 전환 : ALL / HIGH / LOW
                // -----------------------------------------
                String v_chord = ((PianoApp)MainActivity.this.getApplicationContext()).chordPlay;

                if (v_chord.equals("ALL")) {
                    ((PianoApp)MainActivity.this.getApplicationContext()).chordPlay = "HIGH";
                    Toast.makeText(MainActivity.this, "Chord - High",Toast.LENGTH_LONG).show();
                } else if (v_chord.equals("HIGH")) {
                    ((PianoApp)MainActivity.this.getApplicationContext()).chordPlay = "LOW";
                    Toast.makeText(MainActivity.this, "Chord - Low",Toast.LENGTH_LONG).show();
                } else if (v_chord.equals("LOW")) {
                    ((PianoApp)MainActivity.this.getApplicationContext()).chordPlay = "REV";
                    Toast.makeText(MainActivity.this, "Chord - Reverse",Toast.LENGTH_LONG).show();
                } else if (v_chord.equals("REV")) {
                    ((PianoApp)MainActivity.this.getApplicationContext()).chordPlay = "ALL";
                    Toast.makeText(MainActivity.this, "Chord - All",Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.menu_mode:

                // -----------------------------------------
                // Make / Play Mode 전환
                // -----------------------------------------
                String v_mode = ((PianoApp)MainActivity.this.getApplicationContext()).modeMakePlay;
                String v_dialog_text = "";

                if (v_mode.equals("PLAY")) {
                    v_dialog_text = "피아노 작곡모드";
                } else {
                    v_dialog_text = "피아노 연주모드";
                }
                AlertDialog.Builder dialogMode = new AlertDialog.Builder(this);
                dialogMode.setCancelable(false);
                dialogMode.setTitle("모드변경");
                dialogMode.setMessage(v_dialog_text+"로 변경하시겠습니까?");

                dialogMode.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        changeMode();

                    }
                });

                dialogMode.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "취소",Toast.LENGTH_LONG).show();
                    }
                });

                final AlertDialog alertMode = dialogMode.create();
                alertMode.show();

                break;

            case R.id.menu_close:

                // -----------------------------------------
                // 앱 종료
                // -----------------------------------------
                ActivityCompat.finishAffinity(this);
                System.runFinalizersOnExit(true);
                System.exit(0);

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent resultIntent)
    {
        String v_music_id = "0";

        switch (requestCode)
        {
            case 0:  // Music 선택

                // 선택한 Music ID
                v_music_id = resultIntent.getStringExtra("music_id");

                if ( v_music_id == null || Integer.parseInt(v_music_id) == 0 )
                {
                    break;
                }

                // clear music sheet
                playSheet.clearMusic();

                // select music sheet from database
                new MainActivity.PostAsyncMusicSelect().execute(new AsyncMusicSelectParam(v_music_id, "OPEN" ));
                ((PianoApp)this.getApplicationContext()).currPlaySeq = 0;

                try{
                    Thread.sleep(2000);
                } catch (Exception e) {

                }

                playSheet.playMusic();

                break;

            default:

                break;
        }
    }

    /*
     * --------------------------------------------------
     * 모드 변경
     * --------------------------------------------------
     */

    public void changeMode(){
        String v_mode = ((PianoApp)this.getApplicationContext()).modeMakePlay;
        String v_dialog_text = "";

        if (v_mode.equals("PLAY")) {
            v_dialog_text = "피아노 작곡모드";
            ((PianoApp)this.getApplicationContext()).modeMakePlay = "MAKE";
            loadMusicSheet();
        } else {
            v_dialog_text = "피아노 연주모드";
            ((PianoApp)this.getApplicationContext()).modeMakePlay = "PLAY";
            getMusicSheet();
        }

        Toast.makeText(this, v_dialog_text,Toast.LENGTH_LONG).show();

        invalidateOptionsMenu();

    }

    /*
     * --------------------------------------------------
     * 음악파일 가져오기 Random
     * --------------------------------------------------
     */
    public void getMusicSheet(){

        // clear music sheet
        playSheet.clearMusic();

        // select music sheet from database
        new MainActivity.PostAsyncMusicSelect().execute(new AsyncMusicSelectParam("0", "OPEN" ));
        ((PianoApp)this.getApplicationContext()).currPlaySeq = 0;

        try{
            Thread.sleep(2000);
        } catch (Exception e) {

        }

        playSheet.playMusic();
    }

    /*
     * --------------------------------------------------
     * 음악목록 가져오기
     *  2020/04/11 수정
     * --------------------------------------------------
     */
    public void getMusicList(){

        // Music List 보여주기
        Intent intent = new Intent(getApplicationContext(), MusicListActivity.class);
        startActivityForResult(intent, 0);  // Music 선택하기

    }

    /*
     * ------------------------------------------------------------------------------------------
     * 작곡중인 임시저장 음악파일 가져오기
     * ------------------------------------------------------------------------------------------
     */
    public void loadMusicSheet() {
        // Load Music Sheet ( from File )
        if (new PrefManager(this).isMusicClear()) {

            clearMusicSheet();

        } else {

            ((PianoApp) this.getApplicationContext()).playMusicId = new PrefManager(this).getMusicId();
            ((PianoApp) this.getApplicationContext()).playKeySign = new PrefManager(this).getMusicKeySign();
            ((PianoApp) this.getApplicationContext()).playTimeSign = new PrefManager(this).getMusicTimeSign();
            ((PianoApp) this.getApplicationContext()).playSpeed = new PrefManager(this).getMusicPlaySpeed();

            // clear music sheet
            playSheet.clearMusic();

            // load music sheet from Temporary
            playSheet.loadMusicFile();
            ((PianoApp) this.getApplicationContext()).currPlaySeq = 0;

            Toast.makeText(this, "악보 가져오기", Toast.LENGTH_LONG).show();
        }
    }

    /*
     * --------------------------------------------------
     * 작곡중인 음악정보 Clear
     * --------------------------------------------------
     */
    public void clearMusicSheet() {

        // Clear Music Sheet
        ((PianoApp)this.getApplicationContext()).playMusicId = 0;
        ((PianoApp)this.getApplicationContext()).playKeySign = "";
        ((PianoApp)this.getApplicationContext()).playTimeSign = "";
        ((PianoApp)this.getApplicationContext()).playSpeed =  3.0f;

        playSheet.clearMusic();
        ((PianoApp)this.getApplicationContext()).currPlaySeq = 0;

        Toast.makeText(this, "Clear Music Sheet",Toast.LENGTH_LONG).show();

    }

    /*
     * --------------------------------------------------
     * 작곡중인 음악정보 Clear
     * --------------------------------------------------
     */
    public void tuneMusicSheet() {

        //playSheet.clearMusic();

        //현재악보 임시저장
        new PrefManager(MainActivity.this).saveMusic();
        playSheet.saveMusicFile();

        // 모드변경 (Make)
        changeMode();

        // Clear Music Sheet
        ((PianoApp)this.getApplicationContext()).playMusicId = 0;
        ((PianoApp)this.getApplicationContext()).currPlaySeq = 0;

        Toast.makeText(this, "Tune Music Sheet",Toast.LENGTH_LONG).show();

    }

    // -------------------------------------
    // Name : PostAsyncUser
    // User 정보 생성
    // -------------------------------------
    private class PostAsyncUserSave extends AsyncTask<AsyncUserParam, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private static final String USER_URL = "http://ksoocho.cafe24.com/make_play_piano/ajax/ajaxMusicUserInsert.php";

        @Override
        protected void onPreExecute() {

        } // onPreExecute

        @Override
        protected JSONArray doInBackground(AsyncUserParam... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                params.put("userCode", args[0].userCode);
                params.put("userPwd", args[0].userPwd);
                params.put("userName", args[0].userName);

                JSONArray json = jsonParser.makeHttpRequestArr(USER_URL, "POST", params);

                if (json != null) {
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        } // doInBackground

        protected void onPostExecute(JSONArray json) {

            int v_user_id = 0;
            String v_return_code = "E";
            String v_return_msg = "";

            if (json != null) {

                try {

                    for (int i=0; i<json.length(); i++) {

                        JSONObject obj = json.getJSONObject(i);

                        v_user_id = obj.getInt("music_user_id");
                        v_return_code = obj.getString("return_code");
                        v_return_msg = obj.getString("return_msg");

                        // User ID 저장
                        if ( v_return_code.equals("S")) {
                            new PrefManager(MainActivity.this).saveUser(v_user_id);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } // onPostExecute

    } // PostAsyncUser

    // -------------------------------------
    // Name : PostAsyncMusicSelect
    // Random PLAY
    // Music 악보 가져오기
    // -------------------------------------
    private class PostAsyncMusicSelect extends AsyncTask<AsyncMusicSelectParam, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private static final String MUSIC_SELECT_URL = "http://ksoocho.cafe24.com/make_play_piano/ajax/ajaxMusicSheetSelect.php";

        @Override
        protected void onPreExecute() {

        } // onPreExecute

        @Override
        protected JSONArray doInBackground(AsyncMusicSelectParam... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                params.put("musicId", args[0].musicId);
                params.put("activeStatus", args[0].activeStatus);

                JSONArray json = jsonParser.makeHttpRequestArr(MUSIC_SELECT_URL, "POST", params);

                if (json != null) {
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        } // doInBackground

        protected void onPostExecute(JSONArray json) {

            int v_music_seq = 0;
            int v_low_note_length = 0;
            int v_low_play_flag = 0;
            String v_low_note1 = "";
            String v_low_note2 = "";
            String v_low_note3 = "";
            String v_low_note4 = "";
            String v_low_tie = "";
            int v_high_note_length = 0;
            int v_high_play_flag = 0;
            String v_high_note1 = "";
            String v_high_note2 = "";
            String v_high_note3 = "";
            String v_high_note4 = "";
            String v_high_tie = "";
            int v_music_id = 0;

            if (json != null) {

                try {

                    for (int i=0; i<json.length(); i++) {

                        JSONObject obj = json.getJSONObject(i);

                        v_music_seq = obj.getInt("music_seq");
                        v_low_note_length = obj.getInt("low_note_length");
                        v_low_play_flag = obj.getInt("low_play_flag");
                        v_low_note1 = obj.getString("low_note1");
                        v_low_note2 = obj.getString("low_note2");
                        v_low_note3 = obj.getString("low_note3");
                        v_low_note4 = obj.getString("low_note4");
                        v_low_tie = obj.getString("low_tie");
                        v_high_note_length = obj.getInt("high_note_length");
                        v_high_play_flag = obj.getInt("high_play_flag");
                        v_high_note1 = obj.getString("high_note1");
                        v_high_note2 = obj.getString("high_note2");
                        v_high_note3 = obj.getString("high_note3");
                        v_high_note4 = obj.getString("high_note4");
                        v_high_tie = obj.getString("high_tie");

                        v_music_id = obj.getInt("music_id");

                        ((PianoApp)MainActivity.this.getApplicationContext()).musicTable[v_music_seq] =  new MusicSheet(
                                v_low_note_length, v_low_play_flag,   v_low_note1, v_low_note2, v_low_note3, v_low_note4, v_low_tie,
                                v_high_note_length, v_high_play_flag, v_high_note1, v_high_note2, v_high_note3, v_high_note4, v_high_tie);

                    }

                    ((PianoApp)MainActivity.this.getApplicationContext()).musicTable[v_music_seq+1] =  new MusicSheet(
                            0, 2,   "", "", "", "", "",
                            0, 2, "", "", "", "", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ((PianoApp)MainActivity.this.getApplicationContext()).playMusicId = v_music_id;

                // 악보정보 불러오기
                new MainActivity.PostAsyncMusicInfo().execute(new AsyncMusicSelectParam(String.valueOf(v_music_id) , "OPEN" ));
            }

        } // onPostExecute

    } // PostAsyncMusicSelect

    // -------------------------------------
    // Name : PostAsyncMusicInfo
    // Random PLAY
    // Music Info 가져오기
    // -------------------------------------
    private class PostAsyncMusicInfo extends AsyncTask<AsyncMusicSelectParam, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private static final String MUSIC_INFO_URL = "http://ksoocho.cafe24.com/make_play_piano/ajax/ajaxMusicSelect.php";

        @Override
        protected void onPreExecute() {

        } // onPreExecute

        @Override
        protected JSONArray doInBackground(AsyncMusicSelectParam... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                params.put("musicId", args[0].musicId);
                params.put("activeStatus", args[0].activeStatus);

                JSONArray json = jsonParser.makeHttpRequestArr(MUSIC_INFO_URL, "POST", params);

                if (json != null) {
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        } // doInBackground

        protected void onPostExecute(JSONArray json) {

            String v_music_title = "";
            String v_music_title_eng = "";
            String v_music_descr = "";
            String v_music_key_sign = "";
            String v_music_time_sign = "";
            int v_music_speed = 0;
            String v_composer_name = "";

            if (json != null) {

                try {

                    for (int i=0; i<json.length(); i++) {

                        JSONObject obj = json.getJSONObject(i);

                        v_music_title = obj.getString("music_title");
                        v_music_title_eng = obj.getString("music_title_eng");
                        v_music_descr = obj.getString("music_descr");
                        v_music_key_sign = obj.getString("music_key_sign");
                        v_music_time_sign = obj.getString("music_time_sign");
                        v_music_speed = obj.getInt("music_speed");
                        v_composer_name = obj.getString("composer_name");

                        ((PianoApp)MainActivity.this.getApplicationContext()).playMusicTitle = v_music_title;
                        ((PianoApp)MainActivity.this.getApplicationContext()).musicTitleEng = v_music_title_eng;
                        ((PianoApp)MainActivity.this.getApplicationContext()).musicDescr = v_music_descr;
                        ((PianoApp)MainActivity.this.getApplicationContext()).composerName = v_composer_name;
                        ((PianoApp)MainActivity.this.getApplicationContext()).playSpeed = (float)v_music_speed/10;
                        ((PianoApp)MainActivity.this.getApplicationContext()).playKeySign = v_music_key_sign;
                        ((PianoApp)MainActivity.this.getApplicationContext()).playTimeSign = v_music_time_sign;

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "제목 : "+v_music_title+"("+v_composer_name+")",Toast.LENGTH_LONG).show();

            }

        } // onPostExecute

    } // PostAsyncMusicInfo

    // -------------------------------------
    // Name : PostAsyncMusicUserInfo
    // Music User Info 가져오기
    // -------------------------------------
    private class PostAsyncMusicUserInfo extends AsyncTask<AsyncUserParam, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private static final String USER_INFO_URL = "http://ksoocho.cafe24.com/make_play_piano/ajax/ajaxMusicUserSelect.php";

        @Override
        protected void onPreExecute() {

        } // onPreExecute

        @Override
        protected JSONArray doInBackground(AsyncUserParam... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                params.put("musicUserId", args[0].userId);

                JSONArray json = jsonParser.makeHttpRequestArr(USER_INFO_URL, "POST", params);

                if (json != null) {
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        } // doInBackground

        protected void onPostExecute(JSONArray json) {

            String v_user_grade = "";

            if (json != null) {

                try {

                    for (int i=0; i<json.length(); i++) {

                        JSONObject obj = json.getJSONObject(i);

                        v_user_grade = obj.getString("user_grade");

                        new PrefManager(MainActivity.this).saveUserGrade(v_user_grade);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } // onPostExecute

    } // PostAsyncMusicInfo

    // -------------------------------------
    // Name : PostAsyncMusicList
    // Music List 가져오기
    // -------------------------------------
    private class PostAsyncMusicList extends AsyncTask<String, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private static final String MUSIC_INFO_URL = "http://ksoocho.cafe24.com/make_play_piano/ajax/ajaxMusicListSelect.php";

        @Override
        protected void onPreExecute() {

        } // onPreExecute

        @Override
        protected JSONArray doInBackground(String... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                params.put("musicUserId", args[0]);  // User ID

                JSONArray json = jsonParser.makeHttpRequestArr(MUSIC_INFO_URL, "POST", params);

                if (json != null) {
                    return json;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        } // doInBackground

        protected void onPostExecute(JSONArray json) {

            int v_music_id = 0;
            String v_music_title = "";
            String v_music_title_eng = "";
            String v_music_descr = "";
            String v_composer_name = "";
            String v_active_status = "";

            if (json != null) {

                try {

                    int vCount = json.length();

                    for (int i=0; i<vCount; i++) {

                        JSONObject obj = json.getJSONObject(i);

                        v_music_id = obj.getInt("music_id");
                        v_music_title = obj.getString("music_title");
                        v_music_title_eng = obj.getString("music_title_eng");
                        v_music_descr = obj.getString("music_descr");
                        v_composer_name = obj.getString("composer_name");
                        v_active_status = obj.getString("active_status");

                        String music_id =  String.valueOf(v_music_id);
                        String music_title = v_music_title+"/"+v_music_title_eng;
                        String music_descr =  v_music_descr+"/"+v_composer_name;
                        String music_status =  v_active_status;

                        ((PianoApp)MainActivity.this.getApplicationContext()).musicList[i] =  new MusicList(music_id,music_title,music_descr,music_status );

                    }

                    // 비동기처리
                    //displayMusicList();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } // onPostExecute

    } // PostAsyncMusicList

}
