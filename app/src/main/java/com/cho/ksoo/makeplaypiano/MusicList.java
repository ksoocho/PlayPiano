package com.cho.ksoo.makeplaypiano;

public class MusicList {

    private String music_id;
    private String music_title;
    private String music_descr;
    private String music_status;

    public MusicList( String p_music_id,
                      String p_music_title,
                      String p_music_descr,
                      String p_music_status)
    {
        this.music_id = p_music_id;
        this.music_title = p_music_title;
        this.music_descr = p_music_descr;
        this.music_status = p_music_status;
    }

    public String getMusicID() { return music_id; }
    public String getMusicTitle() { return music_title; }
    public String getMusicDescr() { return music_descr; }
    public String getMusicStatus() { return music_status; }
}
