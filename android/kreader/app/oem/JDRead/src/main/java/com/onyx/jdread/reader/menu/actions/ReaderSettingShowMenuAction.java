package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.databinding.ReaderSettingMenuBinding;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.model.ReaderSettingModel;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderSettingShowMenuAction extends BaseReaderAction {
    private ReaderSettingMenuBinding binding;
    private ReaderSettingModel.ReaderSystemMenuGroup menuGroup;

    public ReaderSettingShowMenuAction(ReaderSettingMenuBinding binding, ReaderSettingModel.ReaderSystemMenuGroup menuGroup) {
        this.binding = binding;
        this.menuGroup = menuGroup;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        showMenu();
    }

    private void showMenu() {
        boolean isSystemMenuShow = false;
        if (menuGroup == ReaderSettingModel.ReaderSystemMenuGroup.progressMenuGroup ||
                menuGroup == ReaderSettingModel.ReaderSystemMenuGroup.brightnessMenuGroup) {
            isSystemMenuShow = true;
        }
        binding.readerSettingSystemBar.getSystemBarModel().setIsShow(isSystemMenuShow);
        binding.readerSettingTitleBar.getReaderTitleBarModel().setIsShow(isSystemMenuShow);
        binding.readerSettingFunctionBar.getFunctionBarModel().setIsShow(isSystemMenuShow);

        binding.readerSettingPageInfoBar.getReaderPageInfoModel().setIsShow(ReaderSettingModel.ReaderSystemMenuGroup.progressMenuGroup == menuGroup ? true : false);
        binding.readerSettingBrightnessBar.getBrightnessModel().setIsShow(ReaderSettingModel.ReaderSystemMenuGroup.brightnessMenuGroup == menuGroup ? true : false);

        binding.readerSettingTextSettingBar.getReaderTextModel().setIsShow(ReaderSettingModel.ReaderSystemMenuGroup.textMenuGroup == menuGroup ? true : false);

        binding.readerSettingImageSettingBar.getReaderImageModel().setIsShow(ReaderSettingModel.ReaderSystemMenuGroup.imageMenuGroup == menuGroup ? true : false);

        binding.readerSettingCustomizeFormatBar.getReaderMarginModel().setIsShow(ReaderSettingModel.ReaderSystemMenuGroup.customMenuGroup == menuGroup ? true : false);
    }
}
