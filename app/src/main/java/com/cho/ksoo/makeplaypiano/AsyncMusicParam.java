package com.cho.ksoo.makeplaypiano;

public class AsyncMusicParam {

    String musicId;
    String musicUserId;
    String musicTitle;
    String musicTitleEng;
    String musicDescr;
    String musicKeySign;
    String musicTimeSign;
    String musicSpeed ;
    String composerName;

    public AsyncMusicParam(
            String musicId
            ,String musicUserId
            ,String musicTitle
            ,String musicTitleEng
            ,String musicDescr
            ,String musicKeySign
            ,String musicTimeSign
            ,String musicSpeed
            ,String composerName) {

        this.musicId = musicId;
        this.musicUserId = musicUserId;
        this.musicTitle = musicTitle;
        this.musicTitleEng = musicTitleEng;
        this.musicDescr = musicDescr;
        this.musicKeySign = musicKeySign;
        this.musicTimeSign = musicTimeSign;
        this.musicSpeed  = musicSpeed;
        this.composerName = composerName;

    }
}
