package con.onyx.android.libsetting.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import con.onyx.android.libsetting.BR;
import con.onyx.android.libsetting.data.SettingCategory;


/**
 * Created by solskjaer49 on 2016/11/24 19:22.
 */

public class SettingItem extends BaseObservable {

    @Bindable
    public int getIconRes() {
        return iconRes;
    }

    public SettingItem setIconRes(int iconRes) {
        this.iconRes = iconRes;
        notifyPropertyChanged(BR.iconRes);
        return this;
    }

    @Bindable
    public String getTittle() {
        return tittle;
    }

    public SettingItem setTittle(String tittle) {
        this.tittle = tittle;
        notifyPropertyChanged(BR.tittle);
        return this;
    }

    @Bindable
    public String getSubTittle() {
        return subTittle;
    }

    public SettingItem setSubTittle(String subTittle) {
        this.subTittle = subTittle;
        notifyPropertyChanged(BR.subTittle);
        return this;
    }

    @Bindable
    public
    @SettingCategory.SettingCategoryDef
    int getItemCategory() {
        return itemCategory;
    }

    public SettingItem setItemCategory(int itemCategory) {
        this.itemCategory = itemCategory;
        notifyPropertyChanged(BR.itemCategory);
        return this;
    }

    private
    @SettingCategory.SettingCategoryDef
    int itemCategory;
    private int iconRes;
    private String tittle;
    private String subTittle;

    public SettingItem(int itemCategory, int iconRes, String tittle, String subTittle) {
        this.itemCategory = itemCategory;
        this.iconRes = iconRes;
        this.tittle = tittle;
        this.subTittle = subTittle;
    }

}
