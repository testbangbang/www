package com.onyx.android.edu.teststype;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.chapter.ChapterTypeActivity;
import com.onyx.android.edu.view.TestsTypeItemView;

import butterknife.Bind;

/**
 * Created by ming on 16/6/28.
 */
public class TestsTypeFragment extends BaseFragment implements TestsTypeContract.TestsTypeView {

    @Bind(R.id.type_layout)
    LinearLayout mTypeLayout;
    @Bind(R.id.select_chapter)
    Button mSelectChapter;

    private TestsTypeContract.TestsTypePresenter mPresenter;

    public static TestsTypeFragment newInstance() {
        return new TestsTypeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tests_type;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        mPresenter.subscribe();
        loadData();

        mSelectChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChapterTypeActivity.class));
            }
        });
    }

    private void loadData(){
        int defaultMargin = 10;
        TestsTypeItemView.Attribute defaultAttribute = new TestsTypeItemView.Attribute();
        defaultAttribute.checkBoxWidth = 0;
        defaultAttribute.groupLeftMargin = defaultMargin;
        defaultAttribute.groupRightMargin = defaultMargin;
        defaultAttribute.groupTopMargin = defaultMargin;
        defaultAttribute.groupBottomMargin = defaultMargin;

        TestsTypeItemView testsTypeItemView = new TestsTypeItemView(getActivity());
        String[] selection = {"小学", "初中", "高中"};
        String name = "学段";
        TestsTypeItemView.Attribute attribute = new TestsTypeItemView.Attribute();
        attribute.checkBoxWidth = 100;
        attribute.groupLeftMargin = 100;
        attribute.groupTopMargin = 10;
        attribute.groupBottomMargin = 10;
        testsTypeItemView.setTypeData(attribute, name, selection);
        mTypeLayout.addView(testsTypeItemView);

        testsTypeItemView = new TestsTypeItemView(getActivity());
        String[] textbooks = {"公共版本", "人教版", "北师大版", "华师大版", "苏教版",
                "鲁教版", "浙教版", "录教版", "粤教版", "清华大学版"};
        name = "教材";
        testsTypeItemView.setTypeData(defaultAttribute, name, textbooks);
        mTypeLayout.addView(testsTypeItemView);

        testsTypeItemView = new TestsTypeItemView(getActivity());
        String[] subject = {"语文", "数学", "英语", "物理", "化学",
                "生物", "地理", "历史", "政治", "政治"};
        name = "科目";
        defaultAttribute.isOval = true;
        testsTypeItemView.setTypeData(defaultAttribute, name, subject);
        mTypeLayout.addView(testsTypeItemView);

        testsTypeItemView = new TestsTypeItemView(getActivity());
        String[] subsection = {"七年级上", "七年级下", "八年级上", "八年级下", "九年级上", "九年级下"};
        name = "分段";
        defaultAttribute.countOfLine = 2;
        defaultAttribute.isOval = false;
        testsTypeItemView.setTypeData(defaultAttribute, name, subsection);
        mTypeLayout.addView(testsTypeItemView);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void setPresenter(TestsTypeContract.TestsTypePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
    }
}
