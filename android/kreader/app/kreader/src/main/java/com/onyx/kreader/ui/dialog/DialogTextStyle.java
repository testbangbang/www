package com.onyx.kreader.ui.dialog;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.FontInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.data.ReaderTextStyle.Percentage;
import com.onyx.android.sdk.data.ReaderTextStyle.PageMargin;
import com.onyx.android.sdk.ui.view.AlignTextView;
import com.onyx.kreader.R;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.OnyxCustomViewPager;
import com.onyx.android.sdk.ui.view.OnyxRadioButton;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.kreader.ui.actions.GetFontsAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onyx.kreader.ui.dialog.DialogTextStyle.FontLevel.DECREASE;
import static com.onyx.kreader.ui.dialog.DialogTextStyle.FontLevel.INCREASE;
import static com.onyx.kreader.ui.dialog.DialogTextStyle.FontLevel.LARGE;
import static com.onyx.kreader.ui.dialog.DialogTextStyle.FontLevel.NORMAL;
import static com.onyx.kreader.ui.dialog.DialogTextStyle.FontLevel.SMALL;

/**
 * Created by ming on 2016/11/16.
 */

public class DialogTextStyle extends DialogBase {

    public enum FontLevel {
        SMALL, NORMAL, LARGE, INCREASE, DECREASE
    }

    public interface TextStyleCallback {
        void onSaveReaderStyle(ReaderTextStyle readerTextStyle);
    }

    private View fontFaceLine;
    private View fontSpacingLine;
    private OnyxCustomViewPager viewPager;
    private Button btnOk;
    private Button btnCancel;
    private LinearLayout fontFaceLayout;
    private LinearLayout fontSpacingLayout;

    private PageRecyclerView fontPageView;
    private ImageView preIcon;
    private TextView pageSizeIndicator;
    private ImageView nextIcon;

    private List<FontInfo> fonts = new ArrayList<>();
    private int selectIndex = -1;
    private List<View> pageViews = new ArrayList<>();
    private ReaderDataHolder readerDataHolder;
    private CommonViewHolder fontFaceViewHolder;
    private CommonViewHolder fontSpacingViewHolder;
    List<AlignTextView> fontSizeTexts = new ArrayList<>();
    private TextStyleCallback callback;

    public DialogTextStyle(ReaderDataHolder readerDataHolder, TextStyleCallback callback) {
        super(readerDataHolder.getContext());
        this.readerDataHolder = readerDataHolder;
        this.callback = callback;
        setContentView(R.layout.dialog_text_style_view);
        init();
    }

    private void init() {
        fontFaceLine = findViewById(R.id.font_face_bottom_view);
        fontSpacingLine = findViewById(R.id.page_spacing_bottom_view);
        fontFaceLayout = (LinearLayout) findViewById(R.id.font_face_layout);
        fontSpacingLayout = (LinearLayout) findViewById(R.id.page_spacing_layout);
        fontFaceLine.setVisibility(View.VISIBLE);
        fontSpacingLine.setVisibility(View.INVISIBLE);

        viewPager = (OnyxCustomViewPager) findViewById(R.id.view_pager);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        pageViews.add(initFontFaceView());
        pageViews.add(initPageSpacingView());

        viewPager.setAdapter(new ViewPagerAdapter());
        fontFaceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0, false);
                fontFaceLine.setVisibility(View.VISIBLE);
                fontSpacingLine.setVisibility(View.INVISIBLE);
            }
        });
        fontSpacingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1, false);
                fontFaceLine.setVisibility(View.INVISIBLE);
                fontSpacingLine.setVisibility(View.VISIBLE);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    dismiss();
                    callback.onSaveReaderStyle(getReaderStyle());
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        viewPager.setPagingEnabled(false);
    }

    private View initFontFaceView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text_style_sub_font_face_view, null, false);
        fontFaceViewHolder = new CommonViewHolder(view);
        fontPageView = (PageRecyclerView) view.findViewById(R.id.font_page_view);
        preIcon = (ImageView) view.findViewById(R.id.pre_icon);
        pageSizeIndicator = (TextView) view.findViewById(R.id.page_size_indicator);
        nextIcon = (ImageView) view.findViewById(R.id.next_icon);


        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontPageView.nextPage();
            }
        });

        preIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontPageView.prevPage();
            }
        });

        final String fontFace = getReaderStyle().getFontFace();
        final GetFontsAction getFontsAction = new GetFontsAction(fontFace);
        getFontsAction.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                fonts = getFontsAction.getFonts();
                if (fontFace != null) {
                    for (int i = 0; i < fonts.size(); i++) {
                        if (fontFace.equals(fonts.get(i))) {
                            selectIndex = i;
                        }
                    }
                }
                initPageView();
            }
        });

        fontSizeTexts.add((AlignTextView) fontFaceViewHolder.getView(com.onyx.android.sdk.ui.R.id.text_view_font_size_0));
        fontSizeTexts.add((AlignTextView) fontFaceViewHolder.getView(com.onyx.android.sdk.ui.R.id.text_view_font_size_1));
        fontSizeTexts.add((AlignTextView) fontFaceViewHolder.getView(com.onyx.android.sdk.ui.R.id.text_view_font_size_2));
        fontSizeTexts.add((AlignTextView) fontFaceViewHolder.getView(com.onyx.android.sdk.ui.R.id.text_view_font_size_3));
        fontSizeTexts.add((AlignTextView) fontFaceViewHolder.getView(com.onyx.android.sdk.ui.R.id.text_view_font_size_4));
        fontSizeTexts.add((AlignTextView) fontFaceViewHolder.getView(com.onyx.android.sdk.ui.R.id.text_view_font_size_5));
        fontSizeTexts.add((AlignTextView) fontFaceViewHolder.getView(com.onyx.android.sdk.ui.R.id.text_view_font_size_6));
        fontSizeTexts.add((AlignTextView) fontFaceViewHolder.getView(com.onyx.android.sdk.ui.R.id.text_view_font_size_7));
        updateFontSizeTextView(fontSizeTexts, getReaderStyle());
        return view;
    }

    private ReaderTextStyle getReaderStyle() {
        return readerDataHolder.getReaderViewInfo().getReaderTextStyle();
    }

    private void initPageView() {
        fontPageView.setAdapter(new PageRecyclerView.PageAdapter() {
            @Override
            public int getRowCount() {
                return 6;
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public int getDataCount() {
                return fonts.size();
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                CommonViewHolder viewHolder = new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.reader_layer_menu_font_choose_item_view, parent, false));
                return viewHolder;
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;
                final OnyxRadioButton font = viewHolder.getView(R.id.btn_font);
                final FontInfo fontInfo = fonts.get(position);
                font.setText(fontInfo.getName());
                font.setTypeface(fontInfo.getTypeface());
                font.setChecked(selectIndex == position);

                font.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fontPageView.getPageAdapter().notifyItemChanged(selectIndex);
                        selectIndex = position;
                        fontPageView.getPageAdapter().notifyItemChanged(selectIndex);
                        getReaderStyle().setFontFace(fonts.get(position).getId());
                    }
                });
            }
        });

        fontPageView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicator();
            }
        });
    }

    private View initPageSpacingView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text_style_sub_font_spacing_view, null, false);
        fontSpacingViewHolder = new CommonViewHolder(view);

        Map<FontLevel, ImageView> spacingViewMap = new HashMap<>();
        spacingViewMap.put(SMALL, (ImageView) fontSpacingViewHolder.getView(R.id.image_view_small_line_spacing));
        spacingViewMap.put(NORMAL, (ImageView) fontSpacingViewHolder.getView(R.id.image_view_middle_line_spacing));
        spacingViewMap.put(LARGE, (ImageView) fontSpacingViewHolder.getView(R.id.image_view_large_line_spacing));
        spacingViewMap.put(DECREASE, (ImageView) fontSpacingViewHolder.getView(R.id.image_view_decrease_line_spacing));
        spacingViewMap.put(INCREASE, (ImageView) fontSpacingViewHolder.getView(R.id.image_view_increase_line_spacing));
        updateFontSpacingView(spacingViewMap, getReaderStyle());

        Map<FontLevel, ImageView> marginViewMap = new HashMap<>();
        marginViewMap.put(SMALL, (ImageView) fontSpacingViewHolder.getView(R.id.image_view_small_page_margins));
        marginViewMap.put(NORMAL, (ImageView) fontSpacingViewHolder.getView(R.id.image_view_middle_page_margins));
        marginViewMap.put(LARGE, (ImageView) fontSpacingViewHolder.getView(R.id.image_view_large_page_margins));
        marginViewMap.put(DECREASE, (ImageView) fontSpacingViewHolder.getView(R.id.image_view_decrease_page_margins));
        marginViewMap.put(INCREASE, (ImageView) fontSpacingViewHolder.getView(R.id.image_view_increase_page_margins));
        updateFontSpacingView(marginViewMap, getReaderStyle());
        updateFontMarginView(marginViewMap, getReaderStyle());
        return view;
    }

    private void updatePageIndicator() {
        String page = String.format("%d/%d", Math.max(1, fontPageView.getPaginator().getCurrentPage() + 1), Math.max(1, fontPageView.getPaginator().pages()));
        pageSizeIndicator.setText(page);
    }

    private void updateFontSizeTextView(final List<AlignTextView> fontSizeTexts, final ReaderTextStyle readerTextStyle) {
        for (int i = 0; i < fontSizeTexts.size(); i++) {
            AlignTextView fontSizeText = fontSizeTexts.get(i);
            final ReaderTextStyle.SPUnit size = readerTextStyle.FONT_SIZE_LIST[i];
            boolean isSelected = size.equals(readerTextStyle.getFontSize());
            fontSizeText.setTextSize(size.getValue());
            fontSizeText.setActivated(isSelected);
            fontSizeText.setTextColor(isSelected ? getContext().getResources().getColor(android.R.color.white) : getContext().getResources().getColor(android.R.color.black));
            fontSizeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readerTextStyle.setFontSize(size);
                    updateFontSizeTextView(fontSizeTexts, readerTextStyle);
                }
            });
        }
    }

    private void updateFontSpacingView(final Map<FontLevel, ImageView> spacingViewMap, final ReaderTextStyle readerTextStyle) {
        final Percentage percentage = readerTextStyle.getLineSpacing();
        for (final FontLevel fontLevel : spacingViewMap.keySet()) {
            ImageView spacingView = spacingViewMap.get(fontLevel);
            spacingView.setActivated(isSelectCurrentPercentage(readerTextStyle, fontLevel));
            spacingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (fontLevel) {
                        case SMALL:
                            percentage.setPercent(ReaderTextStyle.SMALL_LINE_SPACING.getPercent());
                            break;
                        case NORMAL:
                            percentage.setPercent(ReaderTextStyle.NORMAL_LINE_SPACING.getPercent());
                            break;
                        case LARGE:
                            percentage.setPercent(ReaderTextStyle.LARGE_LINE_SPACING.getPercent());
                            break;
                        case INCREASE:
                            percentage.setPercent(Math.min(ReaderTextStyle.MAX_LINE_SPACING.getPercent(), percentage.getPercent() + ReaderTextStyle.LINE_SPACING_STEP.getPercent()));
                            break;
                        case DECREASE:
                            percentage.setPercent(Math.max(ReaderTextStyle.MIN_LINE_SPACING.getPercent(), percentage.getPercent() - ReaderTextStyle.LINE_SPACING_STEP.getPercent()));
                            break;
                    }
                    readerTextStyle.setLineSpacing(percentage);
                    updateFontSpacingView(spacingViewMap, readerTextStyle);
                }
            });
        }
    }

    private static boolean isSelectCurrentPercentage(ReaderTextStyle readerTextStyle, FontLevel level) {
        switch (level) {
            case SMALL:
                return ReaderTextStyle.SMALL_LINE_SPACING.equals(readerTextStyle.getLineSpacing());
            case NORMAL:
                return ReaderTextStyle.NORMAL_LINE_SPACING.equals(readerTextStyle.getLineSpacing());
            case LARGE:
                return ReaderTextStyle.LARGE_LINE_SPACING.equals(readerTextStyle.getLineSpacing());
            default:
                return false;
        }
    }

    private void updateFontMarginView(final Map<FontLevel, ImageView> marginViewMap, final ReaderTextStyle readerTextStyle) {
        for (final FontLevel fontLevel : marginViewMap.keySet()) {
            ImageView spacingView = marginViewMap.get(fontLevel);
            spacingView.setActivated(isSelectCurrentMargin(readerTextStyle, fontLevel));
            spacingView.setOnClickListener(new View.OnClickListener() {
                PageMargin currentPageMargin = PageMargin.copy(readerTextStyle.getPageMargin());

                @Override
                public void onClick(View v) {
                    switch (fontLevel) {
                        case SMALL:
                            currentPageMargin = ReaderTextStyle.SMALL_PAGE_MARGIN;
                            break;
                        case NORMAL:
                            currentPageMargin = ReaderTextStyle.NORMAL_PAGE_MARGIN;
                            break;
                        case LARGE:
                            currentPageMargin = ReaderTextStyle.LARGE_PAGE_MARGIN;
                            break;
                        case INCREASE:
                            currentPageMargin.increasePageMargin(ReaderTextStyle.PAGE_MARGIN_STEP);
                            break;
                        case DECREASE:
                            currentPageMargin.decreasePageMargin(ReaderTextStyle.PAGE_MARGIN_STEP);
                            break;
                    }
                    readerTextStyle.setPageMargin(currentPageMargin);
                    updateFontMarginView(marginViewMap, readerTextStyle);
                }
            });
        }
    }

    private static boolean isSelectCurrentMargin(ReaderTextStyle readerTextStyle, FontLevel level) {
        switch (level) {
            case SMALL:
                return ReaderTextStyle.SMALL_PAGE_MARGIN.equals(readerTextStyle.getPageMargin());
            case NORMAL:
                return ReaderTextStyle.NORMAL_PAGE_MARGIN.equals(readerTextStyle.getPageMargin());
            case LARGE:
                return ReaderTextStyle.LARGE_PAGE_MARGIN.equals(readerTextStyle.getPageMargin());
            default:
                return false;
        }
    }

    public class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = pageViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pageViews.get(position));
        }
    }
}
