package com.onyx.jdread.shop.model;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/13.
 */

public class BuyReadVipModel {
    private List<BuyReadVipData> list = new ArrayList<>();

    public void loadData() {
        String[] days = JDReadApplication.getInstance().getResources().getStringArray(R.array.VIP_for_days);
        String[] money = JDReadApplication.getInstance().getResources().getStringArray(R.array.VIP_for_paid);
        if (list != null && list.size() > 0) {
            list.clear();
        }

        for (int i = 0; i < days.length; i++) {
            BuyReadVipData buyReadVipData = new BuyReadVipData();
            buyReadVipData.setDays(days[i]);
            buyReadVipData.setMoney(money[i]);
            list.add(buyReadVipData);
        }
    }

    public List<BuyReadVipData> getBuyReadVipData() {
        return list;
    }
}
