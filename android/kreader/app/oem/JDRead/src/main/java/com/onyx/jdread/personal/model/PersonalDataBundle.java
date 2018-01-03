package com.onyx.jdread.personal.model;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderUrlResultBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadOverInfoBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadTotalInfoBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.TopUpValueBean;
import com.onyx.jdread.shop.model.BookDetailViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class PersonalDataBundle {
    private static PersonalDataBundle personalDataBundle;
    private EventBus eventBus;
    private PersonalViewModel personalViewModel;
    private DataManager dataManager;
    private PersonalModel personalModel;
    private TitleBarModel titleModel;
    private PersonalAccountModel personalAccountModel;
    private List<TopUpValueBean> topValueBeans;
    private GetOrderUrlResultBean orderUrlResultBean;
    private ReadTotalInfoBean readTotalInfo;
    private ReadOverInfoBean readOverInfo;
    private PointsForModel pointsForModel;

    private PersonalDataBundle() {

    }

    public static PersonalDataBundle getInstance() {
        if (personalDataBundle == null) {
            synchronized (BookDetailViewModel.class) {
                if (personalDataBundle == null) {
                    personalDataBundle = new PersonalDataBundle();
                }
            }
        }
        return personalDataBundle;
    }

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = EventBus.getDefault();
        }
        return eventBus;
    }

    public PersonalViewModel getPersonalViewModel() {
        if (personalViewModel == null) {
            synchronized (PersonalViewModel.class) {
                if (personalViewModel == null) {
                    personalViewModel = new PersonalViewModel(getEventBus());
                }
            }
        }
        return personalViewModel;
    }

    public DataManager getDataManager() {
        if (dataManager == null) {
            synchronized (DataManager.class) {
                if (dataManager == null) {
                    dataManager = new DataManager();
                }
            }
        }
        return dataManager;
    }

    public TitleBarModel getTitleModel() {
        if (titleModel == null) {
            titleModel = new TitleBarModel(getEventBus());
        }
        return titleModel;
    }

    public PersonalModel getPersonalModel() {
        if (personalModel == null) {
            personalModel = new PersonalModel();
            personalModel.loadPersonalData();
        }
        return personalModel;
    }

    public PersonalAccountModel getPersonalAccountModel() {
        if (personalAccountModel == null) {
            personalAccountModel = new PersonalAccountModel();
        }
        return personalAccountModel;
    }

    public void setTopValueBeans(List<TopUpValueBean> topValueBeans) {
        this.topValueBeans = topValueBeans;
    }

    public List<TopUpValueBean> getTopValueBeans() {
        return topValueBeans;
    }

    public void setOrderUrlResultBean(GetOrderUrlResultBean orderUrlResultBean) {
        this.orderUrlResultBean = orderUrlResultBean;
    }

    public GetOrderUrlResultBean getOrderUrlResultBean() {
        return orderUrlResultBean;
    }

    public void setReadTotalInfo(ReadTotalInfoBean readTotalInfo) {
        this.readTotalInfo = readTotalInfo;
    }

    public ReadTotalInfoBean getReadTotalInfo() {
        return readTotalInfo;
    }

    public void setReadOverInfo(ReadOverInfoBean readOverInfo) {
        this.readOverInfo = readOverInfo;
    }

    public ReadOverInfoBean getReadOverInfo() {
        return readOverInfo;
    }

    public PointsForModel getPointsForModel() {
        if (pointsForModel == null) {
            pointsForModel = new PointsForModel();
            pointsForModel.loadData();
        }
        return pointsForModel;
    }
}
