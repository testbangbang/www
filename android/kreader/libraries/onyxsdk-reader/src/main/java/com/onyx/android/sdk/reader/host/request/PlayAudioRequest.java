package com.onyx.android.sdk.reader.host.request;

import android.content.Context;
import android.media.MediaPlayer;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

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

    private String saveToTempFile() {
        String path = null;
        File tempMp3 = null;
        FileOutputStream fos = null;
        try {
            File dir = getContext().getDir("temp_audio", Context.MODE_PRIVATE);
            FileUtils.purgeDirectory(dir);
            tempMp3 = File.createTempFile(UUID.randomUUID().toString(), "mp3", dir);
            tempMp3.deleteOnExit();
            fos = new FileOutputStream(tempMp3);
            fos.write(data);
            fos.close();
            path = tempMp3.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            if (tempMp3 != null) {
                tempMp3.delete();
            }
        }finally {
            FileUtils.closeQuietly(fos);
        }
        return path;
    }
}
