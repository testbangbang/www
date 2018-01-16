package com.onyx.android.sdk.reader.host.request;

import android.media.MediaPlayer;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by lxm on 2018/1/16.
 */

public class PlayAudioRequest extends BaseReaderRequest {

    private MediaPlayer mediaPlayer;
    private byte[] data;

    public PlayAudioRequest(MediaPlayer mediaPlayer, byte[] data) {
        this.mediaPlayer = mediaPlayer;
        this.data = data;
    }

    @Override
    public void execute(Reader reader) throws Exception {
        if (mediaPlayer == null || data == null) {
            return;
        }
        mediaPlayer.reset();
        mediaPlayer.setDataSource(saveToTempFile());
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    private FileDescriptor saveToTempFile() throws Exception {
        File tempMp3 = File.createTempFile("temp", "mp3");
        tempMp3.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempMp3);
        fos.write(data);
        fos.close();
        FileInputStream fis = new FileInputStream(tempMp3);
        return fis.getFD();
    }
}
