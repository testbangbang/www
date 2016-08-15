package com.onyx.android.edu.wrongquestions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.edu.R;
import com.onyx.android.edu.base.BaseFragment;
import com.onyx.android.edu.testpaper.TestPaperActivity;

import butterknife.Bind;

/**
 * Created by ming on 16/6/28.
 */
public class WrongQuestionFragment extends BaseFragment implements WrongQuestionContract.View {

    @Bind(R.id.wrong_layout)
    LinearLayout mWrongLayout;
    private WrongQuestionContract.Presenter mPresenter;

    private int rowMaxCount = 4;

    public static WrongQuestionFragment newInstance() {
        return new WrongQuestionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wrong_question;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        mPresenter.subscribe();
    }

    @Override
    protected void initData() {

        String[] key = {"语文","数学","英语","物理","生物","地理","历史","政治"};
        String[] value = {"15","35","10","5","2","0","0","0"};

        int row = key.length < rowMaxCount ? 1 : key.length/rowMaxCount;
        for (int i = 0; i < row; i++) {
            LinearLayout linear = generateNewLinearLayout();
            int size = key.length < rowMaxCount ? key.length : rowMaxCount;
            for (int b = 0; b < size; b++) {
                View view =  getItemView(key[b + i*size],value[b + i*size]);
                linear.addView(view);
            }
            mWrongLayout.addView(linear);
        }
    }

    @Override
    public void setPresenter(WrongQuestionContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unSubscribe();
    }

    private LinearLayout generateNewLinearLayout(){
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,20);
        linearLayout.setLayoutParams(layoutParams);
        return linearLayout;
    }

    private View getItemView(String key,String value){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_wrong_question_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mButton.setText(value);
        viewHolder.mTextView.setText(key);
        viewHolder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TestPaperActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    static class ViewHolder{

        private Button mButton;
        private TextView mTextView;

        public ViewHolder(View view){
            mButton = (Button)view.findViewById(R.id.item_button);
            mTextView = (TextView)view.findViewById(R.id.item_name);
        }
    }
}
