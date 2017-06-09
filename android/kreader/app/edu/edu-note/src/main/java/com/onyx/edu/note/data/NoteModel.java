package com.onyx.edu.note.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.onyx.edu.note.R;

/**
 * Created by solskjaer49 on 2017/6/1 15:31.
 * TODO:temp bean class,finally will use sdk NoteModel class to replace it.
 */

public class NoteModel {
    @NonNull
    private String uniqueID = Integer.toString(Integer.MIN_VALUE);
    private String documentName;
    private String createdDate;
    private String lastModifiedDate;
    private String parentUniqueID;
    private Bitmap thumbnail;
    private @NoteType.NoteTypeDef
    int type;

    public static NoteModel buildCreateNoteModel(Context context) {
        NoteModel model = new NoteModel();
        model.setThumbnail(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_add_note));
        model.setDocumentName(context.getString(R.string.create_note));
        model.setType(NoteType.CREATE_NOTE);
        return model;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getParentUniqueID() {
        return parentUniqueID;
    }

    public void setParentUniqueID(String parentUniqueID) {
        this.parentUniqueID = parentUniqueID;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public @NoteType.NoteTypeDef int getType() {
        return type;
    }

    public void setType(@NoteType.NoteTypeDef int type) {
        this.type = type;
    }
}
