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

package com.onyx.android.edu.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;


/**
 * This provides methods to help Activities load their UI.
 */
public class ActivityUtils {
    private static final String ACTION_CHECK_EXAMINATION_RESULT = "com.onyx.kreader.anction.CHECK_EXAM_RESULT";
    private static final String CHECK_RESULT = "check_result";
    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     *
     */
    public static void addFragmentToActivity (@NonNull FragmentManager fragmentManager,
                                              @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public static void switchFragment(@NonNull FragmentManager fragmentManager,
                                      @NonNull Fragment fromFragment,
                                      @NonNull Fragment toFragment, int frameId){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(fromFragment);
        if (!toFragment.isAdded()){
            transaction.add(frameId, toFragment).commit();
        }else {
            transaction.show(toFragment).commit();
        }
    }

    public static void sendCheckExamResultBroadcast(Context context, String result) {
        Intent intent = new Intent();
        intent.setAction(ACTION_CHECK_EXAMINATION_RESULT);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra(CHECK_RESULT, result);
        context.sendBroadcast(intent);
    }
}
