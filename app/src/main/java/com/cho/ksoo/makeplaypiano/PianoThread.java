package com.cho.ksoo.makeplaypiano;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import java.lang.reflect.Field;

// -----------------------------
// Thread
// -----------------------------
class PianoThread extends Thread {

    SurfaceHolder mHolder; // SurfaceHolder를 저장할 변수
    Context mContext;

    int viewWidth;    // View 폭
    int viewHeight;	  // View 높이

    //boolean canRun = true;
    boolean isWait = false;

    public static final int NB = 28;

    private Paint black, yellow, white;
    private Paint blackLine, redLine;
    private Paint blackText;
    private int keyWidth, keyHeight;
    int vPianoHeight;
    private Paint redBox;

    private boolean canvasLocked;

    // -----------------------------
    // 생성자
    // -----------------------------
    public PianoThread(SurfaceHolder holder, Context context) {

        mHolder = holder;    // SurfaceHolder 보존
        mContext = context;

        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        viewWidth = display.getWidth();        // View의 가로 폭
        viewHeight = display.getHeight() - 250;   // View의 세로 높이

        // View의 크기로 주요 시작점 결정함.
        int vMusicSheetHeight = ((PianoApp)mContext.getApplicationContext()).musicSheetHeight;
        int vControlWidth = ((PianoApp)mContext.getApplicationContext()).controlWidth;

        // 악보영역 결정
        int vMusicSheetWidth = viewWidth - vControlWidth - 160;
        ((PianoApp)mContext.getApplicationContext()).musicSheetWidth = vMusicSheetWidth;
        ((PianoApp)mContext.getApplicationContext()).displayCount = (int)vMusicSheetWidth/80;

        int vHighNoteYPos = viewHeight - vMusicSheetHeight;
        ((PianoApp)mContext.getApplicationContext()).btnHighNoteYPos = vHighNoteYPos ;

        // 피아노 영역 결정
        vPianoHeight = viewHeight - vMusicSheetHeight - 50;

        // 버튼 Control 영역 결정
        int vControlXPos = viewWidth - vControlWidth ;
        int vControlYPos = viewHeight - vMusicSheetHeight + 80;
        ((PianoApp)mContext.getApplicationContext()).btnControlXPos = vControlXPos ;
        ((PianoApp)mContext.getApplicationContext()).btnControlYPos = vControlYPos ;

        // 피아노 기본 값 지정
        black = new Paint();
        black.setColor(Color.BLACK);

        white = new Paint();
        white.setColor(Color.WHITE);
        white.setStyle(Paint.Style.FILL);

        yellow = new Paint();
        yellow.setColor(Color.YELLOW);
        yellow.setStyle(Paint.Style.FILL);

        // Music Line
        blackLine = new Paint();
        blackLine.setColor(Color.DKGRAY);
        blackLine.setStrokeWidth(2); //선의 굵기
        blackLine.setAntiAlias(true);//경계면을 부드럽게 처리하기

        redLine = new Paint();
        redLine.setColor(Color.RED);
        redLine.setStrokeWidth(3); //선의 굵기
        redLine.setAntiAlias(true);//경계면을 부드럽게 처리하기

        // Red Box
        redBox = new Paint();
        redBox.setStyle(Paint.Style.STROKE);
        redBox.setColor(Color.RED);
        redBox.setStrokeWidth(3);

        // Flat Display
        blackText = new Paint();
        blackText.setStyle(Paint.Style.FILL);
        blackText.setColor(Color.BLACK);
        blackText.setTextSize(9);
        blackText.setTextAlign(Paint.Align.CENTER);
        // -----------------------------------------
        // 피아노 건반 그리기
        // -----------------------------------------
        ((PianoApp)mContext.getApplicationContext()).getWhites().clear();
        ((PianoApp)mContext.getApplicationContext()).getBlacks().clear();

        keyWidth = viewWidth / NB;

        keyHeight = vPianoHeight;

        int count = 29;

        for (int i = 0; i < NB; i++) {
            int left = i * keyWidth;
            int right = left + keyWidth;

            if (i == NB - 1) {
                right = viewWidth;
            }

            RectF rect = new RectF(left, 0, right, vPianoHeight);
            ((PianoApp)mContext.getApplicationContext()).getWhites().add(new PianoKey(rect, i + 1));

            if (i != 0  &&   i != 3  &&  i != 7  &&  i != 10 &&
                    i != 14  &&   i != 17  &&  i != 21  &&  i != 24  ) {

                rect = new RectF((float) (i - 1) * keyWidth + 0.5f * keyWidth + 0.15f * keyWidth,
                                0,
                               (float) i * keyWidth + 0.35f * keyWidth,
                             0.6f * keyHeight);

                ((PianoApp)mContext.getApplicationContext()).getBlacks().add(new PianoKey(rect, count));
                count++;
            }
        }

    }

    //-------------------------------------
    //  Canvas에 그리기
    //-------------------------------------
    public void run() {

        Surface surface = mHolder.getSurface();

        if (mHolder == null || surface == null) {
            return;
        }

        if(!surface.isValid()){
            return;
        }

        Canvas canvas = null; 					// canvas를 만든다

        while (!Thread.currentThread().isInterrupted()) {  // canRun

            try {

                if (!canvasLocked) {

                    canvas = mHolder.lockCanvas(null);        // canvas를 잠그고 버퍼 할당
                    canvasLocked = true;

                    synchronized (mHolder) {        // 동기화 유지

                        if (canvas != null) {
                            canvas.save();
                            canvas.drawColor(Color.WHITE);
                        }

                        // 건반이 눌린 상태 / 안눌린 상태 표시
                        for (PianoKey key : ((PianoApp) mContext.getApplicationContext()).getWhites()) {
                            if (key != null && canvas != null) {
                                canvas.drawRect(key.getRect(), key.isDown() ? yellow : white);
                            }
                        }

                        for (int i = 1; i < NB; i++) {
                            if (canvas != null) {
                                canvas.drawLine(i * keyWidth, 0, i * keyWidth, vPianoHeight, black);
                            }
                        }

                        for (PianoKey key : ((PianoApp) mContext.getApplicationContext()).getBlacks()) {
                            if (key != null && canvas != null) {
                                canvas.drawRect(key.getRect(), key.isDown() ? yellow : black);
                            }
                        }

                        if (canvas != null) {

                            canvas.drawLine(0, vPianoHeight, viewWidth, vPianoHeight, black);

                            // -----------------------------------------
                            // 음자리표 그리기
                            // -----------------------------------------
                            Bitmap imgHighNote = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.symbol_high_note);
                            Bitmap imgLowNote = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.symbol_low_note);

                            int imgMainXPos = ((PianoApp) mContext.getApplicationContext()).btnHighNoteXPos;
                            int imgMainYPos = ((PianoApp) mContext.getApplicationContext()).btnHighNoteYPos;

                            // 높은 음자리표
                            canvas.drawBitmap(imgHighNote, imgMainXPos, imgMainYPos, null);

                            // 낮은 음자리표
                            canvas.drawBitmap(imgLowNote, imgMainXPos, imgMainYPos + 250, null);

                            // Key Sign 그리기
                            String playKeySign = ((PianoApp)mContext.getApplicationContext()).playKeySign;

                            Bitmap imgKeySignFlat = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.symbol_flat);
                            Bitmap imgKeySignSharp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.symbol_sharp);

                            if (playKeySign.equals("Gm") ) {

                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100, imgMainYPos, null);
                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100, imgMainYPos + 280, null);

                            } else if (playKeySign.equals("Dm") ) {
                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100, imgMainYPos, null);
                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100, imgMainYPos + 280, null);

                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100+20, imgMainYPos+60, null);
                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100+20, imgMainYPos + 280 +60, null);

                            } else if (playKeySign.equals("Am")) {

                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100, imgMainYPos, null);
                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100, imgMainYPos + 280, null);

                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100+20, imgMainYPos+60, null);
                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100+20, imgMainYPos + 280 +60, null);

                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100+40, imgMainYPos-20, null);
                                canvas.drawBitmap(imgKeySignSharp, imgMainXPos+100+40, imgMainYPos + 280 -20, null);

                            } else if (playKeySign.equals("Fm")) {
                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100, imgMainYPos+70, null);
                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100, imgMainYPos + 280 +70, null);

                            } else if (playKeySign.equals("Bb")) {
                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100, imgMainYPos+70, null);
                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100, imgMainYPos + 280+70, null);

                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100+20, imgMainYPos+20, null);
                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100+20, imgMainYPos + 280 +20, null);

                            } else if (playKeySign.equals("Eb")) {
                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100, imgMainYPos+70, null);
                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100, imgMainYPos + 280+70, null);

                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100+20, imgMainYPos+20, null);
                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100+20, imgMainYPos + 280 +20, null);

                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100+40, imgMainYPos+90, null);
                                canvas.drawBitmap(imgKeySignFlat, imgMainXPos+100+40, imgMainYPos + 280 +90, null);

                            }

                            // 음표/쉼표 그리기
                            int lowBase = SoundMap.getSoundNo("C4");
                            int highBase = SoundMap.getSoundNo("A5");

                            int highBasePos = 27;
                            int lowBasePos = 15;

                            // -----------------------------------------
                            // 악보 그리기
                            // -----------------------------------------
                            int symbolStartPos = imgMainXPos + 300;
                            int symbolHighBase = imgMainYPos - 25;
                            int symbolLowBase = imgMainYPos + 215;

                            //Music Sheet 높은 음자리 음표그리기

                            int lowPlayFlag = 0;
                            String lowNote1;
                            String lowNote2;
                            String lowNote3;
                            String lowNote4;
                            int    lowNoteLength;

                            int highPlayFlag = 0;
                            String highNote1;
                            String highNote2;
                            String highNote3;
                            String highNote4;
                            int    highNoteLength;

                            if (((PianoApp) mContext.getApplicationContext()).musicTable.length > 0) {

                                int vDispCount = ((PianoApp) mContext.getApplicationContext()).displayCount;
                                int vCurrPlaySeq = ((PianoApp) mContext.getApplicationContext()).currPlaySeq;
                                int vArrayCount = ((PianoApp) mContext.getApplicationContext()).musicTable.length;

                                int vArrayStart = (vCurrPlaySeq / vDispCount) * vDispCount;
                                int vArrayEnd = vArrayStart + vDispCount;

                                if (vArrayCount < vArrayEnd) {
                                    vArrayEnd = vArrayCount;
                                }

                                for (int inx = vArrayStart; inx < vArrayEnd; inx++) {

                                    int vStartPos = symbolStartPos + ((inx % vDispCount) - 1) * 70;

                                    // ------------------------------------------
                                    //  High Note
                                    // ------------------------------------------
                                    boolean lowNoteFlat1 = false;
                                    boolean lowNoteFlat2 = false;
                                    boolean lowNoteFlat3 = false;
                                    boolean lowNoteFlat4 = false;
                                    String lowSymbolImage = "";
                                    String lowNoteImage1 = "";
                                    String lowNoteImage2 = "";
                                    String lowNoteImage3 = "";
                                    String lowNoteImage4 = "";
                                    int lowNotePos1 = 0;
                                    int lowNotePos2 = 0;
                                    int lowNotePos3 = 0;
                                    int lowNotePos4 = 0;
                                    String lowDownUp = "up";
                                    String lowNoteTie = "";
                                    boolean lowNoteTieFlag = false;

                                    lowPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowPlayFlag();
                                    lowNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNoteLength();

                                    if (lowPlayFlag == 1) {

                                        lowNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote1();
                                        lowNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote2();
                                        lowNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote3();
                                        lowNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNote4();

                                        // 장조에 따른 음표변환
                                        // PlayFlag - 1 음표, 0 쉼표

                                        // ----------------------------------------------------------
                                        // 장조에 따른 노트변환 ( 음은 변경되지 않음 )
                                        // 사# ( Gb -> F ) 라## (Db -> C) 가### (Ab -> G)
                                        // 바b ( Bb -> B ) 나bb (Eb -> E) 마bbb (Ab -> A)
                                        // 변경된 노트에 대한 NoteNo
                                        // ----------------------------------------------------------
                                        String keyLowNote1 = lowNote1;
                                        String keyLowNote2 = lowNote2;
                                        String keyLowNote3 = lowNote3;
                                        String keyLowNote4 = lowNote4;

                                        // #
                                        if (playKeySign.equals("Gm") || playKeySign.equals("Dm") || playKeySign.equals("Am")) {
                                            keyLowNote1 = keyLowNote1.replace("Gb","F");
                                            keyLowNote2 = keyLowNote2.replace("Gb","F");
                                            keyLowNote3 = keyLowNote3.replace("Gb","F");
                                            keyLowNote4 = keyLowNote4.replace("Gb","F");
                                        }

                                        if (playKeySign.equals("Dm") || playKeySign.equals("Am")) {
                                            keyLowNote1 = keyLowNote1.replace("Db","C");
                                            keyLowNote2 = keyLowNote2.replace("Db","C");
                                            keyLowNote3 = keyLowNote3.replace("Db","C");
                                            keyLowNote4 = keyLowNote4.replace("Db","C");
                                        }

                                        if (playKeySign.equals("Am")) {
                                            keyLowNote1 = keyLowNote1.replace("Ab","G");
                                            keyLowNote2 = keyLowNote2.replace("Ab","G");
                                            keyLowNote3 = keyLowNote3.replace("Ab","G");
                                            keyLowNote4 = keyLowNote4.replace("Ab","G");
                                        }

                                        // b
                                        if (playKeySign.equals("Fm") || playKeySign.equals("Bb") || playKeySign.equals("Eb")) {
                                            keyLowNote1 = keyLowNote1.replace("Bb","B");
                                            keyLowNote2 = keyLowNote2.replace("Bb","B");
                                            keyLowNote3 = keyLowNote3.replace("Bb","B");
                                            keyLowNote4 = keyLowNote4.replace("Bb","B");
                                        }

                                        if (playKeySign.equals("Bb") || playKeySign.equals("Eb")) {
                                            keyLowNote1 = keyLowNote1.replace("Eb","E");
                                            keyLowNote2 = keyLowNote2.replace("Eb","E");
                                            keyLowNote3 = keyLowNote3.replace("Eb","E");
                                            keyLowNote4 = keyLowNote4.replace("Eb","E");
                                        }

                                        if (playKeySign.equals("Eb")) {
                                            keyLowNote1 = keyLowNote1.replace("Ab","A");
                                            keyLowNote2 = keyLowNote2.replace("Ab","A");
                                            keyLowNote3 = keyLowNote3.replace("Ab","A");
                                            keyLowNote4 = keyLowNote4.replace("Ab","A");
                                        }

                                        int keyLowNoteNo1 = SoundMap.getSoundNo(keyLowNote1);
                                        int keyLowNoteNo2 = SoundMap.getSoundNo(keyLowNote2);
                                        int keyLowNoteNo3 = SoundMap.getSoundNo(keyLowNote3);
                                        int keyLowNoteNo4 = SoundMap.getSoundNo(keyLowNote4);

                                        lowNoteFlat1 = keyLowNote1.contains("b");
                                        lowNoteFlat2 = keyLowNote2.contains("b");
                                        lowNoteFlat3 = keyLowNote3.contains("b");
                                        lowNoteFlat4 = keyLowNote4.contains("b");

                                        lowNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getLowNoteTie();

                                        if (lowNoteTie == null){
                                            lowNoteTieFlag = false;
                                        } else {
                                            lowNoteTieFlag = lowNoteTie.contains("T");;
                                        }

                                        // 4개음 중 하나라도 D4보다 크면 down
                                        if ( (!TextUtils.isEmpty(lowNote1)) && lowBase > keyLowNoteNo1 ) {
                                            lowDownUp = "down";
                                        }

                                        if (!lowDownUp.equals("down")) {
                                            if ( (!TextUtils.isEmpty(lowNote2)) && lowBase > keyLowNoteNo2 ) {
                                                lowDownUp = "down";
                                            }
                                        }

                                        if (!lowDownUp.equals("down")) {
                                            if ((!TextUtils.isEmpty(lowNote3)) && lowBase > keyLowNoteNo3 ) {
                                                lowDownUp = "down";
                                            }
                                        }

                                        if (!lowDownUp.equals("down")) {
                                            if ((!TextUtils.isEmpty(lowNote4)) && lowBase > keyLowNoteNo4 ) {
                                                lowDownUp = "down";
                                            }
                                        }

                                        if (!TextUtils.isEmpty(lowNote1)) {
                                            lowNoteImage1 = "symbol_"+String.valueOf(lowNoteLength)+"_"+lowDownUp;

                                            if ( lowDownUp.equals("down")) {
                                                lowNotePos1 = symbolLowBase + (lowBasePos - SoundMap.getKeyNo(keyLowNote1.replace("b",""))) * 20;
                                            } else {
                                                lowNotePos1 = symbolLowBase + (lowBasePos - SoundMap.getKeyNo(keyLowNote1.replace("b",""))) * 20 - 80;
                                            }
                                        }

                                        if (!TextUtils.isEmpty(lowNote2)) {
                                            lowNoteImage2 = "symbol_"+String.valueOf(lowNoteLength)+"_"+lowDownUp;

                                            if ( lowDownUp.equals("down")) {
                                                lowNotePos2 = symbolLowBase + (lowBasePos - SoundMap.getKeyNo(keyLowNote2.replace("b",""))) * 20;
                                            } else {
                                                lowNotePos2 = symbolLowBase + (lowBasePos - SoundMap.getKeyNo(keyLowNote2.replace("b",""))) * 20 - 80;
                                            }
                                        }

                                        if (!TextUtils.isEmpty(lowNote3)) {
                                            lowNoteImage3 = "symbol_"+String.valueOf(lowNoteLength)+"_"+lowDownUp;

                                            if ( lowDownUp.equals("down")) {
                                                lowNotePos3 = symbolLowBase + (lowBasePos - SoundMap.getKeyNo(keyLowNote3.replace("b",""))) * 20;
                                            } else {
                                                lowNotePos3 = symbolLowBase + (lowBasePos - SoundMap.getKeyNo(keyLowNote3.replace("b",""))) * 20 - 80;
                                            }
                                        }

                                        if (!TextUtils.isEmpty(lowNote4)) {
                                            lowNoteImage4 = "symbol_"+String.valueOf(lowNoteLength)+"_"+lowDownUp;

                                            if ( lowDownUp.equals("down")) {
                                                lowNotePos4 = symbolLowBase + (lowBasePos - SoundMap.getKeyNo(keyLowNote4.replace("b",""))) * 20;
                                            } else {
                                                lowNotePos4 = symbolLowBase + (lowBasePos - SoundMap.getKeyNo(keyLowNote4.replace("b",""))) * 20 - 80;
                                            }
                                        }

                                        if (!lowDownUp.equals("down")) {  //up

                                            int tempPos = 0;
                                            boolean v_note_tie_flag = false;

                                            if ( !lowNote1.isEmpty() && lowNoteLength > 0 && lowNotePos1 > tempPos ) tempPos = lowNotePos1;
                                            if ( !lowNote2.isEmpty() && lowNoteLength > 0 && lowNotePos2 > tempPos ) tempPos = lowNotePos2;
                                            if ( !lowNote3.isEmpty() && lowNoteLength > 0 && lowNotePos3 > tempPos ) tempPos = lowNotePos3;
                                            if ( !lowNote4.isEmpty() && lowNoteLength > 0 && lowNotePos4 > tempPos ) tempPos = lowNotePos4;

                                            if (!lowNote1.isEmpty() && lowNoteLength > 0) {
                                                if ( lowNotePos1 == tempPos ) {
                                                    v_note_tie_flag = lowNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, lowNotePos1, lowNoteImage1, lowNoteFlat1, v_note_tie_flag);
                                            }

                                            if (!lowNote2.isEmpty() && lowNoteLength > 0) {
                                                if ( lowNotePos2 == tempPos ) {
                                                    v_note_tie_flag = lowNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, lowNotePos2, lowNoteImage2, lowNoteFlat2, v_note_tie_flag);
                                            }

                                            if (!lowNote3.isEmpty() && lowNoteLength > 0) {
                                                if ( lowNotePos3 == tempPos ) {
                                                    v_note_tie_flag = lowNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, lowNotePos3, lowNoteImage3, lowNoteFlat3, v_note_tie_flag);
                                            }

                                            if (!lowNote4.isEmpty() && lowNoteLength > 0) {
                                                if ( lowNotePos4 == tempPos ) {
                                                    v_note_tie_flag = lowNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, lowNotePos4, lowNoteImage4, lowNoteFlat4, v_note_tie_flag);
                                            }

                                        } else {  //down

                                            int tempPos = 9999;
                                            boolean v_note_tie_flag = false;

                                            if ( !lowNote1.isEmpty() && lowNoteLength > 0 && lowNotePos1 < tempPos ) tempPos = lowNotePos1;
                                            if ( !lowNote2.isEmpty() && lowNoteLength > 0 && lowNotePos2 < tempPos ) tempPos = lowNotePos2;
                                            if ( !lowNote3.isEmpty() && lowNoteLength > 0 && lowNotePos3 < tempPos ) tempPos = lowNotePos3;
                                            if ( !lowNote4.isEmpty() && lowNoteLength > 0 && lowNotePos4 < tempPos ) tempPos = lowNotePos4;

                                            if (!lowNote4.isEmpty() && lowNoteLength > 0) {
                                                if ( lowNotePos4 == tempPos ) {
                                                    v_note_tie_flag = lowNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, lowNotePos4, lowNoteImage4, lowNoteFlat4, v_note_tie_flag);
                                            }

                                            if (!lowNote3.isEmpty() && lowNoteLength > 0) {
                                                if ( lowNotePos3 == tempPos ) {
                                                    v_note_tie_flag = lowNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, lowNotePos3, lowNoteImage3, lowNoteFlat3, v_note_tie_flag);
                                            }

                                            if (!lowNote2.isEmpty() && lowNoteLength > 0) {
                                                if ( lowNotePos2 == tempPos ) {
                                                    v_note_tie_flag = lowNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, lowNotePos2, lowNoteImage2, lowNoteFlat2, v_note_tie_flag);
                                            }

                                            if (!lowNote1.isEmpty() && lowNoteLength > 0) {
                                                if ( lowNotePos1 == tempPos ) {
                                                    v_note_tie_flag = lowNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, lowNotePos1, lowNoteImage1, lowNoteFlat1, v_note_tie_flag);
                                            }

                                        }

                                    } else if (lowPlayFlag == 0) {
                                        // 쉼표 그리기
                                        lowSymbolImage = "symbol_"+String.valueOf(lowNoteLength)+"_wait";
                                        displayMusicSymbol(inx, canvas, vStartPos, imgMainYPos + 250 + 60, lowSymbolImage, false, false);

                                    } else if (lowPlayFlag == 2) {
                                        // Line 그리기
                                        lowSymbolImage = "symbol_single";
                                        displayMusicSymbol(inx, canvas, vStartPos, imgMainYPos + 250 + 10, lowSymbolImage , false, false);

                                    } // playFlag

                                    // ------------------------------------------
                                    //  High Note
                                    // ------------------------------------------
                                    boolean highNoteFlat1 = false;
                                    boolean highNoteFlat2 = false;
                                    boolean highNoteFlat3 = false;
                                    boolean highNoteFlat4 = false;
                                    String highSymbolImage = "";
                                    String highNoteImage1 = "";
                                    String highNoteImage2 = "";
                                    String highNoteImage3 = "";
                                    String highNoteImage4 = "";
                                    int highNotePos1 = 0;
                                    int highNotePos2 = 0;
                                    int highNotePos3 = 0;
                                    int highNotePos4 = 0;
                                    String highDownUp = "up";
                                    String highNoteTie = "";
                                    boolean highNoteTieFlag = false;

                                    highPlayFlag = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighPlayFlag();
                                    highNoteLength = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNoteLength();

                                    if (highPlayFlag == 1) {

                                        // 음표 그리기
                                        highNote1 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote1();
                                        highNote2 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote2();
                                        highNote3 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote3();
                                        highNote4 = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNote4();

                                        // 장조에 따른 음표 변환
                                        // 장조에 따른 노트변환 ( 음은 변경되지 않음 )
                                        // 사# ( Gb -> F ) 라## (Db -> C) 가### (Ab -> G)
                                        // 바b ( Bb -> B ) 나bb (Eb -> E) 마bbb (Ab -> A)
                                        // 변경된 노트에 대한 NoteNo
                                        String keyHighNote1 = highNote1;
                                        String keyHighNote2 = highNote2;
                                        String keyHighNote3 = highNote3;
                                        String keyHighNote4 = highNote4;

                                        // #
                                        if (playKeySign.equals("Gm") || playKeySign.equals("Dm") || playKeySign.equals("Am")) {
                                            keyHighNote1 = keyHighNote1.replace("Gb","F");
                                            keyHighNote2 = keyHighNote2.replace("Gb","F");
                                            keyHighNote3 = keyHighNote3.replace("Gb","F");
                                            keyHighNote4 = keyHighNote4.replace("Gb","F");
                                        }

                                        if (playKeySign.equals("Dm") || playKeySign.equals("Am")) {
                                            keyHighNote1 = keyHighNote1.replace("Db","C");
                                            keyHighNote2 = keyHighNote2.replace("Db","C");
                                            keyHighNote3 = keyHighNote3.replace("Db","C");
                                            keyHighNote4 = keyHighNote4.replace("Db","C");
                                        }

                                        if (playKeySign.equals("Am")) {
                                            keyHighNote1 = keyHighNote1.replace("Ab","G");
                                            keyHighNote2 = keyHighNote2.replace("Ab","G");
                                            keyHighNote3 = keyHighNote3.replace("Ab","G");
                                            keyHighNote4 = keyHighNote4.replace("Ab","G");
                                        }

                                        // b
                                        if (playKeySign.equals("Fm") || playKeySign.equals("Bb") || playKeySign.equals("Eb")) {
                                            keyHighNote1 = keyHighNote1.replace("Bb","B");
                                            keyHighNote2 = keyHighNote2.replace("Bb","B");
                                            keyHighNote3 = keyHighNote3.replace("Bb","B");
                                            keyHighNote4 = keyHighNote4.replace("Bb","B");
                                        }

                                        if (playKeySign.equals("Bb") || playKeySign.equals("Eb")) {
                                            keyHighNote1 = keyHighNote1.replace("Eb","E");
                                            keyHighNote2 = keyHighNote2.replace("Eb","E");
                                            keyHighNote3 = keyHighNote3.replace("Eb","E");
                                            keyHighNote4 = keyHighNote4.replace("Eb","E");
                                        }

                                        if (playKeySign.equals("Eb")) {
                                            keyHighNote1 = keyHighNote1.replace("Ab","A");
                                            keyHighNote2 = keyHighNote2.replace("Ab","A");
                                            keyHighNote3 = keyHighNote3.replace("Ab","A");
                                            keyHighNote4 = keyHighNote4.replace("Ab","A");
                                        }

                                        int keyHighNoteNo1 = SoundMap.getSoundNo(keyHighNote1);
                                        int keyHighNoteNo2 = SoundMap.getSoundNo(keyHighNote2);
                                        int keyHighNoteNo3 = SoundMap.getSoundNo(keyHighNote3);
                                        int keyHighNoteNo4 = SoundMap.getSoundNo(keyHighNote4);


                                        highNoteFlat1 = keyHighNote1.contains("b");
                                        highNoteFlat2 = keyHighNote2.contains("b");
                                        highNoteFlat3 = keyHighNote3.contains("b");
                                        highNoteFlat4 = keyHighNote4.contains("b");

                                        highNoteTie = ((PianoApp) mContext.getApplicationContext()).musicTable[inx].getHighNoteTie();

                                        if ( highNoteTie == null) {
                                            highNoteTieFlag = false;
                                        } else {
                                            highNoteTieFlag = highNoteTie.contains("T");
                                        }

                                        // 4개음 중 하나라도 D4보다 크면 down
                                        if ( (!TextUtils.isEmpty(highNote1)) && highBase > keyHighNoteNo1 ) {
                                            highDownUp = "down";
                                        }

                                        if (!highDownUp.equals("down")) {
                                            if ( (!TextUtils.isEmpty(highNote2)) && highBase > keyHighNoteNo2 ) {
                                                highDownUp = "down";
                                            }
                                        }

                                        if (!highDownUp.equals("down")) {
                                            if ((!TextUtils.isEmpty(highNote3)) && highBase > keyHighNoteNo3 ) {
                                                highDownUp = "down";
                                            }
                                        }

                                        if (!highDownUp.equals("down")) {
                                            if ((!TextUtils.isEmpty(highNote4)) && highBase > keyHighNoteNo4 ) {
                                                highDownUp = "down";
                                            }
                                        }

                                        if (!TextUtils.isEmpty(highNote1)) {
                                            highNoteImage1 = "symbol_"+String.valueOf(highNoteLength)+"_"+highDownUp;

                                            if ( highDownUp.equals("down")) {
                                                highNotePos1 = symbolHighBase + (highBasePos - SoundMap.getKeyNo(keyHighNote1.replace("b",""))) * 20;
                                            } else {
                                                highNotePos1 = symbolHighBase + (highBasePos - SoundMap.getKeyNo(keyHighNote1.replace("b",""))) * 20 - 80;
                                            }
                                        }

                                        if (!TextUtils.isEmpty(highNote2)) {
                                            highNoteImage2 = "symbol_"+String.valueOf(highNoteLength)+"_"+highDownUp;

                                            if ( highDownUp.equals("down")) {
                                                highNotePos2 = symbolHighBase + (highBasePos - SoundMap.getKeyNo(keyHighNote2.replace("b",""))) * 20;
                                            } else {
                                                highNotePos2 = symbolHighBase + (highBasePos - SoundMap.getKeyNo(keyHighNote2.replace("b",""))) * 20 - 80;
                                            }
                                        }

                                        if (!TextUtils.isEmpty(highNote3)) {
                                            highNoteImage3 = "symbol_"+String.valueOf(highNoteLength)+"_"+highDownUp;

                                            if ( highDownUp.equals("down")) {
                                                highNotePos3 = symbolHighBase + (highBasePos - SoundMap.getKeyNo(keyHighNote3.replace("b",""))) * 20;
                                            } else {
                                                highNotePos3 = symbolHighBase + (highBasePos - SoundMap.getKeyNo(keyHighNote3.replace("b",""))) * 20 - 80;
                                            }
                                        }

                                        if (!TextUtils.isEmpty(highNote4)) {
                                            highNoteImage4 = "symbol_"+String.valueOf(highNoteLength)+"_"+highDownUp;

                                            if ( highDownUp.equals("down")) {
                                                highNotePos4 = symbolHighBase + (highBasePos - SoundMap.getKeyNo(keyHighNote4.replace("b",""))) * 20;
                                            } else {
                                                highNotePos4 = symbolHighBase + (highBasePos - SoundMap.getKeyNo(keyHighNote4.replace("b",""))) * 20 - 80;
                                            }
                                        }

                                        // Image Display 순서
                                        if (!highDownUp.equals("down")) {  //up

                                            int tempPos = 0;
                                            boolean v_note_tie_flag = false;

                                            if ( !highNote1.isEmpty() && highNoteLength > 0 && highNotePos1 > tempPos ) tempPos = highNotePos1;
                                            if ( !highNote2.isEmpty() && highNoteLength > 0 && highNotePos2 > tempPos ) tempPos = highNotePos2;
                                            if ( !highNote3.isEmpty() && highNoteLength > 0 && highNotePos3 > tempPos ) tempPos = highNotePos3;
                                            if ( !highNote4.isEmpty() && highNoteLength > 0 && highNotePos4 > tempPos ) tempPos = highNotePos4;

                                            if (!highNote1.isEmpty() && highNoteLength > 0) {
                                                if ( highNotePos1 == tempPos ) {
                                                    v_note_tie_flag = highNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, highNotePos1, highNoteImage1, highNoteFlat1, v_note_tie_flag);
                                            }

                                            if (!highNote2.isEmpty() && highNoteLength > 0) {
                                                if ( highNotePos2 == tempPos ) {
                                                    v_note_tie_flag = highNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, highNotePos2, highNoteImage2, highNoteFlat2, v_note_tie_flag);
                                            }

                                            if (!highNote3.isEmpty() && highNoteLength > 0) {
                                                if ( highNotePos3 == tempPos ) {
                                                    v_note_tie_flag = highNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, highNotePos3, highNoteImage3, highNoteFlat3, v_note_tie_flag);
                                            }

                                            if (!highNote4.isEmpty() && highNoteLength > 0) {
                                                if ( highNotePos4 == tempPos ) {
                                                    v_note_tie_flag = highNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, highNotePos4, highNoteImage4, highNoteFlat4, v_note_tie_flag);
                                            }

                                        } else {  //down

                                            int tempPos = 9999;
                                            boolean v_note_tie_flag = false;

                                            if ( !highNote1.isEmpty() && highNoteLength > 0 && highNotePos1 < tempPos ) tempPos = highNotePos1;
                                            if ( !highNote2.isEmpty() && highNoteLength > 0 && highNotePos2 < tempPos ) tempPos = highNotePos2;
                                            if ( !highNote3.isEmpty() && highNoteLength > 0 && highNotePos3 < tempPos ) tempPos = highNotePos3;
                                            if ( !highNote4.isEmpty() && highNoteLength > 0 && highNotePos4 < tempPos ) tempPos = highNotePos4;

                                            if (!highNote4.isEmpty() && highNoteLength > 0) {
                                                if ( highNotePos4 == tempPos ) {
                                                    v_note_tie_flag = highNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, highNotePos4, highNoteImage4, highNoteFlat4, v_note_tie_flag);
                                            }

                                            if (!highNote3.isEmpty() && highNoteLength > 0) {
                                                if ( highNotePos3 == tempPos ) {
                                                    v_note_tie_flag = highNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, highNotePos3, highNoteImage3, highNoteFlat3, v_note_tie_flag);
                                            }

                                            if (!highNote2.isEmpty() && highNoteLength > 0) {
                                                if ( highNotePos2 == tempPos ) {
                                                    v_note_tie_flag = highNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, highNotePos2, highNoteImage2, highNoteFlat2, v_note_tie_flag);
                                            }

                                            if (!highNote1.isEmpty() && highNoteLength > 0) {
                                                if ( highNotePos1 == tempPos ) {
                                                    v_note_tie_flag = highNoteTieFlag;
                                                } else {
                                                    v_note_tie_flag = false;
                                                }
                                                displayMusicSymbol(inx, canvas, vStartPos, highNotePos1, highNoteImage1, highNoteFlat1, v_note_tie_flag);
                                            }

                                        } // down or up

                                    } else if (highPlayFlag == 0) {
                                        // 쉼표 그리기
                                        highSymbolImage = "symbol_"+String.valueOf(highNoteLength)+"_wait";
                                        displayMusicSymbol(inx, canvas, vStartPos, imgMainYPos + 80, highSymbolImage, false, false);

                                    } else if (highPlayFlag == 2) {
                                        // Line 그리기
                                        highSymbolImage = "symbol_single";
                                        displayMusicSymbol(inx, canvas, vStartPos, imgMainYPos + 18, highSymbolImage, false, false);

                                    } // High playFlag

                                    // ------------------------------------------
                                    // 현재 실행위치 표시하기
                                    // ------------------------------------------
                                    if (inx == ((PianoApp) mContext.getApplicationContext()).currPlaySeq) {
                                        // rect
                                        RectF r = new RectF(vStartPos - 10, 300, vStartPos + 60, 840);
                                        canvas.drawRect(r, redBox); // stroke
                                    }

                                } // for loop

                            }

                            // Music Sheet 높은 음자리 Line 그리기
                            int lineStartPos = imgMainYPos + 30;
                            int vSheetLineLength = ((PianoApp) mContext.getApplicationContext()).musicSheetWidth;

                            // Base Line
                            canvas.drawLine(imgMainXPos, lineStartPos - 40, imgMainXPos + vSheetLineLength, lineStartPos - 40, redLine);

                            canvas.drawLine(imgMainXPos, lineStartPos, imgMainXPos + vSheetLineLength, lineStartPos, blackLine);
                            canvas.drawLine(imgMainXPos, lineStartPos + 40, imgMainXPos + vSheetLineLength, lineStartPos + 40, blackLine);
                            canvas.drawLine(imgMainXPos, lineStartPos + 80, imgMainXPos + vSheetLineLength, lineStartPos + 80, blackLine);
                            canvas.drawLine(imgMainXPos, lineStartPos + 120, imgMainXPos + vSheetLineLength, lineStartPos + 120, blackLine);
                            canvas.drawLine(imgMainXPos, lineStartPos + 160, imgMainXPos + vSheetLineLength, lineStartPos + 160, blackLine);

                            // Base Line
                            canvas.drawLine(imgMainXPos, imgMainYPos + 230, imgMainXPos + vSheetLineLength, imgMainYPos + 230, redLine);

                            // Music Sheet 낮은 음자리 Line 그리기
                            lineStartPos = imgMainYPos + 270;

                            canvas.drawLine(imgMainXPos, lineStartPos, imgMainXPos + vSheetLineLength, lineStartPos, blackLine);
                            canvas.drawLine(imgMainXPos, lineStartPos + 40, imgMainXPos + vSheetLineLength, lineStartPos + 40, blackLine);
                            canvas.drawLine(imgMainXPos, lineStartPos + 80, imgMainXPos + vSheetLineLength, lineStartPos + 80, blackLine);
                            canvas.drawLine(imgMainXPos, lineStartPos + 120, imgMainXPos + vSheetLineLength, lineStartPos + 120, blackLine);
                            canvas.drawLine(imgMainXPos, lineStartPos + 160, imgMainXPos + vSheetLineLength, lineStartPos + 160, blackLine);

                            // Base Line
                            canvas.drawLine(imgMainXPos, lineStartPos + 200, imgMainXPos + vSheetLineLength, lineStartPos + 200, redLine);

                            // 제어버튼 Start Point
                            int vBtnXpos = ((PianoApp) mContext.getApplicationContext()).btnControlXPos;
                            int vBtnYpos = ((PianoApp) mContext.getApplicationContext()).btnControlYPos;

                            // ------------------------------
                            // Layer 0
                            // ------------------------------
                            // 제어버튼 - First
                            Bitmap btnFirstStep = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_first);
                            canvas.drawBitmap(btnFirstStep, vBtnXpos - 100, vBtnYpos - 100, null);
                            btnFirstStep.recycle();

                            // 제어버튼 - Previous Step
                            Bitmap btnPrevStep = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_prevstep);
                            canvas.drawBitmap(btnPrevStep, vBtnXpos, vBtnYpos - 100, null);
                            btnPrevStep.recycle();

                            // 제어버튼 - Next Step
                            Bitmap btnNextStep = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_nextstep);
                            canvas.drawBitmap(btnNextStep, vBtnXpos+100, vBtnYpos-100, null);
                            btnNextStep.recycle();

                            // 제어버튼 - Last Step
                            Bitmap btnLastStep = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_last);
                            canvas.drawBitmap(btnLastStep, vBtnXpos+200, vBtnYpos-100, null);
                            btnLastStep.recycle();

                            // ------------------------------
                            // Layer 1
                            // ------------------------------
                            //Next Play Speed
                            Bitmap btnNextSpeed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_fast);
                            canvas.drawBitmap(btnNextSpeed, vBtnXpos-100, vBtnYpos, null);
                            btnNextSpeed.recycle();

                            //prev Play Speed
                            Bitmap btnPrevSpeed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_slow);
                            canvas.drawBitmap(btnPrevSpeed, vBtnXpos, vBtnYpos, null);
                            btnPrevSpeed.recycle();

                            //prev music step
                            Bitmap btnPrev = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_prev);
                            canvas.drawBitmap(btnPrev, vBtnXpos + 100, vBtnYpos, null);
                            btnPrev.recycle();

                            //Next music step
                            Bitmap btnNext = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_next);
                            canvas.drawBitmap(btnNext, vBtnXpos + 200, vBtnYpos, null);
                            btnNext.recycle();

                            // ------------------------------
                            // Layer 2
                            // ------------------------------

                            if ( ((PianoApp)mContext.getApplicationContext()).modeMakePlay.equals("MAKE")) {

                                // 제어버튼 - 추가
                                Bitmap btnAdd = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_add);
                                canvas.drawBitmap(btnAdd, vBtnXpos, vBtnYpos + 100, null);
                                btnAdd.recycle();

                                // 제어버튼 - 삽입
                                Bitmap btnInsert = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_insert);
                                canvas.drawBitmap(btnInsert, vBtnXpos + 200, vBtnYpos + 100, null);
                                btnInsert.recycle();
                            }

                            // ------------------------------
                            // Layer 3
                            // ------------------------------
                            if ( ((PianoApp)mContext.getApplicationContext()).modeMakePlay.equals("MAKE")) {

                                // 음자리표
                                String vMake = ((PianoApp) mContext.getApplicationContext()).makeMusicSheet;

                                if (vMake.equals("HIGH")) {
                                    Bitmap btnMakeHigh = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_high);
                                    canvas.drawBitmap(btnMakeHigh, vBtnXpos - 100, vBtnYpos + 200, null);
                                    btnMakeHigh.recycle();
                                } else {
                                    Bitmap btnMakeLow = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_low);
                                    canvas.drawBitmap(btnMakeLow, vBtnXpos - 100, vBtnYpos + 200, null);
                                    btnMakeLow.recycle();
                                }

                                // 제어버튼 - 음표 Toggle
                                canvas.drawBitmap(getNoteBitmap(), vBtnXpos + 10, vBtnYpos + 200, null);

                                // 제어버튼 - 쉼표 Toggle
                                canvas.drawBitmap(getRestBitmap(), vBtnXpos + 120, vBtnYpos + 200, null);

                                // 제어버튼 - Line Toggle
                                Bitmap btnLine = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_line);
                                canvas.drawBitmap(btnLine, vBtnXpos + 200, vBtnYpos + 200, null);
                                btnLine.recycle();
                            }

                            // ------------------------------
                            // Layer 4
                            // ------------------------------
                            if ( ((PianoApp)mContext.getApplicationContext()).modeMakePlay.equals("MAKE")) {

                                // 제어버튼 - Lock
                                String vLock = ((PianoApp) mContext.getApplicationContext()).modeLock;
                                if (vLock.equals("LOCK")) {
                                    Bitmap btnLock = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_lock);
                                    canvas.drawBitmap(btnLock, vBtnXpos-100, vBtnYpos + 300, null);
                                    btnLock.recycle();
                                } else {
                                    Bitmap btnLock = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_unlock);
                                    canvas.drawBitmap(btnLock, vBtnXpos-100, vBtnYpos + 300, null);
                                    btnLock.recycle();
                                }

                                // Tie Sign
                                Bitmap btnTieSign = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_tie);
                                canvas.drawBitmap(btnTieSign, vBtnXpos, vBtnYpos + 300, null);
                                btnTieSign.recycle();

                                // 제어버튼 - 삭제
                                Bitmap btnDelete = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_delete);
                                canvas.drawBitmap(btnDelete, vBtnXpos + 100, vBtnYpos + 300, null);
                                btnDelete.recycle();


                                // Key Sign
                                Bitmap btnKeySign = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.key_sign);
                                canvas.drawBitmap(btnKeySign, vBtnXpos + 200, vBtnYpos + 300, null);
                                btnKeySign.recycle();

                            }

                            canvas.restore();

                        } // canvas != null

                    }  // synchronized

                } // canvasLocked

            } finally {   // 버퍼 작업이 끝나면

                try {
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                        canvasLocked = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            //Thread 일시정지
            synchronized (this)
            {
                if (isWait){
                    try {
                        wait();
                    } catch (Exception e) {
                        //nothing
                    }
                }
            } //sync

        } // while
    } // run

    // -----------------------------
    // Thread Stop (완전정지)
    // -----------------------------
    public void StopThread()
    {
        //canRun = false;

        this.interrupt();

        synchronized (this){
            this.notify();
        }
    }

    // -----------------------------
    // Thread Pause (일시정지)
    // -----------------------------
    public void PauseThread(boolean wait)
    {
        isWait = wait;
        synchronized (this){
            this.notify();
        }
    }

    public void  displayMusicSymbol ( int  inx, Canvas canvas , int startPos, int notePos, String noteImage, boolean noteFlat, boolean noteTie) {

        //음표 이미지 그리기
        try {

            Class res = R.drawable.class;
            Field field = res.getField(noteImage);
            int drawableId = field.getInt(null);
            Bitmap imgSymbol = BitmapFactory.decodeResource(mContext.getResources(), drawableId);

            Paint symbolPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
            symbolPaint.setAlpha(150); // 0 - 255
            canvas.drawBitmap(imgSymbol,startPos, notePos, symbolPaint);

            // 이미지에 사용한 메모리 해제하기
            imgSymbol.recycle();

            // Flat or Sharp Display
            if (noteFlat) {

                int vNotePos = notePos-10;

                if (noteImage.contains("up")) {
                    vNotePos = notePos+65;
                }

                Bitmap flatSymbol = BitmapFactory.decodeResource(mContext.getResources(),  R.drawable.symbol_flat);
                canvas.drawBitmap(flatSymbol,startPos-20, vNotePos, symbolPaint);
                flatSymbol.recycle();
            }

            // Tie 표시
            if (noteTie) {

                if (noteImage.contains("up")) {
                    int vNotePos = notePos + 110;

                    Bitmap tieUpSymbol = BitmapFactory.decodeResource(mContext.getResources(),  R.drawable.symbol_up_tie);
                    canvas.drawBitmap(tieUpSymbol,startPos, vNotePos, symbolPaint);
                    tieUpSymbol.recycle();

                } else {
                    int vNotePos = notePos - 30;

                    Bitmap tieDownSymbol = BitmapFactory.decodeResource(mContext.getResources(),  R.drawable.symbol_down_tie);
                    canvas.drawBitmap(tieDownSymbol,startPos, vNotePos, symbolPaint);
                    tieDownSymbol.recycle();
                }

            }

        }
        catch (Exception e) {

        }
    }

    public Bitmap getNoteBitmap () {

        try {

            int noteLength = ((PianoApp) mContext.getApplicationContext()).noteLength;

            String noteImage = "symbol_"+String.valueOf(noteLength)+"_up";

            Class res = R.drawable.class;
            Field field = res.getField(noteImage);
            int drawableId = field.getInt(null);
            Bitmap imgSymbol = BitmapFactory.decodeResource(mContext.getResources(), drawableId);

            int width = imgSymbol.getWidth();
            int height = imgSymbol.getHeight();
            float scaleWidth = ((float) 40) / width;
            float scaleHeight = ((float) 90) / height;

            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();

            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap( imgSymbol, 0, 0, width, height, matrix, false);
            imgSymbol.recycle();
            return resizedBitmap;

        }
        catch (Exception e) {
           return null;
        }

    } //getNoteBitmap

    public Bitmap getRestBitmap () {

        try {

            int restLength = ((PianoApp) mContext.getApplicationContext()).restLength;
            String restImage = "symbol_"+String.valueOf(restLength)+"_wait";

            Class res = R.drawable.class;
            Field field = res.getField(restImage);
            int drawableId = field.getInt(null);
            Bitmap imgSymbol = BitmapFactory.decodeResource(mContext.getResources(), drawableId);

            int width = imgSymbol.getWidth();
            int height = imgSymbol.getHeight();
            float scaleWidth = ((float) 40) / width;
            float scaleHeight = ((float) 90) / height;

            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();

            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap( imgSymbol, 0, 0, width, height, matrix, false);
            imgSymbol.recycle();
            return resizedBitmap;

        }
        catch (Exception e) {
            return null;
        }

    } //getRestBitmap

} // Thread Class