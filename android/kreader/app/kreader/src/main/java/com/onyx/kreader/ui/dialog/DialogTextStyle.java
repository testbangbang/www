package com.onyx.kreader.ui.dialog;

import android.content.DialogInterface;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neverland.engbook.forpublic.TAL_CODE_PAGES;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.FontInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.data.ReaderTextStyle.PageMargin;
import com.onyx.android.sdk.data.ReaderTextStyle.Percentage;
import com.onyx.android.sdk.ui.view.AlignTextView;
import com.onyx.android.sdk.ui.view.CommonViewHolder;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.onyx.android.sdk.ui.view.OnyxCustomViewPager;
import com.onyx.android.sdk.ui.view.OnyxRadioButton;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.actions.ChangeCodePageAction;
import com.onyx.kreader.ui.actions.ChangeStyleAction;
import com.onyx.kreader.ui.actions.GetFontsAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.data.SingletonSharedPreference;

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

    private static Pair<Integer, Integer>[] CODE_PAGES = new Pair[] {
            new Pair(TAL_CODE_PAGES.AUTO, R.string.code_page_auto),
            new Pair(TAL_CODE_PAGES.CP65001, R.string.code_page_utf8),
            new Pair(TAL_CODE_PAGES.CP1200, R.string.code_page_utf16_le),
            new Pair(TAL_CODE_PAGES.CP1201, R.string.code_page_utf16_be),
            new Pair(TAL_CODE_PAGES.CP936, R.string.code_page_simplified_chinese),
            new Pair(TAL_CODE_PAGES.CP950, R.string.code_page_traditional_chinese),
            new Pair(TAL_CODE_PAGES.CP932, R.string.code_page_japanese),
            new Pair(TAL_CODE_PAGES.CP949, R.string.code_page_korean),
            new Pair(TAL_CODE_PAGES.CP1258, R.string.code_page_vietnam),
            new Pair(TAL_CODE_PAGES.CP874, R.string.code_page_thai),
            new Pair(TAL_CODE_PAGES.CP866, R.string.code_page_russian_oem),
            new Pair(TAL_CODE_PAGES.CP1251, R.string.code_page_russian_ansi),
            new Pair(TAL_CODE_PAGES.CP20866, R.string.code_page_russian_koi8_r),
            new Pair(TAL_CODE_PAGES.CP10007, R.string.code_page_cyrillic_mac),
            new Pair(TAL_CODE_PAGES.CP10017, R.string.code_page_ukraine_mac),
            new Pair(TAL_CODE_PAGES.CP1252, R.string.code_page_latin_i),
            new Pair(TAL_CODE_PAGES.CP1255, R.string.code_page_hebrew),
            new Pair(TAL_CODE_PAGES.CP1256, R.string.code_page_arabic),
    };

    private FontInfo defaultFont = new FontInfo();

    private View fontFaceLine;
    private View fontSpacingLine;
    private View codePageLine;
    private OnyxCustomViewPager viewPager;
    private Button btnOk;
    private Button btnCancel;
    private LinearLayout fontFaceLayout;
    private LinearLayout fontSpacingLayout;
    private LinearLayout codePageLayout;
    private ImageView decreaseIcon;
    private ImageView increaseIcon;

    private List<FontInfo> fonts = new ArrayList<>();
    private int selectFontIndex = -1;
    private int selectCodePageIndex = 0;
    private List<View> pageViews = new ArrayList<>();
    private ReaderDataHolder readerDataHolder;
    List<AlignTextView> fontSizeTexts = new ArrayList<>();
    private TextStyleCallback callback;

    private ReaderTextStyle originalStyle;
    private int originalCodePage;

    public DialogTextStyle(ReaderDataHolder readerDataHolder, TextStyleCallback callback) {
        super(readerDataHolder.getContext());
        this.readerDataHolder = readerDataHolder;
        this.callback = callback;
        this.originalStyle = ReaderTextStyle.copy(readerDataHolder.getReaderViewInfo().getReaderTextStyle());
        this.originalCodePage = readerDataHolder.getReaderUserDataInfo().getDocumentCodePage();
        setContentView(R.layout.dialog_text_style_view);
        init();
    }

    private void init() {
        defaultFont.setName(getContext().getString(R.string.default_font));
        defaultFont.setId("serif"); // magic code from alreader engine

        fontFaceLine = findViewById(R.id.font_face_bottom_view);
        fontSpacingLine = findViewById(R.id.page_spacing_bottom_view);
        codePageLine = findViewById(R.id.code_page_bottom_view);
        fontFaceLayout = (LinearLayout) findViewById(R.id.font_face_layout);
        fontSpacingLayout = (LinearLayout) findViewById(R.id.page_spacing_layout);
        codePageLayout = (LinearLayout) findViewById(R.id.code_page_layout);
        fontFaceLine.setVisibility(View.VISIBLE);
        fontSpacingLine.setVisibility(View.INVISIBLE);
        codePageLine.setVisibility(View.INVISIBLE);

        viewPager = (OnyxCustomViewPager) findViewById(R.id.view_pager);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        pageViews.add(initFontFaceView());
        pageViews.add(initPageSpacingView());
        pageViews.add(initCodePageView());

        viewPager.setAdapter(new ViewPagerAdapter());
        fontFaceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchViewPage(0);
            }
        });
        fontSpacingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchViewPage(1);
            }
        });
        codePageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchViewPage(2);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (callback != null) {
                    callback.onSaveReaderStyle(getReaderStyle());
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreAndDismiss();
            }
        });

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                updateReaderStyle(originalStyle);
            }
        });

        viewPager.setPagingEnabled(false);
    }

    private void switchViewPage(final int index) {
        viewPager.setCurrentItem(index, false);
        fontFaceLine.setVisibility(index == 0 ? View.VISIBLE : View.INVISIBLE);
        fontSpacingLine.setVisibility(index == 1 ? View.VISIBLE : View.INVISIBLE);
        codePageLine.setVisibility(index == 2 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            restoreAndDismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void restoreAndDismiss() {
        if (originalCodePage != CODE_PAGES[selectCodePageIndex].first) {
            new ChangeCodePageAction(originalCodePage).execute(readerDataHolder, null);
        }
        updateReaderStyle(originalStyle, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dismiss();
            }
        });
    }

    private View initFontFaceView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text_style_sub_font_face_view, null, false);
        CommonViewHolder fontFaceViewHolder = new CommonViewHolder(view);
        decreaseIcon = (ImageView) view.findViewById(R.id.image_view_decrease_font_size);
        increaseIcon = (ImageView) view.findViewById(R.id.image_view_increase_font_size);
        final PageRecyclerView pageView = (PageRecyclerView) view.findViewById(R.id.font_page_view);
        final TextView pageSizeIndicator = (TextView) view.findViewById(R.id.page_size_indicator);
        ImageView preIcon = (ImageView) view.findViewById(R.id.pre_icon);
        ImageView nextIcon = (ImageView) view.findViewById(R.id.next_icon);

        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageView.nextPage();
            }
        });

        preIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageView.prevPage();
            }
        });

        increaseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReaderTextStyle.SPUnit currentSize = getReaderStyle().getFontSize();
                applyFontSize(currentSize.increaseSPUnit(ReaderTextStyle.FONT_SIZE_STEP));
            }
        });

        decreaseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReaderTextStyle.SPUnit currentSize = getReaderStyle().getFontSize();
                applyFontSize(currentSize.decreaseSPUnit(ReaderTextStyle.FONT_SIZE_STEP));
            }
        });

        pageView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicator(pageView, pageSizeIndicator);
            }
        });

        final String fontFace = getReaderStyle().getFontFace();
        final GetFontsAction getFontsAction = new GetFontsAction(fontFace);
        getFontsAction.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                initFontPageView(pageView, fontFace, getFontsAction.getFonts());
                updatePageIndicator(pageView, pageSizeIndicator);
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

    private View initCodePageView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text_style_sub_code_page_view, null, false);
        final PageRecyclerView pageView = (PageRecyclerView) view.findViewById(R.id.code_page_view);
        final TextView pageSizeIndicator = (TextView) view.findViewById(R.id.page_size_indicator);
        ImageView preIcon = (ImageView) view.findViewById(R.id.pre_icon);
        ImageView nextIcon = (ImageView) view.findViewById(R.id.next_icon);

        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageView.nextPage();
            }
        });

        preIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageView.prevPage();
            }
        });

        pageView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageIndicator(pageView, pageSizeIndicator);
            }
        });

        initCodePageView(pageView);
        updatePageIndicator(pageView, pageSizeIndicator);

        return view;
    }

    private void initCodePageView(final PageRecyclerView pageView) {
        selectCodePageIndex = 0;
        for (int i = 0; i < CODE_PAGES.length; i++) {
            if (CODE_PAGES[i].first == readerDataHolder.getReaderUserDataInfo().getDocumentCodePage()) {
                selectCodePageIndex = i;
                break;
            }
        }

        pageView.setAdapter(new PageRecyclerView.PageAdapter() {
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
                return CODE_PAGES.length;
            }

            @Override
            public RecyclerView.ViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
                CommonViewHolder viewHolder = new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_text_style_radio_button_view, parent, false));
                return viewHolder;
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;
                final OnyxRadioButton button = viewHolder.getView(R.id.button);
                final Pair<Integer, Integer> codePage = CODE_PAGES[position];
                button.setText(codePage.second);
                button.setChecked(selectCodePageIndex == position);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageView.getPageAdapter().notifyItemChanged(selectCodePageIndex);
                        selectCodePageIndex = position;
                        pageView.getPageAdapter().notifyItemChanged(selectCodePageIndex);
                        new ChangeCodePageAction(CODE_PAGES[position].first).execute(readerDataHolder, null);
                    }
                });
                viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            if (pageView.getPaginator().isInNextPage(position)) {
                                btnCancel.requestFocus();
                            }
                            if (pageView.getPaginator().isInPrevPage(position)) {
                                codePageLayout.requestFocus();
                            }
                        }
                    }
                });
            }
        });
    }

    private ReaderTextStyle getReaderStyle() {
        return readerDataHolder.getReaderViewInfo().getReaderTextStyle();
    }

    private void updateReaderStyle(final ReaderTextStyle style) {
        updateReaderStyle(style, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    private void updateReaderStyle(final ReaderTextStyle style, BaseCallback callback) {
        new ChangeStyleAction(style).execute(readerDataHolder, callback);
    }

    private void initFontPageView(final PageRecyclerView pageView, String fontFace, List<FontInfo> fontsList) {
        fonts.clear();
        fonts.add(defaultFont);
        fonts.addAll(fontsList);
        if (fontFace == null) {
            selectFontIndex = 0; // choose default
        } else {
            for (int i = 0; i < fonts.size(); i++) {
                if (fontFace.equals(fonts.get(i).getId())) {
                    selectFontIndex = i;
                }
            }
        }

        pageView.setAdapter(new PageRecyclerView.PageAdapter() {
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
                CommonViewHolder viewHolder = new CommonViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_text_style_radio_button_view, parent, false));
                return viewHolder;
            }

            @Override
            public void onPageBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                CommonViewHolder viewHolder = (CommonViewHolder) holder;
                final OnyxRadioButton button = viewHolder.getView(R.id.button);
                final FontInfo fontInfo = fonts.get(position);
                button.setText(fontInfo.getName());
                button.setTypeface(fontInfo.getTypeface());
                button.setChecked(selectFontIndex == position);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageView.getPageAdapter().notifyItemChanged(selectFontIndex);
                        selectFontIndex = position;
                        pageView.getPageAdapter().notifyItemChanged(selectFontIndex);
                        getReaderStyle().setFontFace(fonts.get(position).getId());
                        updateReaderStyle(getReaderStyle());
                    }
                });
                viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            if (pageView.getPaginator().isInNextPage(position)) {
                                decreaseIcon.requestFocus();
                            }
                            if (pageView.getPaginator().isInPrevPage(position)) {
                                fontFaceLayout.requestFocus();
                            }
                        }
                    }
                });
            }
        });
    }

    private View initPageSpacingView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_text_style_sub_font_spacing_view, null, false);
        CommonViewHolder fontSpacingViewHolder = new CommonViewHolder(view);

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

    private void updatePageIndicator(PageRecyclerView pageView, TextView pageSizeIndicator) {
        if (pageView.getPaginator() != null) {
            String page = String.format("%d/%d", Math.max(1, pageView.getPaginator().getCurrentPage() + 1), Math.max(1, pageView.getPaginator().pages()));
            pageSizeIndicator.setText(page);
        }
    }

    private void updateFontSizeTextView(final List<AlignTextView> fontSizeTexts, final ReaderTextStyle readerTextStyle) {
        for (int i = 0; i < readerTextStyle.DEFAULT_FONT_SIZE_LIST.length; i++) {
            AlignTextView fontSizeText = fontSizeTexts.get(i);
            fontSizeText.setVisibility(View.VISIBLE);
            final ReaderTextStyle.SPUnit size = readerTextStyle.DEFAULT_FONT_SIZE_LIST[i];
            boolean isSelected = size.equals(readerTextStyle.getFontSize());
            fontSizeText.setTextSize(size.getValue());
            fontSizeText.setActivated(isSelected);
            fontSizeText.setTextColor(isSelected ? getContext().getResources().getColor(android.R.color.white) : getContext().getResources().getColor(android.R.color.black));
            fontSizeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyFontSize(size);
                }
            });
        }
    }

    private void applyFontSize(ReaderTextStyle.SPUnit size) {
        getReaderStyle().setFontSize(size);
        updateReaderStyle(getReaderStyle());
        updateFontSizeTextView(fontSizeTexts, getReaderStyle());
        SingletonSharedPreference.setLastFontSize(size.getValue());
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
                            if (percentage.getPercent() >= ReaderTextStyle.LARGE_LINE_SPACING.getPercent()) {
                                Toast.makeText(getContext(), R.string.already_at_maximum_line_spacing, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            percentage.setPercent(Math.min(ReaderTextStyle.LARGE_LINE_SPACING.getPercent(), percentage.getPercent() + ReaderTextStyle.LINE_SPACING_STEP.getPercent()));
                            break;
                        case DECREASE:
                            if (percentage.getPercent() <= ReaderTextStyle.SMALL_LINE_SPACING.getPercent()) {
                                Toast.makeText(getContext(), R.string.already_at_minimum_line_spacing, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            percentage.setPercent(Math.max(ReaderTextStyle.SMALL_LINE_SPACING.getPercent(), percentage.getPercent() - ReaderTextStyle.LINE_SPACING_STEP.getPercent()));
                            break;
                    }
                    readerTextStyle.setLineSpacing(percentage);
                    updateReaderStyle(readerTextStyle);
                    updateFontSpacingView(spacingViewMap, readerTextStyle);
                    SingletonSharedPreference.setLastLineSpacing(percentage.getPercent());
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
                            currentPageMargin = PageMargin.copy(ReaderTextStyle.SMALL_PAGE_MARGIN);
                            break;
                        case NORMAL:
                            currentPageMargin = PageMargin.copy(ReaderTextStyle.NORMAL_PAGE_MARGIN);
                            break;
                        case LARGE:
                            currentPageMargin = PageMargin.copy(ReaderTextStyle.LARGE_PAGE_MARGIN);
                            break;
                        case INCREASE:
                            if (currentPageMargin.getLeftMargin().getPercent() >=
                                    ReaderTextStyle.LARGE_PAGE_MARGIN.getLeftMargin().getPercent()) {
                                Toast.makeText(getContext(), R.string.already_at_maximum_page_margins, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            currentPageMargin.increasePageMargin(ReaderTextStyle.PAGE_MARGIN_STEP);
                            break;
                        case DECREASE:
                            if (currentPageMargin.getLeftMargin().getPercent() <=
                                    ReaderTextStyle.SMALL_PAGE_MARGIN.getLeftMargin().getPercent()) {
                                Toast.makeText(getContext(), R.string.already_at_minimum_page_margins, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            currentPageMargin.decreasePageMargin(ReaderTextStyle.PAGE_MARGIN_STEP);
                            break;
                    }
                    readerTextStyle.setPageMargin(currentPageMargin);
                    SingletonSharedPreference.setLastLeftMargin(currentPageMargin.getLeftMargin().getPercent());
                    SingletonSharedPreference.setLastTopMargin(currentPageMargin.getTopMargin().getPercent());
                    SingletonSharedPreference.setLastRightMargin(currentPageMargin.getRightMargin().getPercent());
                    SingletonSharedPreference.setLastBottomMargin(currentPageMargin.getBottomMargin().getPercent());
                    updateReaderStyle(readerTextStyle);
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
