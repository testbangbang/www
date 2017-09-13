package com.onyx.android.dr.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.DictLanguageTypeAdapter;
import com.onyx.android.dr.adapter.SelectDictAdapter;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.bean.LanguageTypeBean;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.dialog.SelectAlertDialog;
import com.onyx.android.dr.interfaces.DictSettingView;
import com.onyx.android.dr.presenter.DictSettingPresenter;
import com.onyx.android.dr.util.Utils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by zhouzhiming on 2017/9/12.
 */
public class DictSettingActivity extends BaseActivity implements DictSettingView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.title_bar_title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_right_icon_four)
    ImageView iconFour;
    @Bind(R.id.dict_setting_activity_first_spinner)
    Spinner firstSpinner;
    @Bind(R.id.dict_setting_activity_second_spinner)
    Spinner secondSpinner;
    @Bind(R.id.dict_setting_activity_third_spinner)
    Spinner thirdSpinner;
    @Bind(R.id.dict_setting_activity_first_select_dict)
    TextView firstSelectDict;
    @Bind(R.id.dict_setting_activity_second_select_dict)
    TextView secondSelectDict;
    @Bind(R.id.dict_setting_activity_third_select_dict)
    TextView thirdSelectDict;
    private DictLanguageTypeAdapter languageTypeAdapter;
    private DictSettingPresenter dictSettingPresenter;
    private List<LanguageTypeBean> languageData;
    private List<LanguageTypeBean> saveLanguageData;
    private int firstSequenceType = Constants.ENGLISH_TYPE;
    private int secondSequenceType = Constants.CHINESE_TYPE;
    private int thirdSequenceType = Constants.OTHER_TYPE;
    private LanguageTypeBean firstBean;
    private LanguageTypeBean secondBean;
    private LanguageTypeBean thirdBean;
    private SelectAlertDialog selectTimeDialog;
    private TextView dialogTitle;
    private PageRecyclerView resultView;
    private Button confirm;
    private Button cancel;
    private List<DictTypeBean> pathList;
    private SelectDictAdapter selectDictAdapter;
    private ArrayList<Boolean> listCheck;
    private DividerItemDecoration dividerItemDecoration;
    private int jumpSource;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_dict_setting;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initRecyclerView();
        loadDialog();
    }

    private void initRecyclerView() {
    }

    @Override
    protected void initData() {
        listCheck = new ArrayList<>();
        pathList = new ArrayList<>();
        languageData = new ArrayList<>();
        saveLanguageData = new ArrayList<>();
        languageTypeAdapter = new DictLanguageTypeAdapter(this);
        dividerItemDecoration = new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        resultView.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        resultView.addItemDecoration(dividerItemDecoration);
        selectDictAdapter = new SelectDictAdapter();
        dictSettingPresenter = new DictSettingPresenter(this);
        dictSettingPresenter.getData();
        getIntentData();
        initTitleData();
        initEvent();
    }

    private void getIntentData() {
        jumpSource = getIntent().getIntExtra(Constants.JUMP_SOURCE, -1);
    }

    private void initTitleData() {
        image.setImageResource(R.drawable.ic_reader_top_setting);
        title.setText(getString(R.string.menu_settings));
        iconFour.setVisibility(View.VISIBLE);
        iconFour.setImageResource(R.drawable.ic_reader_note_diary_save);
    }

    @Override
    public void setDictSettingData(List<LanguageTypeBean> dataList) {
        if (dataList == null || dataList.size() <= 0) {
            return;
        }
        languageData = dataList;
        languageTypeAdapter.setDatas(languageData);
        firstSpinner.setAdapter(languageTypeAdapter);
        secondSpinner.setAdapter(languageTypeAdapter);
        thirdSpinner.setAdapter(languageTypeAdapter);
        initSpinnerData();
    }

    private void initSpinnerData() {
        firstSpinner.setSelection(0);
        secondSpinner.setSelection(1);
        thirdSpinner.setSelection(2);
        firstBean = languageData.get(0);
        secondBean = languageData.get(1);
        thirdBean = languageData.get(2);
    }

    public void initEvent() {
        firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LanguageTypeBean languageTypeBean = languageData.get(position);
                firstBean = languageTypeBean;
                firstSequenceType = languageTypeBean.getType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        secondSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LanguageTypeBean languageTypeBean = languageData.get(position);
                secondBean = languageTypeBean;
                secondSequenceType = languageTypeBean.getType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        thirdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LanguageTypeBean languageTypeBean = languageData.get(position);
                thirdBean = languageTypeBean;
                thirdSequenceType = languageTypeBean.getType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadDialog() {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(
                R.layout.dialog_dict_select, null);
        selectTimeDialog = new SelectAlertDialog(this);
        // find id
        dialogTitle = (TextView) view.findViewById(R.id.dict_select_dialog_title);
        resultView = (PageRecyclerView) view.findViewById(R.id.dict_select_dialog_recycler_view);
        confirm = (Button) view.findViewById(R.id.dict_select_dialog_confirm);
        cancel = (Button) view.findViewById(R.id.dict_select_dialog_cancel);
        WindowManager.LayoutParams attributes = selectTimeDialog.getWindow().getAttributes();
        Float heightProportion = Float.valueOf(getString(R.string.dict_select_dialog_height));
        Float widthProportion = Float.valueOf(getString(R.string.dict_select_dialog_width));
        attributes.height = (int) (Utils.getScreenHeight(DRApplication.getInstance()) * heightProportion);
        attributes.width = (int) (Utils.getScreenWidth(DRApplication.getInstance()) * widthProportion);
        selectTimeDialog.getWindow().setAttributes(attributes);
        selectTimeDialog.setView(view);
    }

    private void loadDialogData(final int type) {
        pathList.clear();
        listCheck.clear();
        if (type == Constants.ENGLISH_TYPE) {
            dialogTitle.setText(getString(R.string.dict_query_language) + getString(R.string.dictionary_key_lists));
            pathList = Utils.getDictName(Constants.ENGLISH_DICTIONARY);
        } else if (type == Constants.CHINESE_TYPE) {
            pathList = Utils.getDictName(Constants.CHINESE_DICTIONARY);
            dialogTitle.setText(getString(R.string.dict_query_chinese_language) + getString(R.string.dictionary_key_lists));
        } else if (type == Constants.OTHER_TYPE) {
            pathList = Utils.getDictName(Constants.OTHER_DICTIONARY);
            dialogTitle.setText(getString(R.string.Japanese) + getString(R.string.dictionary_key_lists));
        }
        listCheck = Utils.getCheckedList(pathList);
        selectDictAdapter.setDataList(pathList, listCheck);
        resultView.setAdapter(selectDictAdapter);
        selectDictAdapter.setOnItemListener(new SelectDictAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClick(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }

            @Override
            public void setOnItemCheckedChanged(int position, boolean isCheck) {
                listCheck.set(position, isCheck);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectTimeDialog.isShowing()) {
                    selectTimeDialog.cancel();
                }
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<DictTypeBean> data = getData(listCheck, pathList);
                if (data == null || data.isEmpty()) {
                    CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getString(R.string.please_select_dict));
                    return;
                }
                dictSettingPresenter.saveSelectDict(type, listCheck, pathList);
                if (selectTimeDialog.isShowing()) {
                    selectTimeDialog.cancel();
                }
            }
        });
        selectTimeDialog.show();
    }

    private List<DictTypeBean> getData(ArrayList<Boolean> listCheck, List<DictTypeBean> list) {
        List<DictTypeBean> selectList = new ArrayList<>();
        for (int i = 0, j = list.size(); i < j; i++) {
            Boolean aBoolean = listCheck.get(i);
            if (aBoolean) {
                DictTypeBean bean = list.get(i);
                if (!selectList.contains(bean)) {
                    selectList.add(bean);
                }
            }
        }
        return selectList;
    }

    @OnClick({R.id.image_view_back,
            R.id.dict_setting_activity_first_select_dict,
            R.id.dict_setting_activity_second_select_dict,
            R.id.dict_setting_activity_third_select_dict,
            R.id.title_bar_right_icon_four})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.title_bar_right_icon_four:
                saveData();
                break;
            case R.id.dict_setting_activity_first_select_dict:
                loadDialogData(firstSequenceType);
                break;
            case R.id.dict_setting_activity_second_select_dict:
                loadDialogData(secondSequenceType);
                break;
            case R.id.dict_setting_activity_third_select_dict:
                loadDialogData(thirdSequenceType);
                break;
        }
    }

    private void saveData() {
        if (Utils.compareWhetherEqual(firstSequenceType, secondSequenceType, thirdSequenceType)) {
            CommonNotices.showMessage(DictSettingActivity.this, getString(R.string.select_language_hint));
            return;
        }
        saveLanguageData.clear();
        saveLanguageData.add(firstBean);
        saveLanguageData.add(secondBean);
        saveLanguageData.add(thirdBean);
        dictSettingPresenter.saveSettingData(saveLanguageData);
        finish();
        if (jumpSource == Constants.DICT_QUERY) {
            ActivityManager.startDictQueryActivity(DRApplication.getInstance());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
