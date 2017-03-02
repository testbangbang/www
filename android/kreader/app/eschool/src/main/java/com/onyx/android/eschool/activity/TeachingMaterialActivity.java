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
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.eschool.R;
import com.onyx.android.eschool.model.AppConfig;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2016/11/17.
 */

public class TeachingMaterialActivity extends BaseActivity {
    private static final String LAST_SYLLABUS_INDEX = "teaching_material_syllabus_Index";

    @Bind(R.id.syllabus_page_view)
    PageRecyclerView syllabusPageView;

    @Bind(R.id.content_page_view)
    PageRecyclerView contentPageView;

    @Bind(R.id.textView_user_grade)
    TextView gradeLabelTextView;

    private List<String> syllabusList = new ArrayList<>();
    private int syllabusCurrentIndex = 0;

    private List<Metadata> metadataList = new ArrayList<>();

    private String documentDisplayPath = "/mnt/sdcard/slide/sample-cfa-png.pdf";

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
        initAppConfig();
    }

    private void initAppConfig() {
        String path = AppConfig.sharedInstance(this).getTeachingMaterialDocumentDisplayFilePath();
        if (StringUtils.isNotBlank(path)) {
            documentDisplayPath = path;
        }
    }

    private void loadCategoryConfig() {
        syllabusCurrentIndex = StudentPreferenceManager.getIntValue(this, LAST_SYLLABUS_INDEX, 0);
    }

    private void saveCategoryConfig() {
        StudentPreferenceManager.setIntValue(this, LAST_SYLLABUS_INDEX, syllabusCurrentIndex);
    }

    @Override
    protected void initView() {
        initSyllabusPageView();
        initContentPageView();
    }

    @Override
    protected void initData() {
        loadSyllabusData();
        loadSpecifySyllabusData();
        loadContentData();
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

    private void initContentPageView() {
        contentPageView.setLayoutManager(new DisableScrollGridManager(this));
        contentPageView.setAdapter(new PageRecyclerView.PageAdapter<ContentViewHolder>() {
            @Override
            public int getRowCount() {
                return 3;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public int getDataCount() {
                return getDataSize(metadataList, -1);
            }

            @Override
            public ContentViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.teaching_content_item, parent, false));
            }

            @Override
            public void onPageBindViewHolder(ContentViewHolder viewHolder, int position) {
                viewHolder.itemView.setTag(position);

                loadImage(viewHolder.bookCover, getBookCover(position));
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
                    //toggleSyllabusPageView();
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
        int[] covers = getBookCovers();
        return covers[TestUtils.randInt(0, covers.length - 1)];
    }

    private int[] getBookCovers() {
        return new int[]{
                R.drawable.sample_cover1, R.drawable.sample_cover2, R.drawable.sample_cover3,
                R.drawable.sample_cover4, R.drawable.sample_cover5, R.drawable.sample_cover6,
                R.drawable.sample_cover7, R.drawable.sample_cover8, R.drawable.sample_cover9,
                R.drawable.sample_cover10, R.drawable.sample_cover11, R.drawable.sample_cover12,
                R.drawable.sample_cover13};
    }

    private int getBookCover(int position) {
        int[] covers = getBookCovers();
        return covers[position % covers.length];
    }

    private Metadata loadRandomMetadata() {
        Metadata metadata = new Metadata();
        if (!CollectionUtils.isNullOrEmpty(syllabusList)) {
            metadata.setTitle(syllabusList.get(syllabusCurrentIndex));
        }
        metadata.setUpdatedAt(getRandomTime());
        metadata.setProgress(getRandomProgress());
        metadata.setPublisher(TestUtils.randString());
        return metadata;
    }

    private void loadSyllabusData() {
        String gradeSelected = StudentPreferenceManager.loadGradeSelected(this, "小学一年级");
        String schoolSelected = StudentPreferenceManager.loadSchoolSelected(this, "小学");
        gradeLabelTextView.setText(gradeSelected);
        List<String> syllabusConfigList = loadRowSyllabusConfig(schoolSelected, gradeSelected.replace("上", "").replace("下", ""));
        if (syllabusConfigList != null) {
            syllabusList.addAll(syllabusConfigList);
        }
        if (syllabusCurrentIndex >= syllabusList.size()) {
            syllabusCurrentIndex = 0;
        }
    }

    private void loadContentData() {
        metadataList.clear();
        int N = TestUtils.randInt(getBookCovers().length, 19);
        for (int i = 0; i < N; i++) {
            metadataList.add(loadRandomMetadata());
        }
        contentPageView.getAdapter().notifyDataSetChanged();
    }

    private List<String> loadRowSyllabusConfig(String schoolSelected, String gradeSelected) {
        String content = RawResourceUtil.contentOfRawResource(this, R.raw.syllabus_config);
        Map<String, Object> map = JSON.parseObject(content, new TypeReference<Map<String, Object>>() {
        });
        Map<String, List<String>> dataMap = (Map<String, List<String>>) map.get(schoolSelected);
        if (CollectionUtils.isNullOrEmpty(dataMap) || !dataMap.containsKey(gradeSelected)) {
            return new ArrayList<>();
        }
        return dataMap.get(gradeSelected);
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
                //toggleSyllabusPageView();
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
        int height = syllabusPageView.getMeasuredHeight();
        int[] location = new int[2];
        syllabusPageView.getLocationOnScreen(location);
        height += location[1];
        return height;
    }

    private void toggleSyllabusPageView() {
        syllabusPageView.setVisibility(isPageViewVisible() ? View.GONE : View.VISIBLE);
    }

    private void loadSpecifySyllabusData() {

    }

    private void processSyllabusItemClick(int position) {
        syllabusPageView.getAdapter().notifyItemChanged(syllabusCurrentIndex);
        syllabusPageView.getAdapter().notifyItemChanged(position);
        syllabusCurrentIndex = position;
        loadSpecifySyllabusData();
    }

    private void processContentItemClick(int position) {
        ActivityUtil.startActivitySafely(this,
                ViewDocumentUtils.viewActionIntentWithMimeType(new File(documentDisplayPath)),
                ViewDocumentUtils.getReaderComponentName(this));
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

    class ContentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imageView_book_image)
        ImageView bookCover;

        public ContentViewHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processContentItemClick((Integer) itemView.getTag());
                }
            });
            ButterKnife.bind(this, itemView);
        }
    }
}
