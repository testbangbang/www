package com.onyx.edu.reader.note.model;

import android.graphics.RectF;

import com.onyx.android.sdk.scribble.data.ConverterRectangle;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by ming on 2017/8/1.
 */

@Table(database = ReaderNoteDatabase.class)
public class SignatureShapeModel extends ReaderNoteShapeModel {

    @Column
    private String accountId;

    @Column(typeConverter = ConverterRectangle.class)
    private RectF signatureRect = null;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public RectF getSignatureRect() {
        return signatureRect;
    }

    public void setSignatureRect(RectF signatureRect) {
        this.signatureRect = signatureRect;
    }
}
