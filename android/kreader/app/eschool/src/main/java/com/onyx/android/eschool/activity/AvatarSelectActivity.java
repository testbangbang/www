package com.onyx.android.eschool.activity;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.model.StudentAccount;
import com.onyx.android.eschool.utils.AvatarUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by suicheng on 2016/11/21.
 */

public class AvatarSelectActivity extends BaseActivity {

    @Bind(R.id.avatar_select_page_view)
    PageRecyclerView avatarPageView;
    @Bind(R.id.imageView_preview)
    ImageView userAvatarPreview;

    private List<String> avatarArray = new ArrayList<>();
    int currentSelect = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_avatar_select;
    }

    protected void initConfig() {
    }

    @Override
    protected void initView() {
        AvatarUtils.loadAvatar(this, userAvatarPreview, StudentAccount.loadAvatarPath(this));
        avatarPageView.setLayoutManager(new DisableScrollGridManager(this));
        avatarPageView.setAdapter(new PageRecyclerView.PageAdapter<AvatarHolder>() {
            @Override
            public int getRowCount() {
                if (avatarArray == null) {
                    return 0;
                }
                int count = avatarArray.size() / getColumnCount();
                if (avatarArray.size() % getColumnCount() != 0) {
                    count++;
                }
                if (count > 3) {
                    count = 3;
                }
                return count;
            }

            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public int getDataCount() {
                return CollectionUtils.isNullOrEmpty(avatarArray) ? 0 : avatarArray.size();
            }

            @Override
            public AvatarHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new AvatarHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_info_select_image_item, null));
            }

            @Override
            public void onPageBindViewHolder(AvatarHolder avatarHolder, int position) {
                avatarHolder.itemView.setTag(position);
                updateImageView(avatarHolder.avatarView, avatarArray.get(position));
            }
        });
    }

    @Override
    protected void initData() {
        String[] stringArray = getResources().getStringArray(R.array.default_user_avatar_array);
        avatarArray.addAll(Arrays.asList(stringArray));
    }

    private int[] getRefIds(int arrayResId) {
        TypedArray ar = getResources().obtainTypedArray(arrayResId);
        int len = ar.length();
        int[] refIds = new int[len + 1];
        for (int i = 0; i < len; i++) {
            refIds[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();
        return refIds;
    }

    private void saveAccountAvatar() {
        StudentAccount.saveAvatarPath(this, avatarArray.get(currentSelect));
    }

    @OnClick(R.id.confirm_save)
    void onConfirmClick() {
        saveAccountAvatar();
        showToast(R.string.save_success, Toast.LENGTH_SHORT);
    }

    private void processAddNewAvatar() {
    }

    private void updateImageView(ImageView imageView, String path) {
        if (StringUtils.isNullOrEmpty(path)) {
            return;
        }
        AvatarUtils.loadAvatar(this, imageView, path);
    }

    private void processAvatarClick(int position) {
        if (position == avatarArray.size() - 1) {
            processAddNewAvatar();
            return;
        }
        currentSelect = position;
        updateImageView(userAvatarPreview, avatarArray.get(currentSelect));
    }

    class AvatarHolder extends RecyclerView.ViewHolder {
        ImageView avatarView;

        public AvatarHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processAvatarClick((Integer) v.getTag());
                }
            });
            avatarView = (ImageView) itemView;
        }
    }
}
