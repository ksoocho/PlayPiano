package com.cho.ksoo.makeplaypiano;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.SparseArray;
import java.io.InputStream;

/**
 * Created by ssaurel on 15/03/2018.
 */
public class AudioKeyPlayer {

    private SparseArray<PlayThread> threadMap = null;
    private Context context;
    public static final int MAX_VOLUME = 100, CURRENT_VOLUME = 90;

    public AudioKeyPlayer(Context context) {
        this.context = context;
        threadMap = new SparseArray<>();
    }

    public void playNote(int note, int noteLength) {
        if (!isNotePlaying(note)) {
            PlayThread thread = new PlayThread(note,noteLength);
            thread.start();
            threadMap.put(note, thread);
        }
    }

    public void stopNote(int note) {
        PlayThread thread = threadMap.get(note);

        if (thread != null) {
            threadMap.remove(note);
        }
    }

    public boolean isNotePlaying(int note) {
        return threadMap.get(note) != null;
    }

    private class PlayThread extends Thread {
        int note;
        int noteLength;

        AudioTrack audioTrack;

        public PlayThread(int note, int noteLen) {
            this.note = note;
            this.noteLength = noteLen;
        }

        @Override
        public void run() {
            try {

                long startTime = System.currentTimeMillis();

                String path = SoundMap.getKeyNote(note) + ".wav";

                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor ad = assetManager.openFd(path);

                long fileSize = ad.getLength();
                int bufferSize = 4096;
                byte[] buffer = new byte[bufferSize];

                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

                float logVolume = (float) (1 - (Math.log(MAX_VOLUME - CURRENT_VOLUME) / Math.log(MAX_VOLUME)));
                audioTrack.setStereoVolume(logVolume, logVolume);

                audioTrack.play();
                InputStream audioStream = null;

                int headerOffset = 0x2C;
                long bytesWritten = 0;
                int bytesRead = 0;

                audioStream = assetManager.open(path);
                audioStream.read(buffer, 0, headerOffset);

                boolean isPlaying = true;
                int checkCount = 0;

                while (bytesWritten < fileSize - headerOffset) {

                    checkCount ++;

                    bytesRead = audioStream.read(buffer, 0, bufferSize);
                    bytesWritten += audioTrack.write(buffer, 0, bytesRead);

                    long currentTime = System.currentTimeMillis();
                    long playTime = (currentTime - startTime);
                    long stopTime = noteLength;

                    if (playTime > stopTime) {
                        break;
                    }

                } // isPlaying

                audioTrack.stop();
                audioTrack.release();

            } catch (Exception e) {

            } finally {
                if (audioTrack != null) {
                    audioTrack.release();
                }
            }
        }
    }

}