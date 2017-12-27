package com.onyx.jdread.library.model;

import android.databinding.ObservableField;

import java.util.Observable;

/**
 * Created by hehai on 17-12-26.
 */

public class FileServerModel extends Observable {

    public final ObservableField<String> serverAddress = new ObservableField<>();
}
