package com.onyx.jdread.shop.common;

import com.onyx.jdread.shop.model.BaseSubjectViewModel;
import com.onyx.jdread.shop.model.SubjectViewModel;

import java.util.Comparator;

/**
 * Created by jackdeng on 2018/3/16.
 */

public class RankListComparator implements Comparator<BaseSubjectViewModel> {

    @Override
    public int compare(BaseSubjectViewModel o1, BaseSubjectViewModel o2) {
        SubjectViewModel viewModel1 = (SubjectViewModel) o1;
        SubjectViewModel viewModel2 = (SubjectViewModel) o2;
        int rank_type1 = viewModel1.getModelBean().module_type;
        int rank_type2 = viewModel2.getModelBean().module_type;
        return rank_type1 - rank_type2;
    }
}
