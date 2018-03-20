package com.onyx.jdread.util;

import android.databinding.DataBindingUtil;
import android.databinding.ViewStubProxy;
import android.os.Build;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.NoneResultBinding;
import com.onyx.jdread.main.model.NoneResultModel;

import java.lang.reflect.Method;

/**
 * Created by suicheng on 2018/2/7.
 */

public class ViewCompatUtil {

    public static void disableEditShowSoftInput(EditText editText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod("setShowSoftInputOnFocus", new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception ignored) {
            }
        }
    }

    public static void disableEditShowSoftInput(EditText... editTexts) {
        if (editTexts == null || editTexts.length <= 0) {
            return;
        }
        for (EditText edit : editTexts) {
            disableEditShowSoftInput(edit);
        }
    }

    public static void showNoneResultView(ViewStubProxy viewStubProxy, boolean show, final NoneResultModel model) {
        if (viewStubProxy.isInflated()) {
            NoneResultBinding binding = (NoneResultBinding) viewStubProxy.getBinding();
            binding.layoutNoneResult.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.setViewModel(model);
            return;
        }
        viewStubProxy.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                NoneResultBinding binding = DataBindingUtil.bind(inflated);
                binding.setViewModel(model);
            }
        });
        viewStubProxy.getViewStub().inflate().setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
