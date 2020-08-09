package com.cho.ksoo.makeplaypiano;

public class AsyncMusicSheetParam {

    String musicId;
    String musicSeq;
    String lowNoteLength;
    String lowPlayFlag;
    String lowNote1;
    String lowNote2;
    String lowNote3;
    String lowNote4;
    String lowTie;
    String highNoteLength;
    String highPlayFlag;
    String highNote1;
    String highNote2;
    String highNote3;
    String highNote4;
    String highTie;

    public AsyncMusicSheetParam (
            String musicId,
            String musicSeq,
            String lowNoteLength,
            String lowPlayFlag,
            String lowNote1,
            String lowNote2,
            String lowNote3,
            String lowNote4,
            String lowTie,
            String highNoteLength,
            String highPlayFlag,
            String highNote1,
            String highNote2,
            String highNote3,
            String highNote4,
            String highTie
    ) {

        this.musicId = musicId;
        this.musicSeq = musicSeq;
        this.lowNoteLength = lowNoteLength;
        this.lowPlayFlag = lowPlayFlag;
        this.lowNote1 = lowNote1;
        this.lowNote2 = lowNote2;
        this.lowNote3 = lowNote3;
        this.lowNote4 = lowNote4;
        this.lowTie = lowTie;
        this.highNoteLength = highNoteLength;
        this.highPlayFlag = highPlayFlag;
        this.highNote1 = highNote1;
        this.highNote2 = highNote2;
        this.highNote3 = highNote3;
        this.highNote4 = highNote4;
        this.highTie = highTie;

    }
}
