package com.onyx.jdread.reader.menu.actions;

import android.databinding.ObservableBoolean;

import com.onyx.jdread.databinding.ReaderSettingMenuBinding;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderSettingShowMenuAction extends BaseReaderAction {
    private ReaderSettingMenuBinding binding;
    private List<Integer> showMenuId = new ArrayList<>();

    public ReaderSettingShowMenuAction(ReaderSettingMenuBinding binding, List<Integer> showMenuId) {
        this.binding = binding;
        this.showMenuId.addAll(showMenuId);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        showMenu();
    }

    private void showMenu() {
        int id = binding.readerSettingSystemBar.getRoot().getId();
        if (showMenuId.indexOf(id) >= 0) {
            binding.readerSettingSystemBar.getSystemBarModel().setIsShow(true);
        } else {
            binding.readerSettingSystemBar.getSystemBarModel().setIsShow(false);
        }

        id = binding.readerSettingTitleBar.getRoot().getId();
        if (showMenuId.indexOf(id) >= 0) {
            binding.readerSettingTitleBar.getReaderTitleBarModel().setIsShow(true);
        } else {
            binding.readerSettingTitleBar.getReaderTitleBarModel().setIsShow(false);
        }

        id = binding.readerSettingPageInfoBar.getRoot().getId();
        if (showMenuId.indexOf(id) >= 0) {
            binding.readerSettingPageInfoBar.getReaderPageInfoModel().setIsShow(true);
        } else {
            binding.readerSettingPageInfoBar.getReaderPageInfoModel().setIsShow(false);
        }

        id = binding.readerSettingBrightnessBar.getRoot().getId();
        if (showMenuId.indexOf(id) >= 0) {
            binding.readerSettingBrightnessBar.getReaderBrightnessModel().setIsShow(true);
        } else {
            binding.readerSettingBrightnessBar.getReaderBrightnessModel().setIsShow(false);
        }

        id = binding.readerSettingTextSettingBar.getRoot().getId();
        if (showMenuId.indexOf(id) >= 0) {
            binding.readerSettingTextSettingBar.getReaderTextModel().setIsShow(true);
        } else {
            binding.readerSettingTextSettingBar.getReaderTextModel().setIsShow(false);
        }

        id = binding.readerSettingImageSettingBar.getRoot().getId();
        if (showMenuId.indexOf(id) >= 0) {
            binding.readerSettingImageSettingBar.getReaderImageModel().setIsShow(true);
        } else {
            binding.readerSettingImageSettingBar.getReaderImageModel().setIsShow(false);
        }

        id = binding.readerSettingCustomizeFormatBar.getRoot().getId();
        if (showMenuId.indexOf(id) >= 0) {
            binding.readerSettingCustomizeFormatBar.getReaderCustomizeModel().setIsShow(true);
        } else {
            binding.readerSettingCustomizeFormatBar.getReaderCustomizeModel().setIsShow(false);
        }

        id = binding.readerSettingFunctionBar.getRoot().getId();
        if (showMenuId.indexOf(id) >= 0) {
            binding.readerSettingFunctionBar.getFunctionBarModel().setIsShow(true);
        } else {
            binding.readerSettingFunctionBar.getFunctionBarModel().setIsShow(false);
        }
    }
}
