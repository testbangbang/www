package com.onyx.einfo.utils;

import android.content.Context;
import android.widget.ImageView;

import com.onyx.einfo.R;
import com.onyx.einfo.model.AccountInfo;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by suicheng on 2016/11/26.
 */

public class AvatarUtils {

    private static String[] defaultAvatarArray;

    private static String[] loadDefaultAvatarArray(Context context) {
        if (defaultAvatarArray != null && defaultAvatarArray.length > 0) {
            return defaultAvatarArray;
        }
        return defaultAvatarArray = context.getResources().getStringArray(R.array.default_user_avatar_array);
    }

    public static void loadAvatar(Context context, ImageView imageView, String avatarPath) {
        String path = avatarPath;
        if (StringUtils.isNullOrEmpty(path)) {
            path = loadDefaultAvatarArray(context)[0];
            AccountInfo.saveAvatarPath(context, path);
        }

        if (path.startsWith("res/drawable") || path.startsWith("res/mipmap")) {
            imageView.setImageResource(ResourceUtils.getDrawableResIdByName(context, FileUtils.getBaseName(path)));
        } else {
            Picasso.with(context).load(path).fit().centerCrop().into(imageView);
        }
    }

    public static String getSpecifyArrayAvatarPath(Context context, int avatarArrayRes) {
        String avatarPath = AccountInfo.loadAvatarPath(context);
        if (!AppCompatUtils.isColorDevice(context)) {
            return avatarPath;
        }
        String[] avatarArray = context.getResources().getStringArray(avatarArrayRes);
        if (StringUtils.isNullOrEmpty(avatarPath)) {
            avatarPath = avatarArray[0];
        }
        for (String s : avatarArray) {
            if (FileUtils.getBaseName(s).startsWith(FileUtils.getBaseName(avatarPath))) {
                avatarPath = s;
                break;
            }
        }
        return avatarPath;
    }
}
