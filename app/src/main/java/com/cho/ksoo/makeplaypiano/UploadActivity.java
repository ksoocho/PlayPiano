package com.cho.ksoo.makeplaypiano;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload);

        Button btnConfirm = (Button) findViewById(R.id.btn_upload_confirm) ;

        btnConfirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Music Upload
                uploadMusicData();
            }
        });

    }

    public void uploadMusicData() {

        if (new PrefManager(this).isMusicClear()) {
            Toast.makeText(this, "먼저 작곡한 악보를 저장하세요",Toast.LENGTH_LONG).show();
            return;
        }

        int vArrayCount = ((PianoApp)this.getApplicationContext()).musicTable.length;
        int checkCount = 0;

        for (int inx = 0; inx < vArrayCount; inx++) {
            if (((PianoApp) this.getApplicationContext()).musicTable[inx].getHighNoteLength() > 0 ||
                    ((PianoApp) this.getApplicationContext()).musicTable[inx].getLowNoteLength() > 0 ) {
                checkCount++;
            }
        }

        if (checkCount < 50) {
            Toast.makeText(this, "악보 업로드 - 악보개수 50 이상 가능",Toast.LENGTH_LONG).show();
            return;
        }

        // Music ID = 0 이면 Music ID 생성
        // Music 정보를 저장하고 Music Sheet 정보도 저장함.
        if (!new PrefManager(this).isUserClear()) {

            int vUserId = new PrefManager(this).getUserId();

            int vMusicId = ((PianoApp)this.getApplicationContext()).playMusicId;
            String vKeySign = ((PianoApp)this.getApplicationContext()).playKeySign;
            String vTimeSign = ((PianoApp)this.getApplicationContext()).playTimeSign;
            int vPlaySpeed = (int)(((PianoApp)this.getApplicationContext()).playSpeed*10);


            EditText v_edt_title = (EditText)findViewById(R.id.edt_upload_title);
            EditText v_edt_descr = (EditText)findViewById(R.id.edt_upload_descr);
            EditText v_edt_composer = (EditText)findViewById(R.id.edt_upload_composer);


            String v_upload_title = v_edt_title.getText().toString();
            String v_upload_descr = v_edt_descr.getText().toString();
            String v_upload_composer = v_edt_composer.getText().toString();

            if (v_upload_title.length() == 0)
            {
                Toast.makeText(this, "곡제목은 필수입니다",Toast.LENGTH_LONG).show();
                return;
            }

            String vMusicTitle = v_upload_title;
            String vMusicTitleEng = " ";
            String vMusicDescr = v_upload_descr;
            String vComposerName = v_upload_composer;

            new UploadActivity.PostAsyncMusicSave().execute(new AsyncMusicParam(
                    String.valueOf(vMusicId),
                    String.valueOf(vUserId),
                    vMusicTitle,
                    vMusicTitleEng,
                    vMusicDescr,
                    vKeySign,
                    vTimeSign,
                    String.valueOf(vPlaySpeed),
                    vComposerName));;

        }
    }

    // -------------------------------------
    // Name : PostAsyncMusicSave
    // UPLOAD
    // Music 정보 생성 및 저장 ( Upload 시 )
    // -------------------------------------
    private class PostAsyncMusicSave extends AsyncTask<AsyncMusicParam, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private static final String MUSIC_URL = "http://ksoocho.cafe24.com/make_play_piano/ajax/ajaxMusicSave.php";

        @Override
        protected void onPreExecute() {

        } // onPreExecute

        @Override
        protected JSONArray doInBackground(AsyncMusicParam... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                params.put("musicId", args[0].musicId);
                params.put("musicUserId", args[0].musicUserId);
                params.put("musicTitle", args[0].musicTitle);
                params.put("musicTitleEng", args[0].musicTitleEng);
                params.put("musicDescr", args[0].musicDescr);
                params.put("musicKeySign", args[0].musicKeySign);
                params.put("musicTimeSign", args[0].musicTimeSign);
                params.put("musicSpeed", args[0].musicSpeed);
                params.put("composerName", args[0].composerName);

                JSONArray json = jsonParser.makeHttpRequestArr(MUSIC_URL, "POST", params);

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
            String v_return_code = "E";
            String v_return_msg = "";

            if (json != null) {

                try {

                    for (int i=0; i<json.length(); i++) {

                        JSONObject obj = json.getJSONObject(i);

                        v_music_id = obj.getInt("music_id");
                        v_return_code = obj.getString("return_code");
                        v_return_msg = obj.getString("return_msg");

                        // Music ID 저장
                        if ( v_return_code.equals("S")) {

                            ((PianoApp)getApplicationContext()).playMusicId = v_music_id;
                            new PrefManager(getApplicationContext()).saveMusic();

                            //uploadMusicSheet();
                            uploadMusicArray();

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } // onPostExecute

    } // PostAsyncMusic

    /*
     * --------------------------------------------------
     * 작곡한 음악정보 Database Upload
     *   -. music sheet 저장하기
     * --------------------------------------------------
     */
    public void uploadMusicArray(){

        JSONArray jsonArray = new JSONArray();


        // Insert Music Sheet ( Loop )
        int vArrayCount = ((PianoApp)this.getApplicationContext()).musicArrayCount;

        int vLastPlaySeq = vArrayCount - 1;

        for (int inx = 0; inx < vArrayCount; inx++) {
            if (((PianoApp) this.getApplicationContext()).musicTable[inx].getHighNoteLength() > 0 ||
                    ((PianoApp) this.getApplicationContext()).musicTable[inx].getLowNoteLength() > 0 ||
                    ((PianoApp) this.getApplicationContext()).musicTable[inx].getHighPlayFlag() > 0 ||
                    ((PianoApp) this.getApplicationContext()).musicTable[inx].getLowPlayFlag() > 0
            ) {
                vLastPlaySeq = inx;
            }
        }

        // Check User ID / Music ID
        if (!new PrefManager(this).isUserClear()) {

            int vMusicId = ((PianoApp)getApplicationContext()).playMusicId;

            // Insert Music Sheet
            for(int inx = 0; inx < vLastPlaySeq; inx++) {

                int v_lowNoteLength = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNoteLength();
                int v_lowPlayFlag = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowPlayFlag();
                String v_lowNote1 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNote1();
                String v_lowNote2 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNote2();
                String v_lowNote3 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNote3();
                String v_lowNote4 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNote4();
                String v_lowTie = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNoteTie();
                int v_highNoteLength = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNoteLength();
                int v_highPlayFlag = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighPlayFlag();
                String v_highNote1 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNote1();
                String v_highNote2 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNote2();
                String v_highNote3 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNote3();
                String v_highNote4 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNote4();
                String v_highTie = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNoteTie();

                if ( v_lowTie == null ) v_lowTie = "";
                if ( v_highTie == null ) v_highTie = "";

                JSONObject jsonObject = new JSONObject();

                try {

                    jsonObject.put("musicId", String.valueOf(vMusicId));
                    jsonObject.put("musicSeq", String.valueOf(inx));
                    jsonObject.put("lowNoteLength", String.valueOf(v_lowNoteLength));
                    jsonObject.put("lowPlayFlag", String.valueOf(v_lowPlayFlag));
                    jsonObject.put("lowNote1", v_lowNote1);
                    jsonObject.put("lowNote2", v_lowNote2);
                    jsonObject.put("lowNote3", v_lowNote3);
                    jsonObject.put("lowNote4", v_lowNote4);
                    jsonObject.put("lowTie", v_lowTie);
                    jsonObject.put("highNoteLength", String.valueOf(v_highNoteLength));
                    jsonObject.put("highPlayFlag", String.valueOf(v_highPlayFlag));
                    jsonObject.put("highNote1", v_highNote1);
                    jsonObject.put("highNote2", v_highNote2);
                    jsonObject.put("highNote3", v_highNote3);
                    jsonObject.put("highNote4", v_highNote4);
                    jsonObject.put("highTie", v_highTie);

                }catch (JSONException e){
                    e.printStackTrace();
                }

                jsonArray.put(jsonObject);

            } //for

            // JSON Array 보내기
            new UploadActivity.PostAsyncMusicArraySave().execute(jsonArray);

            Toast.makeText(this, "악보 Array 업로드",Toast.LENGTH_LONG).show();

        } // isUser

    }

    // -------------------------------------
    // Name : PostAsyncMusicArraySave
    // Music 악보 JSON Array 저장하기
    // -------------------------------------
    private class PostAsyncMusicArraySave extends AsyncTask<JSONArray, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private static final String MUSICARRAY_URL = "http://ksoocho.cafe24.com/make_play_piano/ajax/ajaxMusicArraySave.php";

        @Override
        protected void onPreExecute() {

        } // onPreExecute

        @Override
        protected JSONArray doInBackground(JSONArray... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                params.put("musicJSON", args[0].toString());

                JSONArray json = jsonParser.makeHttpRequestArr(MUSICARRAY_URL, "POST", params);

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
            String v_return_code = "E";
            String v_return_msg = "";

            if (json != null) {

                try {

                    for (int i=0; i<json.length(); i++) {

                        JSONObject obj = json.getJSONObject(i);

                        v_music_id = obj.getInt("music_id");
                        v_return_code = obj.getString("return_code");
                        v_return_msg = obj.getString("return_msg");

                        if ( v_return_code.equals("S")) {
                            ((PianoApp)getApplicationContext()).playMusicId = v_music_id;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } // onPostExecute

    } // PostAsyncMusicArray

    public void uploadMusicSheet(){

        // Insert Music Sheet ( Loop )
        int vArrayCount = ((PianoApp)this.getApplicationContext()).musicArrayCount;

        int vLastPlaySeq = vArrayCount - 1;

        for (int inx = 0; inx < vArrayCount; inx++) {
            if (((PianoApp) this.getApplicationContext()).musicTable[inx].getHighNoteLength() > 0 ||
                    ((PianoApp) this.getApplicationContext()).musicTable[inx].getLowNoteLength() > 0 ||
                    ((PianoApp) this.getApplicationContext()).musicTable[inx].getHighPlayFlag() > 0 ||
                    ((PianoApp) this.getApplicationContext()).musicTable[inx].getLowPlayFlag() > 0
            ) {
                vLastPlaySeq = inx;
            }
        }

        // Check User ID / Music ID
        if (!new PrefManager(this).isUserClear()) {

            int vMusicId = ((PianoApp)getApplicationContext()).playMusicId;

            // Insert Music Sheet
            for(int inx = 0; inx < vLastPlaySeq; inx++) {

                int v_lowNoteLength = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNoteLength();
                int v_lowPlayFlag = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowPlayFlag();
                String v_lowNote1 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNote1();
                String v_lowNote2 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNote2();
                String v_lowNote3 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNote3();
                String v_lowNote4 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNote4();
                String v_lowTie = ((PianoApp)this.getApplicationContext()).musicTable[inx].getLowNoteTie();
                int v_highNoteLength = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNoteLength();
                int v_highPlayFlag = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighPlayFlag();
                String v_highNote1 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNote1();
                String v_highNote2 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNote2();
                String v_highNote3 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNote3();
                String v_highNote4 = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNote4();
                String v_highTie = ((PianoApp)this.getApplicationContext()).musicTable[inx].getHighNoteTie();

                if ( v_lowTie == null ) v_lowTie = "";
                if ( v_highTie == null ) v_highTie = "";

                new UploadActivity.PostAsyncMusicSheetSave().execute(new AsyncMusicSheetParam( String.valueOf(vMusicId), String.valueOf(inx),
                        String.valueOf(v_lowNoteLength), String.valueOf(v_lowPlayFlag), v_lowNote1, v_lowNote2, v_lowNote3, v_lowNote4, v_lowTie,
                        String.valueOf(v_highNoteLength), String.valueOf(v_highPlayFlag), v_highNote1, v_highNote2, v_highNote3, v_highNote4, v_highTie
                ));

            }

            Toast.makeText(this, "악보 Sheet 업로드",Toast.LENGTH_LONG).show();

        }
    }


    // -------------------------------------
    // Name : PostAsyncMusicSheetSave
    // Music 악보 개별 Sheet 저장하기
    // -------------------------------------
    private class PostAsyncMusicSheetSave extends AsyncTask<AsyncMusicSheetParam, String, JSONArray> {

        JSONParser jsonParser = new JSONParser();

        private static final String MUSICSHEET_URL = "http://ksoocho.cafe24.com/make_play_piano/ajax/ajaxMusicSheetSave.php";

        @Override
        protected void onPreExecute() {

        } // onPreExecute

        @Override
        protected JSONArray doInBackground(AsyncMusicSheetParam... args) {

            try {

                HashMap<String, String> params = new HashMap<>();

                params.put("musicId", args[0].musicId);
                params.put("musicSeq", args[0].musicSeq);
                params.put("lowNoteLength", args[0].lowNoteLength);
                params.put("lowPlayFlag", args[0].lowPlayFlag);
                params.put("lowNote1", args[0].lowNote1);
                params.put("lowNote2", args[0].lowNote2);
                params.put("lowNote3", args[0].lowNote3);
                params.put("lowNote4", args[0].lowNote4);
                params.put("lowTie", args[0].lowTie);
                params.put("highNoteLength", args[0].highNoteLength);
                params.put("highPlayFlag", args[0].highPlayFlag);
                params.put("highNote1", args[0].highNote1);
                params.put("highNote2", args[0].highNote2);
                params.put("highNote3", args[0].highNote3);
                params.put("highNote4", args[0].highNote4);
                params.put("highTie", args[0].highTie);

                JSONArray json = jsonParser.makeHttpRequestArr(MUSICSHEET_URL, "POST", params);

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
            String v_return_code = "E";
            String v_return_msg = "";

            if (json != null) {

                try {

                    for (int i=0; i<json.length(); i++) {

                        JSONObject obj = json.getJSONObject(i);

                        v_music_id = obj.getInt("music_id");
                        v_return_code = obj.getString("return_code");
                        v_return_msg = obj.getString("return_msg");

                        if ( v_return_code.equals("S")) {
                            ((PianoApp)getApplicationContext()).playMusicId = v_music_id;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } // onPostExecute

    } // PostAsyncMusicSheet

}
