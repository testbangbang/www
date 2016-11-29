package com.onyx.android.eschool.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by suicheng on 2016/11/17.
 */

public class TeachingMaterialActivity extends BaseActivity {
    private static final String LAST_SYLLABUS_INDEX = "teaching_material_syllabus_Index";

    @Bind(R.id.syllabus_page_view)
    PageRecyclerView syllabusPageView;

    // about showcase
    @Bind(R.id.text_title)
    TextView bookTitleView;
    @Bind(R.id.text_publisher)
    TextView bookPublisherView;
    @Bind(R.id.text_last_reading_time)
    TextView bookLastReadingTime;
    @Bind(R.id.text_progress)
    TextView bookLastReadingTextProgress;
    @Bind(R.id.progress_line)
    ProgressBar bookProgressBar;
    @Bind(R.id.image_cover)
    ImageView bookCover;

    @Bind(R.id.textView_material_label)
    TextView materialLabelView;

    @Bind(R.id.volume_one_item)
    ImageView bookVolumeOne;
    @Bind(R.id.volume_two_item)
    ImageView bookVolumeTwo;

    private List<String> syllabusList = new ArrayList<>();
    private int syllabusCurrentIndex = 0;

    private List<Metadata> VolumeMetadataCollection = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_teaching_material;
    }

    protected void initConfig() {
        loadCategoryConfig();
    }

    private void loadCategoryConfig() {
        syllabusCurrentIndex = StudentPreferenceManager.getIntValue(this, LAST_SYLLABUS_INDEX, 0);
    }

    private void saveCategoryConfig() {
        StudentPreferenceManager.setIntValue(this, LAST_SYLLABUS_INDEX, syllabusCurrentIndex);
    }

    @Override
    protected void initView() {
        initMaterialLabelView();
        initSyllabusPageView();
    }

    @Override
    protected void initData() {
        loadSyllabusData();
        loadSpecifySyllabusData();
    }

    private void initMaterialLabelView() {
        String materialLabel = StudentPreferenceManager.loadGradeSelected(this, getString(R.string.home_item_teaching_materials_text));
        materialLabel = materialLabel.replace("上", "").replace("下", "") + getString(R.string.home_item_teaching_materials_text);
        materialLabelView.setText(materialLabel);
    }

    private void initSyllabusPageView() {
        syllabusPageView.setLayoutManager(new DisableScrollGridManager(this));
        syllabusPageView.setAdapter(new PageRecyclerView.PageAdapter<SyllabusViewHolder>() {
            @Override
            public int getRowCount() {
                return 2;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public int getDataCount() {
                return getDataSize(syllabusList, -1);
            }

            @Override
            public SyllabusViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new SyllabusViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.teaching_category_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(SyllabusViewHolder viewHolder, int position) {
                viewHolder.itemView.setTag(position);

                viewHolder.titleTextView.setText(syllabusList.get(position));
                viewHolder.chooseImageView.setImageResource(syllabusCurrentIndex == position ?
                        R.drawable.delivery_dot_green : R.drawable.delivery_dot_grey);
            }
        });
    }

    private <T> int getDataSize(List<T> list, int forceValue) {
        return CollectionUtils.isNullOrEmpty(list) ? 0 : (forceValue > 0 ? forceValue : list.size());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isPageViewVisible() && ev.getY() > getSyllabusPageViewHeight()) {
                    toggleSyllabusPageView();
                    return true;
                }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        saveCategoryConfig();
        super.onStop();
    }

    private String getRandomProgress() {
        int max = TestUtils.randInt(199, 999);
        int min = TestUtils.randInt(0, max);
        return min + "/" + max;
    }

    private Date getRandomTime() {
        int diff = TestUtils.randInt(999999999, 1999999999);
        long time = System.currentTimeMillis() - diff;
        return new Date(time);
    }

    private int getRandomBookCover() {
        int[] covers = new int[]{
                R.drawable.sample_cover1, R.drawable.sample_cover2, R.drawable.sample_cover3,
                R.drawable.sample_cover4, R.drawable.sample_cover5, R.drawable.sample_cover6,
                R.drawable.sample_cover7, R.drawable.sample_cover8,};
        return covers[TestUtils.randInt(0, covers.length - 2)];
    }

    private Metadata loadRandomMetadata() {
        Metadata metadata = new Metadata();
        metadata.setTitle(syllabusList.get(syllabusCurrentIndex));
        metadata.setUpdatedAt(getRandomTime());
        metadata.setProgress(getRandomProgress());
        metadata.setPublisher(TestUtils.randString());
        return metadata;
    }

    private void loadSpecifySyllabusData() {
        if (CollectionUtils.isNullOrEmpty(syllabusList)) {
            return;
        }
        VolumeMetadataCollection.clear();
        VolumeMetadataCollection.add(loadRandomMetadata());
        VolumeMetadataCollection.add(loadRandomMetadata());
        updateBookVolume();
        updateShowcaseView(VolumeMetadataCollection.get(0), bookVolumeOne.getDrawable());
    }

    private void loadSyllabusData() {
        String gradeSelected = StudentPreferenceManager.loadGradeSelected(this, "小学一年级");
        String schoolSelected = StudentPreferenceManager.loadSchoolSelected(this, "小学");
        syllabusList.addAll(loadRowSyllabusConfig(schoolSelected, gradeSelected.replace("上", "").replace("下", "")));
        if (syllabusCurrentIndex >= syllabusList.size()) {
            syllabusCurrentIndex = 0;
        }
    }

    private List<String> loadRowSyllabusConfig(String schoolSelected, String gradeSelected) {
        String content = RawResourceUtil.contentOfRawResource(this, R.raw.syllabus_config);
        Map<String, Object> map = JSON.parseObject(content, new TypeReference<Map<String, Object>>() {
        });
        Map<String, List<String>> dataMap = (Map<String, List<String>>) map.get(schoolSelected);
        if (CollectionUtils.isNullOrEmpty(dataMap)) {
            return new ArrayList<>();
        }
        return dataMap.get(gradeSelected);
    }

    private void updateBookVolume() {
        bookVolumeOne.setImageResource(getRandomBookCover());
        bookVolumeTwo.setImageResource(getRandomBookCover());
    }

    private void updateShowcaseView(Metadata metadata, Object image) {
        bookTitleView.setText(metadata.getTitle());
        bookPublisherView.setText(metadata.getPublisher());
        bookLastReadingTime.setText(DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMM.format(metadata.getUpdatedAt()));
        bookLastReadingTextProgress.setText(getString(R.string.page) + metadata.getProgress());
        if (metadata.getProgress().contains("/")) {
            String[] progressInfo = metadata.getProgress().split("/");
            bookProgressBar.setProgress(getProgress(progressInfo));
        }
        loadImage(bookCover, image);
    }

    private void loadImage(ImageView imageView, Object image) {
        if (image instanceof Integer) {
            imageView.setImageResource((Integer) image);
        } else if (image instanceof Bitmap) {
            imageView.setImageBitmap((Bitmap) image);
        } else if (image instanceof String) {
            imageView.setImageBitmap(BitmapFactory.decodeFile((String) image));
        } else if (image instanceof Drawable) {
            BitmapDrawable bitmap = (BitmapDrawable) image;
            imageView.setImageBitmap(bitmap.getBitmap());
        }
    }

    private int getProgress(String[] progressInfo) {
        int progress = 0;
        try {
            progress = Integer.parseInt(progressInfo[0]) * 100 / Integer.parseInt(progressInfo[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progress;
    }

    @OnClick(R.id.volume_one_layout)
    void onVolumeOneClick() {
        updateShowcaseView(VolumeMetadataCollection.get(0), bookVolumeOne.getDrawable());
    }

    @OnClick(R.id.volume_two_layout)
    void onVolumeTwoClick() {
        updateShowcaseView(VolumeMetadataCollection.get(1), bookVolumeTwo.getDrawable());
    }

    @OnClick(R.id.showcase_id)
    void onShowCaseClick() {
    }

    @Override
    public void onBackPressed() {
        if (hideSyllabusPageView()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                toggleSyllabusPageView();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean hideSyllabusPageView() {
        boolean visible = isPageViewVisible();
        if (visible) {
            toggleSyllabusPageView();
        }
        return visible;
    }

    private boolean isPageViewVisible() {
        return syllabusPageView.getVisibility() == View.VISIBLE;
    }

    private int getSyllabusPageViewHeight() {
        return syllabusPageView.getMeasuredHeight();
    }

    private void toggleSyllabusPageView() {
        syllabusPageView.setVisibility(isPageViewVisible() ? View.GONE : View.VISIBLE);
    }

    private void processSyllabusItemClick(int position) {
        syllabusPageView.getAdapter().notifyItemChanged(syllabusCurrentIndex);
        syllabusPageView.getAdapter().notifyItemChanged(position);
        syllabusCurrentIndex = position;
        loadSpecifySyllabusData();
    }

    class SyllabusViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title_text)
        TextView titleTextView;
        @Bind(R.id.choose_image)
        ImageView chooseImageView;

        public SyllabusViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processSyllabusItemClick((Integer) itemView.getTag());
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }
}
