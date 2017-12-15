package com.onyx.edu.homework.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.R;
import com.onyx.edu.homework.base.BaseFragment;
import com.onyx.edu.homework.databinding.FragmentReviewBinding;
import com.onyx.edu.homework.event.PageChangeEvent;
import com.onyx.edu.homework.event.UpdatePagePositionEvent;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/14.
 */

public class ReviewFragment extends BaseFragment {

    private FragmentReviewBinding binding;
    private Question question;
    private List<ImageView> imageViews;
    private List<String> reviewUrls;

    public static ReviewFragment newInstance(Question question) {
        ReviewFragment fragment = new ReviewFragment();
        fragment.setQuestion(question);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review, container, false);
        DataBundle.getInstance().register(this);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        DataBundle.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    private void initView() {
        if (question.review == null) {
            return;
        }
        reviewUrls = question.review.attachment;
        if (reviewUrls == null) {
            return;
        }
        initImageView(reviewUrls.size());
        binding.list.setPagingEnabled(false);
        binding.list.setUseKeyPage(true);
        binding.list.setUseGesturesPage(true);
        binding.list.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return reviewUrls.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = imageViews.get(position);
                Glide.with(getActivity()).load(reviewUrls.get(position)).into(imageView);
                container.addView(imageView);
                return imageView;
            }
        });
        binding.list.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updatePageInfo(position);
                EpdController.invalidate(binding.list, UpdateMode.GC);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updatePageInfo(0);
    }

    private void updatePageInfo(int position) {
        int current = position + 1;
        int total = Math.max(1, reviewUrls.size());
        DataBundle.getInstance().post(new UpdatePagePositionEvent(current + File.separator + total));
    }

    private void initImageView(int size) {
        imageViews = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            imageViews.add(new ImageView(getActivity()));
        }
    }

    @Subscribe
    public void onPageChangeEvent(PageChangeEvent event) {
        if (event.next) {
            nextPage();
        }else {
            prevPage();
        }
    }

    private void nextPage() {
        if (CollectionUtils.isNullOrEmpty(reviewUrls)) {
            return;
        }
        int next = binding.list.getCurrentItem() + 1;
        next = Math.min(reviewUrls.size() - 1, next);
        binding.list.setCurrentItem(next,false);
    }

    private void prevPage() {
        if (CollectionUtils.isNullOrEmpty(reviewUrls)) {
            return;
        }
        int prev = binding.list.getCurrentItem() - 1;
        prev = Math.max(0, prev);
        binding.list.setCurrentItem(prev,false);
    }
}
