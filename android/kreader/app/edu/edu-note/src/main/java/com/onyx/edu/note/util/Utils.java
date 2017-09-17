/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onyx.edu.note.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.PageNameList;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.edu.note.NoteApplication;
import com.onyx.edu.note.R;

/**
 * This provides methods to help Activities load their UI.
 */
public class Utils {

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(fragment, tag);
        transaction.commit();
    }

    //TODO:place this static method in noteModel class?
    public static NoteModel createAddNoteItem(Context context) {
        final NoteModel document = new NoteModel();
        document.setType(Constant.TYPE_CREATE_NOTE);
        document.setTitle(context.getString(R.string.create_note));
        document.setUniqueId(Long.toString(Long.MIN_VALUE));
        document.setThumbnail(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_add_note));
        return document;
    }

    public static boolean isValidNote(NoteModel model) {
        return model.getType() > Constant.TYPE_CREATE_NOTE;
    }

    public static void showNoteNameIllegal(Context context, android.app.FragmentManager fragmentManager, final boolean isInteractWithScribble) {
        final OnyxAlertDialog illegalDialog = new OnyxAlertDialog();
        final NoteManager noteManager = NoteApplication.getInstance().getNoteManager();
        OnyxAlertDialog.Params params = new OnyxAlertDialog.Params().setTittleString(context.getString(R.string.noti))
                .setAlertMsgString(context.getString(R.string.note_name_already_exist))
                .setEnableNegativeButton(false).setCanceledOnTouchOutside(false)
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        illegalDialog.dismiss();
                        if (isInteractWithScribble) {
                            noteManager.sync(true, !noteManager.inUserErasing());
                        }
                    }
                });
        illegalDialog.setParams(params);
        illegalDialog.show(fragmentManager, "illegalDialog");
    }

    public static String getLibraryTitleWithSize(final NoteModel noteModel) {
        StringBuilder builder = new StringBuilder(noteModel.getTitle());
        PageNameList pageNameList = noteModel.getPageNameList();
        builder.append("(");
        builder.append(noteModel.getSubDocCount());
        builder.append(")");
        return builder.toString();
    }
}
