package com.cho.ksoo.makeplaypiano;

import android.util.SparseArray;

public class SoundMap {

    private  static final SparseArray<String> SOUND_MAP = new SparseArray<>();
    private  static final SparseArray<String> KEY_MAP = new SparseArray<>();

    static {
        SOUND_MAP.put(1, "C8");
        SOUND_MAP.put(2, "B7");
        SOUND_MAP.put(3, "Bb7");
        SOUND_MAP.put(4, "A7");
        SOUND_MAP.put(5, "Ab7");
        SOUND_MAP.put(6, "G7");
        SOUND_MAP.put(7, "Gb7");
        SOUND_MAP.put(8, "F7");
        SOUND_MAP.put(9, "E7");
        SOUND_MAP.put(10, "Eb7");
        SOUND_MAP.put(11, "D7");
        SOUND_MAP.put(12, "Db7");
        SOUND_MAP.put(13, "C7");
        SOUND_MAP.put(14, "B6");
        SOUND_MAP.put(15, "Bb6");
        SOUND_MAP.put(16, "A6");
        SOUND_MAP.put(17, "Ab6");
        SOUND_MAP.put(18, "G6");
        SOUND_MAP.put(19, "Gb6");
        SOUND_MAP.put(20, "F6");
        SOUND_MAP.put(21, "E6");
        SOUND_MAP.put(22, "Eb6");
        SOUND_MAP.put(23, "D6");
        SOUND_MAP.put(24, "Db6");
        SOUND_MAP.put(25, "C6");
        SOUND_MAP.put(26, "B5");
        SOUND_MAP.put(27, "Bb5");
        SOUND_MAP.put(28, "A5");
        SOUND_MAP.put(29, "Ab5");
        SOUND_MAP.put(30, "G5");
        SOUND_MAP.put(31, "Gb5");
        SOUND_MAP.put(32, "F5");
        SOUND_MAP.put(33, "E5");
        SOUND_MAP.put(34, "Eb5");
        SOUND_MAP.put(35, "D5");
        SOUND_MAP.put(36, "Db5");
        SOUND_MAP.put(37, "C5");
        SOUND_MAP.put(38, "B4");
        SOUND_MAP.put(39, "Bb4");
        SOUND_MAP.put(40, "A4");
        SOUND_MAP.put(41, "Ab4");
        SOUND_MAP.put(42, "G4");
        SOUND_MAP.put(43, "Gb4");
        SOUND_MAP.put(44, "F4");
        SOUND_MAP.put(45, "E4");
        SOUND_MAP.put(46, "Eb4");
        SOUND_MAP.put(47, "D4");
        SOUND_MAP.put(48, "Db4");
        SOUND_MAP.put(49, "C4");
        SOUND_MAP.put(50, "B3");
        SOUND_MAP.put(51, "Bb3");
        SOUND_MAP.put(52, "A3");
        SOUND_MAP.put(53, "Ab3");
        SOUND_MAP.put(54, "G3");
        SOUND_MAP.put(55, "Gb3");
        SOUND_MAP.put(56, "F3");
        SOUND_MAP.put(57, "E3");
        SOUND_MAP.put(58, "Eb3");
        SOUND_MAP.put(59, "D3");
        SOUND_MAP.put(60, "Db3");
        SOUND_MAP.put(61, "C3");
        SOUND_MAP.put(62, "B2");
        SOUND_MAP.put(63, "Bb2");
        SOUND_MAP.put(64, "A2");
        SOUND_MAP.put(65, "Ab2");
        SOUND_MAP.put(66, "G2");
        SOUND_MAP.put(67, "Gb2");
        SOUND_MAP.put(68, "F2");
        SOUND_MAP.put(69, "E2");
        SOUND_MAP.put(70, "Eb2");
        SOUND_MAP.put(71, "D2");
        SOUND_MAP.put(72, "Db2");
        SOUND_MAP.put(73, "C2");
        SOUND_MAP.put(74, "B1");
        SOUND_MAP.put(75, "Bb1");
        SOUND_MAP.put(76, "A1");
        SOUND_MAP.put(77, "Ab1");
        SOUND_MAP.put(78, "G1");
        SOUND_MAP.put(79, "Gb1");
        SOUND_MAP.put(80, "F1");
        SOUND_MAP.put(81, "E1");
        SOUND_MAP.put(82, "Eb1");
        SOUND_MAP.put(83, "D1");
        SOUND_MAP.put(84, "Db1");
        SOUND_MAP.put(85, "C1");
        SOUND_MAP.put(86, "B0");
        SOUND_MAP.put(87, "Bb0");
        SOUND_MAP.put(88, "A0");
    }

    static {
        // white keys sounds
        KEY_MAP.put(1, "C3");
        KEY_MAP.put(2, "D3");
        KEY_MAP.put(3, "E3");
        KEY_MAP.put(4, "F3");
        KEY_MAP.put(5, "G3");
        KEY_MAP.put(6, "A3");
        KEY_MAP.put(7, "B3");
        KEY_MAP.put(8, "C4");
        KEY_MAP.put(9, "D4");
        KEY_MAP.put(10, "E4");
        KEY_MAP.put(11, "F4");
        KEY_MAP.put(12, "G4");
        KEY_MAP.put(13, "A4");
        KEY_MAP.put(14, "B4");
        KEY_MAP.put(15, "C5");
        KEY_MAP.put(16, "D5");
        KEY_MAP.put(17, "E5");
        KEY_MAP.put(18, "F5");
        KEY_MAP.put(19, "G5");
        KEY_MAP.put(20, "A5");
        KEY_MAP.put(21, "B5");
        KEY_MAP.put(22, "C6");
        KEY_MAP.put(23, "D6");
        KEY_MAP.put(24, "E6");
        KEY_MAP.put(25, "F6");
        KEY_MAP.put(26, "G6");
        KEY_MAP.put(27, "A6");
        KEY_MAP.put(28, "B6");
        // black keys sounds
        KEY_MAP.put(29, "Db3");
        KEY_MAP.put(30, "Eb3");
        KEY_MAP.put(31, "Gb3");
        KEY_MAP.put(32, "Ab3");
        KEY_MAP.put(33, "Bb3");
        KEY_MAP.put(34, "Db4");
        KEY_MAP.put(35, "Eb4");
        KEY_MAP.put(36, "Gb4");
        KEY_MAP.put(37, "Ab4");
        KEY_MAP.put(38, "Bb4");
        KEY_MAP.put(39, "Db5");
        KEY_MAP.put(40, "Eb5");
        KEY_MAP.put(41, "Gb5");
        KEY_MAP.put(42, "Ab5");
        KEY_MAP.put(43, "Bb5");
        KEY_MAP.put(44, "Db6");
        KEY_MAP.put(45, "Eb6");
        KEY_MAP.put(46, "Gb6");
        KEY_MAP.put(47, "Ab6");
        KEY_MAP.put(48, "Bb6");
    }

    public SoundMap() {

    }

    public static String getSoundNote(int soundNo) {

        return SOUND_MAP.get(soundNo);
    }

    public static int getSoundNo(String soundNote) {

        int soundNo = 0;

        for (int inx = 1; inx <= 88; inx++ ) {
            if ( SOUND_MAP.get(inx).equals(soundNote) ) {
                soundNo = inx;
                break;
            }
        }

        return soundNo;
    }

    public static String getKeyNote(int keyNo) {

        return KEY_MAP.get(keyNo);
    }

    public static int getKeyNo(String soundNote) {

        int keyNo = 0;

        for (int inx = 1; inx <= 48; inx++ ) {
            if ( KEY_MAP.get(inx).equals(soundNote) ) {
                keyNo = inx;
                break;
            }
        }

        return keyNo;
    }

}
