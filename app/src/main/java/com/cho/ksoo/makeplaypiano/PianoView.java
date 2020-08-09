package com.cho.ksoo.makeplaypiano;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Vector;

public class PianoView extends SurfaceView implements SurfaceHolder.Callback {

    static PianoThread mThread;
    static SurfaceHolder mHolder;
    static Context mContext;

    private AudioKeyPlayer keyPlayer;
    static int view_width, view_height;		// View

    boolean surfaceCreated;

    public PianoView(Context context, AttributeSet attrs) {

        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mHolder = holder;  // holder 보존
        mContext = context; // context 보존

        mThread = new PianoThread(holder, context);  // Thread 생성
        keyPlayer = new AudioKeyPlayer(context);

        Display display = ((WindowManager) mContext.getSystemService (Context.WINDOW_SERVICE)).getDefaultDisplay();
        view_width = display.getWidth();
        view_height = display.getHeight();

        setFocusable(true);

        surfaceCreated = false;

    }

    //-------------------------------------
    //  Surface View가 생성될 때 실행되는 부분
    //-------------------------------------
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (surfaceCreated == false) {

            try {
                mThread.start();
            } catch (Exception e) {
                RestartProcess();
            }

            surfaceCreated = true;
        }

    }

    //-------------------------------------
    //  Surface View가 바뀔 때 실행되는 부분
    //-------------------------------------
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //-------------------------------------
    //  Surface View가 해제될 때 실행되는 부분
    //-------------------------------------
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        StopProcess();
        surfaceCreated = false;
    }

    //-------------------------------------
    //  Thread 관련
    //-------------------------------------
    // Stop
    public void StopProcess()
    {
        mThread.StopThread();
    }

    // Pause
    public void PauseProcess()
    {
        mThread.PauseThread(true);
    }

    // Resume
    public void ResumeProcess()
    {
        mThread.PauseThread(false);
    }

    // Restart
    public void RestartProcess()
    {
        mThread.StopThread();  // Thread 중지

        mThread = null;
        mThread = new PianoThread(mHolder,mContext);
        mThread.start();

    }

    //------------------------------------
    //      Timer Handler
    //------------------------------------
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            invalidate();		// View를 다시 그림
            mHandler.sendEmptyMessageDelayed(0, 10);
        }
    }; // Handler


    //------------------------------------
    //      onTouch Event
    //------------------------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int vDispCount = ((PianoApp)mContext.getApplicationContext()).displayCount;
        int vArrayCount = ((PianoApp)mContext.getApplicationContext()).musicTable.length;

        // 피아노 연주모드에서 눌린 피아노 건반에 대한 상태 처리
        int id[] = new int[10];
        int x[] = new int[10];
        int y[] = new int[10];

        PianoKey key;

        int pointer_count = event.getPointerCount(); //현재 터치 발생한 포인트 수를 얻는다.
        if(pointer_count > 10) pointer_count = 10; //4개 이상의 포인트를 터치했더라도 3개까지만 처리를 한다.

        switch(event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: //한 개 포인트에 대한 DOWN을 얻을 때.

                id[0] = event.getPointerId(0); //터치한 순간부터 부여되는 포인트 고유번호.
                x[0] = (int) (event.getX());
                y[0] = (int) (event.getY());

               key = ((PianoApp)mContext.getApplicationContext()).keyForCoords(x[0],y[0]);

                if (key != null) {
                     key.setDown(true);

                    int currSeq = ((PianoApp)mContext.getApplicationContext()).currPlaySeq;
                    String musicNote = SoundMap.getKeyNote(key.getSound());

                    if (((PianoApp)mContext.getApplicationContext()).modeMakePlay.equals("MAKE")) {

                         if (((PianoApp)mContext.getApplicationContext()).makeMusicSheet.equals("HIGH")) {

                             if (((PianoApp)mContext.getApplicationContext()).modeLock.equals("UNLOCK")) {
                                 int vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNoteLength();
                                 int vLowPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowPlayFlag();
                                 String vLowNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote1();
                                 String vLowNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote2();
                                 String vLowNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote3();
                                 String vLowNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote4();
                                 String vLowNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNoteTie();

                                 int vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).noteLength;

                                 ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq] = new MusicSheet(
                                         vLowNoteLength, vLowPlayFlag, vLowNote1, vLowNote2, vLowNote3, vLowNote4, vLowNoteTie,
                                         vHighNoteLength, 1, musicNote, "", "", "","");

                                 if ( vArrayCount - 1 > currSeq + 1 ) ((PianoApp)mContext.getApplicationContext()).currPlaySeq = currSeq + 1;

                             } else {

                                 int vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNoteLength();
                                 int vLowPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowPlayFlag();
                                 String vLowNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote1();
                                 String vLowNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote2();
                                 String vLowNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote3();
                                 String vLowNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote4();
                                 String vLowNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNoteTie();
                                 int vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNoteLength();
                                 int vHighPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighPlayFlag();
                                 String vHighNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote1();
                                 String vHighNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote2();
                                 String vHighNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote3();
                                 String vHighNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote4();
                                 String vHighNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNoteTie();

                                 if (vHighNote1.isEmpty()){
                                     vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).noteLength;
                                     vHighPlayFlag = 1;
                                     vHighNote1 = musicNote;
                                 } else if (vHighNote2.isEmpty()){
                                     vHighNote2 = musicNote;
                                 } else if (vHighNote3.isEmpty()){
                                     vHighNote3 = musicNote;
                                 } else if (vHighNote4.isEmpty()){
                                     vHighNote4 = musicNote;
                                 }

                                 ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq] = new MusicSheet(
                                         vLowNoteLength, vLowPlayFlag, vLowNote1, vLowNote2, vLowNote3, vLowNote4, vLowNoteTie,
                                           vHighNoteLength, vHighPlayFlag, vHighNote1, vHighNote2, vHighNote3, vHighNote4, vHighNoteTie);

                             }

                         } else {

                             if (((PianoApp)mContext.getApplicationContext()).modeLock.equals("UNLOCK")) {

                                 int vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNoteLength();
                                 int vHighPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighPlayFlag();
                                 String vHighNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote1();
                                 String vHighNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote2();
                                 String vHighNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote3();
                                 String vHighNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote4();
                                 String vHighNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNoteTie();

                                 int vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).noteLength;

                                 ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq] = new MusicSheet(
                                         vLowNoteLength, 1, musicNote, "", "", "", "",
                                         vHighNoteLength, vHighPlayFlag, vHighNote1, vHighNote2, vHighNote3, vHighNote4, vHighNoteTie);

                                 if ( vArrayCount - 1 > currSeq + 1 ) ((PianoApp) mContext.getApplicationContext()).currPlaySeq = currSeq + 1;

                             } else {

                                 int vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNoteLength();
                                 int vLowPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowPlayFlag();
                                 String vLowNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote1();
                                 String vLowNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote2();
                                 String vLowNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote3();
                                 String vLowNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote4();
                                 String vLowNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNoteTie();
                                 int vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNoteLength();
                                 int vHighPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighPlayFlag();
                                 String vHighNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote1();
                                 String vHighNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote2();
                                 String vHighNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote3();
                                 String vHighNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote4();
                                 String vHighNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNoteTie();

                                 if (vLowNote1.isEmpty()){
                                     vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).noteLength;
                                     vLowPlayFlag = 1;
                                     vLowNote1 = musicNote;
                                 } else if (vLowNote2.isEmpty()){
                                     vLowNote2 = musicNote;
                                 } else if (vLowNote3.isEmpty()){
                                     vLowNote3 = musicNote;
                                 } else if (vLowNote4.isEmpty()){
                                     vLowNote4 = musicNote;
                                 }
                                 ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq] = new MusicSheet(
                                         vLowNoteLength, vLowPlayFlag, vLowNote1, vLowNote2, vLowNote3, vLowNote4, vLowNoteTie,
                                         vHighNoteLength, vHighPlayFlag, vHighNote1, vHighNote2, vHighNote3, vHighNote4, vHighNoteTie);

                             } //Lock

                         } //makeMusicSheet

                    }

                } else {

                    // 버튼을 누른 경우
                    int vBtnXpos = ((PianoApp) mContext.getApplicationContext()).btnControlXPos;
                    int vBtnYpos = ((PianoApp) mContext.getApplicationContext()).btnControlYPos;

                    int vCurrPlaySeq = ((PianoApp)mContext.getApplicationContext()).currPlaySeq;

                    // -----------------------------
                    // Layer 0
                    // -----------------------------
                    // First Step
                    if (x[0] >= vBtnXpos-100 && y[0] >= vBtnYpos-100 && x[0] <= vBtnXpos && y[0] <= vBtnYpos) {
                        ((PianoApp)mContext.getApplicationContext()).currPlaySeq = 0;

                        setSymbolDisplay();
                    }

                    // Previous Step
                    if (x[0] >= vBtnXpos && y[0] >= vBtnYpos-100 && x[0] <= vBtnXpos + 100 && y[0] <= vBtnYpos) {
                        if ( vCurrPlaySeq - vDispCount < 0) {
                            ((PianoApp)mContext.getApplicationContext()).currPlaySeq = 0;
                        } else {
                            ((PianoApp)mContext.getApplicationContext()).currPlaySeq =  vCurrPlaySeq - vDispCount;
                        }

                        setSymbolDisplay();
                    }

                    // Next Step
                    if (x[0] >= vBtnXpos+100 && y[0] >= vBtnYpos-100 && x[0] <= vBtnXpos + 200 && y[0] <= vBtnYpos) {

                        int vLastPlaySeq = vArrayCount - 1;

                        for (int inx = 0; inx < vArrayCount; inx++) {
                            if (((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNoteLength() > 0 ||
                                    ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNoteLength() > 0 ) {
                                vLastPlaySeq = inx;
                            }
                        }

                        if ((vCurrPlaySeq + vDispCount) > (vLastPlaySeq)) {
                            ((PianoApp)mContext.getApplicationContext()).currPlaySeq = vLastPlaySeq;
                        } else {
                            ((PianoApp)mContext.getApplicationContext()).currPlaySeq =  vCurrPlaySeq + vDispCount;
                        }

                        setSymbolDisplay();
                    }

                    // Last Step
                    if (x[0] >= vBtnXpos+200 && y[0] >= vBtnYpos-100 && x[0] <= vBtnXpos + 300 && y[0] <= vBtnYpos) {

                        int vLastPlaySeq = vArrayCount - 1;

                        for (int inx = 0; inx < vArrayCount; inx++) {
                            if (((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNoteLength() > 0 ||
                                    ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNoteLength() > 0 ) {
                                vLastPlaySeq = inx;
                            }
                        }

                        ((PianoApp)mContext.getApplicationContext()).currPlaySeq = vLastPlaySeq;

                        setSymbolDisplay();
                    }

                    // -----------------------------
                    // Layer 1
                    // -----------------------------
                    float vPlaySpeed = ((PianoApp)mContext.getApplicationContext()).playSpeed;

                    // Next Play Speed
                    if (x[0] >= vBtnXpos-100 && y[0] >= vBtnYpos  && x[0] <= vBtnXpos && y[0] <= vBtnYpos + 100) {

                        float vNextPlaySpeed = getNextPlaySpeed(vPlaySpeed);
                        ((PianoApp)mContext.getApplicationContext()).playSpeed = vNextPlaySpeed;
                        Toast.makeText(mContext, "Play Speed - "+String.valueOf(vNextPlaySpeed),Toast.LENGTH_LONG).show();

                    }

                    // Previous Play Speed
                    if (x[0] >= vBtnXpos && y[0] >= vBtnYpos  && x[0] <= vBtnXpos+100 && y[0] <= vBtnYpos + 100) {
                        float vPrevPlaySpeed = getPrevPlaySpeed(vPlaySpeed);
                        ((PianoApp)mContext.getApplicationContext()).playSpeed = vPrevPlaySpeed;
                        Toast.makeText(mContext, "Play Speed - "+String.valueOf(vPrevPlaySpeed),Toast.LENGTH_LONG).show();
                    }


                    // 이전 Column
                    if (x[0] >= vBtnXpos+100 && y[0] >= vBtnYpos && x[0] <= vBtnXpos + 200 && y[0] <= vBtnYpos + 100) {

                        if ( vCurrPlaySeq - 1 < 0) {
                            ((PianoApp)mContext.getApplicationContext()).currPlaySeq = 0;
                        } else {
                            ((PianoApp)mContext.getApplicationContext()).currPlaySeq =  vCurrPlaySeq - 1;
                        }

                        setSymbolDisplay();
                    }

                    // 다음 Column
                    if (x[0] >= vBtnXpos + 200 && y[0] >= vBtnYpos && x[0] <= vBtnXpos + 300 && y[0] <= vBtnYpos + 100) {

                        if ((vCurrPlaySeq + 1) > (vArrayCount-1)) {
                            ((PianoApp)mContext.getApplicationContext()).currPlaySeq = vArrayCount - 1;
                        } else {
                            ((PianoApp)mContext.getApplicationContext()).currPlaySeq =  vCurrPlaySeq + 1;
                        }

                        setSymbolDisplay();
                    }

                    // -----------------------------
                    // Layer 2
                    // -----------------------------
                    // ---------------------------------------------------------
                    // 작곡모드인 경우만
                    // ---------------------------------------------------------
                    if (((PianoApp)mContext.getApplicationContext()).modeMakePlay.equals("MAKE")) {

                        // Add Button
                        if (x[0] >= vBtnXpos && y[0] >= vBtnYpos+100 && x[0] <= vBtnXpos + 100 && y[0] <= vBtnYpos + 200) {

                            if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Add Note or Rest",Toast.LENGTH_LONG).show();

                            if ( vArrayCount > vCurrPlaySeq + 1 ) {

                                for (int inx = vArrayCount - 2; vCurrPlaySeq < inx; inx--) {

                                    int vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNoteLength();
                                    int vLowPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowPlayFlag();
                                    String vLowNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote1();
                                    String vLowNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote2();
                                    String vLowNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote3();
                                    String vLowNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote4();
                                    String vLowNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNoteTie();
                                    int vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNoteLength();
                                    int vHighPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighPlayFlag();
                                    String vHighNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote1();
                                    String vHighNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote2();
                                    String vHighNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote3();
                                    String vHighNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote4();
                                    String vHighNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNoteTie();

                                    ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1] = new MusicSheet(
                                            vLowNoteLength, vLowPlayFlag, vLowNote1, vLowNote2, vLowNote3, vLowNote4, vLowNoteTie,
                                            vHighNoteLength, vHighPlayFlag, vHighNote1, vHighNote2, vHighNote3, vHighNote4, vHighNoteTie);

                                }

                                ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq + 1] = new MusicSheet(
                                        0, 0, "", "", "", "", "",
                                        0, 0, "", "", "", "", "");

                                ((PianoApp) mContext.getApplicationContext()).currPlaySeq = vCurrPlaySeq + 1;

                            }
                        }

                        // Insert Button
                        if (x[0] >= vBtnXpos + 200 && y[0] >= vBtnYpos+100 && x[0] <= vBtnXpos + 300 && y[0] <= vBtnYpos + 200) {

                            if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Insert Note or Rest",Toast.LENGTH_LONG).show();

                            for (int inx = vArrayCount-2; vCurrPlaySeq <= inx; inx--) {

                                int vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNoteLength();
                                int vLowPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowPlayFlag();
                                String vLowNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote1();
                                String vLowNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote2();
                                String vLowNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote3();
                                String vLowNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote4();
                                String vLowNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNoteTie();
                                int vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNoteLength();
                                int vHighPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighPlayFlag();
                                String vHighNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote1();
                                String vHighNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote2();
                                String vHighNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote3();
                                String vHighNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote4();
                                String vHighNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNoteTie();

                                ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1] = new MusicSheet(
                                       vLowNoteLength, vLowPlayFlag, vLowNote1, vLowNote2, vLowNote3, vLowNote4, vLowNoteTie,
                                        vHighNoteLength, vHighPlayFlag, vHighNote1, vHighNote2, vHighNote3, vHighNote4, vHighNoteTie);

                            }

                            ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq] = new MusicSheet(
                                    0, 0, "", "", "", "", "",
                                    0, 0, "", "", "", "", "");

                        }

                        // -----------------------------
                        // Layer 3
                        // -----------------------------
                        // Music Sheet Button
                        if (x[0] >= vBtnXpos - 100 && y[0] >= vBtnYpos + 200 && x[0] <= vBtnXpos  && y[0] <= vBtnYpos + 200 + 100) {

                            if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Toggle Treble/Bass Staff",Toast.LENGTH_LONG).show();

                            if (((PianoApp)mContext.getApplicationContext()).makeMusicSheet.equals("HIGH")) {
                                if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Bass Staff",Toast.LENGTH_LONG).show();
                                ((PianoApp)mContext.getApplicationContext()).makeMusicSheet = "LOW";
                            } else {
                                if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Treble Staff",Toast.LENGTH_LONG).show();
                                ((PianoApp)mContext.getApplicationContext()).makeMusicSheet = "HIGH";
                            }
                        }


                        // 음표 Button
                        if (x[0] >= vBtnXpos && y[0] >= vBtnYpos + 200 && x[0] <= vBtnXpos + 100 && y[0] <= vBtnYpos + 200 + 100) {

                            if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Change Note Symbol",Toast.LENGTH_LONG).show();

                            int vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNoteLength();
                            int vLowPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowPlayFlag();
                            String vLowNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNote1();
                            String vLowNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNote2();
                            String vLowNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNote3();
                            String vLowNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNote4();
                            String vLowNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNoteTie();
                            int vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNoteLength();
                            int vHighPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighPlayFlag();
                            String vHighNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNote1();
                            String vHighNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNote2();
                            String vHighNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNote3();
                            String vHighNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNote4();
                            String vHighNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNoteTie();

                            if (((PianoApp)mContext.getApplicationContext()).makeMusicSheet.equals("HIGH")) {
                                vHighNoteLength = getNextSymbol(vHighNoteLength);
                                ((PianoApp) mContext.getApplicationContext()).noteLength = vHighNoteLength;
                                vHighPlayFlag = 1;
                            } else {
                                vLowNoteLength = getNextSymbol(vLowNoteLength);
                                ((PianoApp) mContext.getApplicationContext()).noteLength = vLowNoteLength;
                                vLowPlayFlag = 1;
                            }

                            ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq] = new MusicSheet(
                                     vLowNoteLength, vLowPlayFlag, vLowNote1, vLowNote2, vLowNote3, vLowNote4, vLowNoteTie,
                                    vHighNoteLength, vHighPlayFlag, vHighNote1, vHighNote2, vHighNote3, vHighNote4, vHighNoteTie);

                        }

                        // 쉼표 Button
                        if (x[0] >= vBtnXpos + 100 && y[0] >= vBtnYpos + 200 && x[0] <= vBtnXpos + 100 + 100 && y[0] <= vBtnYpos + 200 + 100) {

                            if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Change Rest Symbol",Toast.LENGTH_LONG).show();

                            int vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNoteLength();
                            int vLowPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowPlayFlag();
                            String vLowNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNote1();
                            String vLowNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNote2();
                            String vLowNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNote3();
                            String vLowNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNote4();
                            String vLowNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNoteTie();
                            int vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNoteLength();
                            int vHighPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighPlayFlag();
                            String vHighNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNote1();
                            String vHighNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNote2();
                            String vHighNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNote3();
                            String vHighNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNote4();
                            String vHighNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNoteTie();

                            if (((PianoApp)mContext.getApplicationContext()).makeMusicSheet.equals("HIGH")) {
                                vHighNoteLength = getNextWait(vHighNoteLength);
                                ((PianoApp) mContext.getApplicationContext()).restLength = vHighNoteLength;
                                vHighPlayFlag = 0;
                            } else {
                                vLowNoteLength = getNextWait(vLowNoteLength);
                                ((PianoApp) mContext.getApplicationContext()).restLength = vLowNoteLength;
                                vLowPlayFlag = 0;
                            }

                            ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq] = new MusicSheet(
                                    vLowNoteLength, vLowPlayFlag, vLowNote1, vLowNote2, vLowNote3, vLowNote4, vLowNoteTie,
                                    vHighNoteLength, vHighPlayFlag, vHighNote1, vHighNote2, vHighNote3, vHighNote4, vHighNoteTie);

                        }

                        // 라인 Button
                        if (x[0] >= vBtnXpos + 200 && y[0] >= vBtnYpos + 200 && x[0] <= vBtnXpos + 200 + 100 && y[0] <= vBtnYpos + 200 + 100) {

                            if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Insert Bar Line",Toast.LENGTH_LONG).show();

                            ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq] = new MusicSheet(
                                    0, 2, "", "", "", "", "",
                                    0, 2, "", "", "", "", "");

                            if ( vArrayCount - 1 >vCurrPlaySeq + 1 ) ((PianoApp)mContext.getApplicationContext()).currPlaySeq = vCurrPlaySeq + 1;

                        }

                        // -----------------------------
                        // Layer 4
                        // -----------------------------
                        // Lock Button
                        if (x[0] >= vBtnXpos-100 && y[0] >= vBtnYpos + 300 && x[0] <= vBtnXpos && y[0] <= vBtnYpos + 300 + 100) {

                            if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Lock/Unlock Auto Step",Toast.LENGTH_LONG).show();

                            if (((PianoApp) mContext.getApplicationContext()).modeLock.equals("UNLOCK")) {
                                if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Lock",Toast.LENGTH_LONG).show();
                                ((PianoApp) mContext.getApplicationContext()).modeLock = "LOCK";
                            } else {
                                if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Unlock",Toast.LENGTH_LONG).show();
                                ((PianoApp) mContext.getApplicationContext()).modeLock = "UNLOCK";
                            }
                        }

                        // Tie Sign Button
                        if (x[0] >= vBtnXpos && y[0] >= vBtnYpos + 300 && x[0] <= vBtnXpos + 100 && y[0] <= vBtnYpos + 300 + 100) {


                            if (((PianoApp)mContext.getApplicationContext()).makeMusicSheet.equals("HIGH")) {
                                String v_note_tie = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getHighNoteTie();
                                if (v_note_tie == null || !v_note_tie.equals("T")) {
                                    ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].setHighNoteTie("T");
                                } else {
                                    ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].setHighNoteTie("");
                                }
                            } else {
                                String v_note_tie = ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].getLowNoteTie();
                                if (v_note_tie == null || !v_note_tie.equals("T")) {
                                    ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].setLowNoteTie("T");
                                } else {
                                    ((PianoApp) mContext.getApplicationContext()).musicTable[vCurrPlaySeq].setLowNoteTie("");
                                }
                            }

                        }

                        // Delete Button
                        if (x[0] >= vBtnXpos + 100 && y[0] >= vBtnYpos+300 && x[0] <= vBtnXpos + 200 && y[0] <= vBtnYpos + 300 + 100) {

                            if (((PianoApp) mContext.getApplicationContext()).helpFlag) Toast.makeText(mContext, "Delete Note or Rest",Toast.LENGTH_LONG).show();

                            for (int inx = vCurrPlaySeq; inx < (vArrayCount-2); inx++) {

                                int vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getLowNoteLength();
                                int vLowPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getLowPlayFlag();
                                String vLowNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getLowNote1();
                                String vLowNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getLowNote2();
                                String vLowNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getLowNote3();
                                String vLowNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getLowNote4();
                                String vLowNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getLowNoteTie();
                                int vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getHighNoteLength();
                                int vHighPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getHighPlayFlag();
                                String vHighNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getHighNote1();
                                String vHighNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getHighNote2();
                                String vHighNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getHighNote3();
                                String vHighNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getHighNote4();
                                String vHighNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[inx + 1].getHighNoteTie();

                                ((PianoApp) mContext.getApplicationContext()).musicTable[inx] = new MusicSheet(
                                        vLowNoteLength, vLowPlayFlag, vLowNote1, vLowNote2, vLowNote3, vLowNote4, vLowNoteTie,
                                        vHighNoteLength, vHighPlayFlag, vHighNote1, vHighNote2, vHighNote3, vHighNote4, vHighNoteTie);

                            }

                            ((PianoApp) mContext.getApplicationContext()).musicTable[vArrayCount - 1] = new MusicSheet(
                                    0, 0, "", "", "", "", "",
                                    0, 0, "", "", "", "", "");
                        }

                        // Key Sign Button
                        if (x[0] >= vBtnXpos + 200 && y[0] >= vBtnYpos + 300 && x[0] <= vBtnXpos + 200 + 100 && y[0] <= vBtnYpos + 300 + 100) {

                            String vKeySign = ((PianoApp)mContext.getApplicationContext()).playKeySign;
                            String vNextKeySign = getNextKeySign(vKeySign);
                            ((PianoApp)mContext.getApplicationContext()).playKeySign = vNextKeySign;
                            Toast.makeText(mContext, "Key Sign - "+vNextKeySign,Toast.LENGTH_LONG).show();
                        }

                    } // MAKE



                } // key

                break;

            case MotionEvent.ACTION_POINTER_DOWN: //두 개 이상의 포인트에 대한 DOWN을 얻을 때.

                int currSeq = ((PianoApp)mContext.getApplicationContext()).currPlaySeq;

                String musicNote1 = "";
                String musicNote2 = "";
                String musicNote3 = "";
                String musicNote4 = "";

                for(int i = 0; i < pointer_count; i++) {

                    id[i] = event.getPointerId(i);
                    x[i] = (int) (event.getX(i));
                    y[i] = (int) (event.getY(i));

                    key = ((PianoApp)mContext.getApplicationContext()).keyForCoords(x[i],y[i]);

                    if (key != null) {
                        key.setDown(true);

                        String musicNote = SoundMap.getKeyNote(key.getSound());

                        if (i == 0 ) {
                            musicNote1 = musicNote;
                        } else if (i == 1 ) {
                            musicNote2 = musicNote;
                        } else if (i == 2 ) {
                            musicNote3 = musicNote;
                        } else if (i == 3 ) {
                            musicNote4 = musicNote;
                        }

                    }
                } // for

                if (!musicNote1.isEmpty()) {

                    if (((PianoApp)mContext.getApplicationContext()).modeMakePlay.equals("MAKE")) {

                        if (((PianoApp)mContext.getApplicationContext()).makeMusicSheet.equals("HIGH")) {

                            int vLowNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNoteLength();
                            int vLowPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowPlayFlag();
                            String vLowNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote1();
                            String vLowNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote2();
                            String vLowNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote3();
                            String vLowNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNote4();
                            String vLowNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getLowNoteTie();

                            ((PianoApp)mContext.getApplicationContext()).musicTable[currSeq] =  new MusicSheet(
                                    vLowNoteLength, vLowPlayFlag,   vLowNote1, vLowNote2, vLowNote3, vLowNote4, vLowNoteTie,
                                    1000, 1, musicNote1, musicNote2, musicNote3, musicNote4, "");

                        } else {

                            int vHighNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNoteLength();
                            int vHighPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighPlayFlag();
                            String vHighNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote1();
                            String vHighNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote2();
                            String vHighNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote3();
                            String vHighNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNote4();
                            String vHighNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[currSeq].getHighNoteTie();

                            ((PianoApp)mContext.getApplicationContext()).musicTable[currSeq] =  new MusicSheet(
                                    1000, 1,  musicNote1, musicNote2, musicNote3, musicNote4, "",
                                    vHighNoteLength, vHighPlayFlag,   vHighNote1, vHighNote2, vHighNote3, vHighNote4, vHighNoteTie);

                        }

                        if (vArrayCount - 1 > currSeq + 1 ) ((PianoApp)mContext.getApplicationContext()).currPlaySeq = currSeq + 1;

                    }      //MAKE

                } // isEmpty

                break;

            case MotionEvent.ACTION_MOVE:

                for(int i = 0; i < pointer_count; i++) {

                    id[i] = event.getPointerId(i);
                    x[i] = (int) (event.getX(i));
                    y[i] = (int) (event.getY(i));

                    key = ((PianoApp)mContext.getApplicationContext()).keyForCoords(x[i],y[i]);

                    if (key != null) {
                        key.setDown(true);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                for(int i = 0; i < pointer_count; i++) {

                    id[i] = event.getPointerId(i);
                    x[i] = (int) (event.getX(i));
                    y[i] = (int) (event.getY(i));

                    key = ((PianoApp)mContext.getApplicationContext()).keyForCoords(x[i],y[i]);

                    if (key != null) {
                        key.setDown(false);
                    }
                }

                break;
        }

        // 연주하기
        playSound();

        return true;
    } // onTouchEvent


    public void playSound() {

        Vector<PianoKey> tmp = new Vector<>(((PianoApp)mContext.getApplicationContext()).getWhites());
        tmp.addAll(((PianoApp)mContext.getApplicationContext()).getBlacks());

        for (PianoKey key : tmp) {

            if (key.isDown()) {
                if (!keyPlayer.isNotePlaying(key.getSound())) {
                    keyPlayer.playNote(key.getSound(), 3000);
                } else {
                    releaseKey(key);
                }
            }

            if (!key.isDown()) {
                if (keyPlayer.isNotePlaying(key.getSound())) {
                    keyPlayer.stopNote(key.getSound());
                }
                releaseKey(key);
            }
        }
    }

    private void releaseKey(final PianoKey key) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                key.setDown(false);
                handler.sendEmptyMessage(0);
            }
        }, 100);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };


    private int getNextSymbol ( int symbol ) {

        int symbols[] = {0, 125, 188, 250, 375, 500, 725, 1000, 1500, 2000, 3000, 4000 };
        int checkSymbol = 0;

        for (int inx = 0; symbols.length > inx ; inx++) {
            if (symbols[inx] == symbol) {
                checkSymbol = inx;
                break;
            }
        }

        if ( checkSymbol < (symbols.length - 1) ) {
            return symbols[checkSymbol+1];
        } else {
            return symbols[0];
        }

    }

    private int getNextWait ( int symbol ) {

        int symbols[] = {0, 125, 250, 500, 1000, 2000, 4000 };
        int checkSymbol = 0;

        for (int inx = 0; symbols.length > inx ; inx++) {
            if (symbols[inx] == symbol) {
                checkSymbol = inx;
                break;
            }
        }

        if ( checkSymbol < (symbols.length - 1) ) {
            return symbols[checkSymbol+1];
        } else {
            return symbols[0];
        }

    }

    public void setSymbolDisplay() {

        int vPlaySeq = ((PianoApp)mContext.getApplicationContext()).currPlaySeq;

        if (((PianoApp)mContext.getApplicationContext()).makeMusicSheet.equals("HIGH")) {

            int vNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[vPlaySeq].getHighNoteLength();
            int vPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[vPlaySeq].getHighPlayFlag();

            if ( vNoteLength > 0 ) {
                if (vPlayFlag == 1 ) {
                    ((PianoApp) mContext.getApplicationContext()).noteLength = vNoteLength;
                } else  if (vPlayFlag == 0 ) {
                    ((PianoApp) mContext.getApplicationContext()).restLength = vNoteLength;
                }
            }

        } else {

            int vNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[vPlaySeq].getLowNoteLength();
            int vPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[vPlaySeq].getLowPlayFlag();

            if ( vNoteLength > 0 ) {
                if (vPlayFlag == 1 ) {
                    ((PianoApp) mContext.getApplicationContext()).noteLength = vNoteLength;
                } else if (vPlayFlag == 0 ) {
                    ((PianoApp) mContext.getApplicationContext()).restLength = vNoteLength;
                }
            }
        }

        // Key board
        setKeyDisplay();
    }

    public void setKeyDisplay() {

        ((PianoApp)mContext.getApplicationContext()).clearKeyDown();

        int vPlaySeq = ((PianoApp)mContext.getApplicationContext()).currPlaySeq;

        int highNoteLength = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getHighNoteLength();
        int highPlayFlag  = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getHighPlayFlag();
        int highNote1  = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getHighNoteNo1();
        int highNote2  = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getHighNoteNo2();
        int highNote3  = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getHighNoteNo3();
        int highNote4  = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getHighNoteNo4();

        if ( highNoteLength > 0 && highPlayFlag == 1) {
            if (highNote1 > 0) {
                ((PianoApp) mContext.getApplicationContext()).setKeyDown(SoundMap.getKeyNo(SoundMap.getSoundNote(highNote1)));
            }
            if (highNote2 > 0) {
                ((PianoApp) mContext.getApplicationContext()).setKeyDown(SoundMap.getKeyNo(SoundMap.getSoundNote(highNote2)));
            }
            if (highNote3 > 0) {
                ((PianoApp) mContext.getApplicationContext()).setKeyDown(SoundMap.getKeyNo(SoundMap.getSoundNote(highNote3)));
            }
            if (highNote4 > 0) {
                ((PianoApp) mContext.getApplicationContext()).setKeyDown(SoundMap.getKeyNo(SoundMap.getSoundNote(highNote4)));
            }
        }

        int lowNoteLength = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getLowNoteLength();
        int lowPlayFlag  = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getLowPlayFlag();
        int lowNote1  = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getLowNoteNo1();
        int lowNote2  = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getLowNoteNo2();
        int lowNote3  = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getLowNoteNo3();
        int lowNote4  = ((PianoApp)mContext.getApplicationContext()).musicTable[vPlaySeq].getLowNoteNo4();

        if ( lowNoteLength > 0 && lowPlayFlag == 1) {
            if (lowNote1 > 0) {
                ((PianoApp) mContext.getApplicationContext()).setKeyDown(SoundMap.getKeyNo(SoundMap.getSoundNote(lowNote1)));
            }
            if (lowNote2 > 0) {
                ((PianoApp) mContext.getApplicationContext()).setKeyDown(SoundMap.getKeyNo(SoundMap.getSoundNote(lowNote2)));
            }
            if (lowNote3 > 0) {
                ((PianoApp) mContext.getApplicationContext()).setKeyDown(SoundMap.getKeyNo(SoundMap.getSoundNote(lowNote3)));
            }
            if (lowNote4 > 0) {
                ((PianoApp) mContext.getApplicationContext()).setKeyDown(SoundMap.getKeyNo(SoundMap.getSoundNote(lowNote4)));
            }
        }

    }

    private String getNextKeySign ( String keySign ) {

        String keySigns[] = {"X","Gm","Dm","Am","Fm","Bb","Eb"};
        int checkSymbol = 0;

        for (int inx = 0; keySigns.length > inx ; inx++) {
            if (keySigns[inx] == keySign) {
                checkSymbol = inx;
                break;
            }
        }

        if ( checkSymbol < (keySigns.length - 1) ) {
            return keySigns[checkSymbol+1];
        } else {
            return keySigns[0];
        }

    }


    private float getNextPlaySpeed ( float speed ) {

        float speeds[] = { 1.0f, 1.2f, 1.5f, 1.8f, 2.1f, 2.4f, 2.7f, 3.0f, 3.3f, 3.6f, 4.0f, 4.5f, 5.0f };
        int checkCount = 0;

        for (int inx = 0; speeds.length > inx ; inx++) {
            if (speeds[inx] == speed) {
                checkCount = inx;
                break;
            }
        }

        if ( checkCount < (speeds.length - 1) ) {
            return speeds[checkCount+1];
        } else {
            return speeds[0];
        }

    }

    private float getPrevPlaySpeed ( float speed ) {

        float speeds[] = { 1.0f, 1.2f, 1.5f, 1.8f, 2.1f, 2.4f, 2.7f, 3.0f, 3.3f, 3.6f, 4.0f, 4.5f, 5.0f };
        int checkCount = 0;

        for (int inx = 0; speeds.length > inx ; inx++) {
            if (speeds[inx] == speed) {
                checkCount = inx;
                break;
            }
        }

        if ( checkCount == 0 ) {
            return speeds[speeds.length - 1];
        } else {
            return speeds[checkCount-1];
        }

    }


}
