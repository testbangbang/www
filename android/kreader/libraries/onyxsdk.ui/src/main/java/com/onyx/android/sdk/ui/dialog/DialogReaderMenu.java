package com.onyx.android.sdk.ui.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.device.DeviceInfo;
import com.onyx.android.sdk.device.EpdController;
import com.onyx.android.sdk.device.EpdController.EPDMode;
import com.onyx.android.sdk.device.EpdController.UpdateMode;
import com.onyx.android.sdk.device.IDeviceFactory.TouchType;
import com.onyx.android.sdk.ui.dialog.data.IReaderMenuHandler;
import com.onyx.android.sdk.ui.dialog.data.IReaderMenuHandler.LineSpacingProperty;
import com.onyx.android.sdk.ui.dialog.data.IReaderMenuHandler.RotationScreenProperty;
import com.onyx.android.sdk.ui.util.WindowUtil;


public class DialogReaderMenu extends DialogBaseOnyx
{
    private final static String TAG = "DialogReaderMenu";

    private long mThreadId = -1;
    private Handler mHandler = new Handler();

    private TextView mTextViewChildLines = null;
    private View mTextViewLines = null;
    private RelativeLayout mLayoutMainMenu = null;
    private LinearLayout mLayoutSecondaryMenu = null;
    private LinearLayout mLayoutChild = null;
    private LayoutInflater mInflater = null;
    private View mMoreView = null;
    private View mFontSettings = null;
    private View mLineSpacingSettings = null;
    private View mTTsView = null;
    private View mRotationView = null;
    private View mZoomSettings = null;
    private View mShowDirectory = null;

    private RelativeLayout mLayoutLineSpacingSmall = null;
    private RelativeLayout mLayoutLineSpacingBig = null;
    private RelativeLayout mLayoutLineSpacingNormal = null;

    private LinearLayout mLayoutRotation_90 = null;
    private LinearLayout mLayoutRotation_180 = null;
    private LinearLayout mLayoutRotation_270 = null;
    private LinearLayout mLayoutRotation_0 = null;

    private RelativeLayout mLayoutSmartReflow = null;

    private RelativeLayout mLayoutFontIncrease = null;
    private RelativeLayout mLayoutFontDecrease = null;
    private RelativeLayout mLayoutFontEmbolden = null;
    private ImageView mImageViewFontEmbolden = null;
    private Button mButtonFontFace = null;
    private TextView mTextViewBookName = null;
    private RelativeLayout layout_spacing;

    private LinearLayout mPageInfo = null;
    private TextView mCurrentPageTextView  = null;
    private TextView mTotalPageTextView = null;
    private Activity mActivity = null;

    int mWindowFlags = 0;
    WindowManager.LayoutParams mParams = null;
    Window mWindow = null;

    private IReaderMenuHandler mMenuHandler = null;

    private boolean mIsShowChildMenu = false;
    private boolean mIsInitReaderMenu = true;

    private int mTextViewChildLineResoruce = -1;

    private GestureDetector mGestureDetector = null;
    private final int mChildLines = 26;

    private static int sRotationScreen = -1;

    private ImageButton mToggleStartStop = null;

    private EpdController.EPDMode mEpdModeBackup = EpdController.EPDMode.AUTO;

    private SeekBar mVolumeSeekBar = null;
    private AudioManager mAudioManager;
    private int mMaxVolume;

    public DialogReaderMenu(Activity activity, final IReaderMenuHandler menuHandler)
    {
        super(activity, R.style.dialog_menu);

        setContentView(R.layout.dialog_reader_menu);
        mActivity = activity;
        mThreadId = Thread.currentThread().getId();

        mLayoutMainMenu = (RelativeLayout) findViewById(R.id.layout_main_menu);
        mLayoutSecondaryMenu = (LinearLayout) findViewById(R.id.layout_secondary_menu);
        mLayoutChild = (LinearLayout) mLayoutSecondaryMenu.findViewById(R.id.layout_child);
        mTextViewChildLines = (TextView) mLayoutSecondaryMenu.findViewById(R.id.textview_child_lines);
        mTextViewLines = (View) mLayoutMainMenu.findViewById(R.id.textview_line);
        mTextViewLines.setVisibility(View.GONE);

        /*TODO old style main_menu shown book name*/
        mTextViewBookName = (TextView) findViewById(R.id.textview_book_name);
        if (mTextViewBookName != null) {
        	mTextViewBookName.setText(menuHandler.getBookName());
        }

        mInflater = LayoutInflater.from(mActivity);
        mMoreView = mInflater.inflate(R.layout.menu_more_view, null);
        mFontSettings = mInflater.inflate(R.layout.menu_font_settings, null);
        mLineSpacingSettings = mInflater.inflate(R.layout.menu_line_spacing_settings, null);
        mTTsView = mInflater.inflate(R.layout.menu_tts_view, null);
        mRotationView = mInflater.inflate(R.layout.menu_rotation_settings, null);
        mZoomSettings = mInflater.inflate(R.layout.menu_zoom_settings, null);
        mCurrentPageTextView = (TextView)findViewById(R.id.textview_current_page);
        mTotalPageTextView = (TextView)findViewById(R.id.textview_total_page);
        mShowDirectory = mInflater.inflate(R.layout.menu_directory_view, null);

        mLayoutRotation_0 = (LinearLayout) mRotationView.findViewById(R.id.linearlayout_rotation_0);
        mLayoutRotation_90 = (LinearLayout) mRotationView.findViewById(R.id.linearlayout_rotation_90);
        mLayoutRotation_180 = (LinearLayout) mRotationView.findViewById(R.id.linearlayout_rotation_180);
        mLayoutRotation_270 = (LinearLayout) mRotationView.findViewById(R.id.linearlayout_rotation_270);

        mMenuHandler = menuHandler;

        mLayoutRotation_0.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogReaderMenu.this.dismiss();

                int orientation = getOrientation(RotationScreenProperty.rotation_0);
                if (orientation != -1) {
                    mMenuHandler.changeRotationScreen(orientation);
                }
            }
        });
        mLayoutRotation_90.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogReaderMenu.this.dismiss();

                int orientation = getOrientation(RotationScreenProperty.rotation_90);
                if (orientation != -1) {
                    mMenuHandler.changeRotationScreen(orientation);
                }
            }
        });
        mLayoutRotation_180.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogReaderMenu.this.dismiss();

                int orientation = getOrientation(RotationScreenProperty.rotation_180);
                if (orientation != -1) {
                    mMenuHandler.changeRotationScreen(orientation);
                }
            }
        });
        mLayoutRotation_270.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogReaderMenu.this.dismiss();

                int orientation = getOrientation(RotationScreenProperty.rotation_270);
                if (orientation != -1) {
                    mMenuHandler.changeRotationScreen(orientation);
                }
            }
        });

        mLayoutSmartReflow = (RelativeLayout)mFontSettings.findViewById(R.id.layout_smart_reflow);
        if (mLayoutSmartReflow != null) {
            if (menuHandler.canSmartReflow()) {
                mLayoutSmartReflow.setVisibility(View.VISIBLE);
                mLayoutSmartReflow.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        menuHandler.setSmartReflow(!menuHandler.isSmartReflow());
                    }
                });
            }
        }

        mLayoutFontDecrease = (RelativeLayout) mFontSettings.findViewById(R.id.layout_font_decrease);
        mLayoutFontIncrease = (RelativeLayout) mFontSettings.findViewById(R.id.layout_font_increase);
        mLayoutFontEmbolden = (RelativeLayout) mFontSettings.findViewById(R.id.layout_font_embolden);
        mImageViewFontEmbolden = (ImageView) mFontSettings.findViewById(R.id.imageview_font_embolden_icon);
        mButtonFontFace = (Button) mFontSettings.findViewById(R.id.button_font_face);
        if (!mMenuHandler.canChangeFontFace()) {
        	mFontSettings.findViewById(R.id.relativelayout_font_face).setVisibility(View.GONE);
        	mFontSettings.findViewById(R.id.relativelayout_font_type).setVisibility(View.GONE);
        }

        updateFontEmboldenIcon();

        mLayoutFontDecrease.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.decreaseFontSize();
            }
        });
        mLayoutFontIncrease.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.increaseFontSize();
            }
        });
        mLayoutFontEmbolden.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.toggleFontEmbolden();
                updateFontEmboldenIcon();
            }
        });
        mFontSettings.findViewById(R.id.relativelayout_font_face).setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.setFontFace();
            }
        });

        mLayoutLineSpacingBig = (RelativeLayout) mLineSpacingSettings.findViewById(R.id.layout_spacing_big);
        mLayoutLineSpacingSmall = (RelativeLayout) mLineSpacingSettings.findViewById(R.id.layout_spacing_small);
        mLayoutLineSpacingNormal = (RelativeLayout) mLineSpacingSettings.findViewById(R.id.layout_spacing_normal);

        mLayoutLineSpacingNormal.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.setLineSpacing(LineSpacingProperty.normal);
            }
        });
        mLayoutLineSpacingBig.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.setLineSpacing(LineSpacingProperty.big);
            }
        });
        mLayoutLineSpacingSmall.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.setLineSpacing(LineSpacingProperty.small);
            }
        });
        RelativeLayout lineSpacingEnlarge = (RelativeLayout) mLineSpacingSettings.findViewById(R.id.layout_spacing_enlarge);
        lineSpacingEnlarge.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.setLineSpacing(LineSpacingProperty.enlarge);
            }
        });
        RelativeLayout lineSpacingDecreases = (RelativeLayout) mLineSpacingSettings.findViewById(R.id.layout_spacing_decreases);
        lineSpacingDecreases.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.setLineSpacing(LineSpacingProperty.decreases);
            }
        });

        final RelativeLayout layout_more = (RelativeLayout) mLayoutSecondaryMenu.findViewById(R.id.layout_more);
        layout_more.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                showChildMenu(layout_more, mMoreView);
            }
        });

        RelativeLayout layout_enlarge = (RelativeLayout) mZoomSettings.findViewById(R.id.layout_enlarge);
        layout_enlarge.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.zoomIn();
            }
        });

        RelativeLayout layout_narrow = (RelativeLayout) mZoomSettings.findViewById(R.id.layout_narrow);
        layout_narrow.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.zoomOut();
            }
        });

        RelativeLayout layout_fitWidth = (RelativeLayout) mZoomSettings.findViewById(R.id.layout_fit_width);
        layout_fitWidth.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.zoomToWidth();
            }
        });

        RelativeLayout layout_fitPage = (RelativeLayout) mZoomSettings.findViewById(R.id.layout_fit_page);
        layout_fitPage.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.zoomToPage();
            }
        });

        ImageButton imageButtonNavigationBar = (ImageButton) mZoomSettings.findViewById(R.id.imagebutton_navigation_bar);
        imageButtonNavigationBar.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.zoomByValue(1.0);
            }
        });

        RelativeLayout layout_twoPointsEnlarge = (RelativeLayout) mZoomSettings.findViewById(R.id.layout_two_points_enlarge);
        layout_twoPointsEnlarge.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.zoomByTwoPoints();
            }
        });

        if (DeviceInfo.currentDevice.getTouchType(activity) == TouchType.None) {
        	mZoomSettings.findViewById(R.id.layout_cutting_edge).setVisibility(View.GONE);
        } else {
			RelativeLayout layout_cuttingEdge = (RelativeLayout) mZoomSettings.findViewById(R.id.layout_cutting_edge);
			layout_cuttingEdge.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mMenuHandler.zoomBySelection();
				}
			});
        }

        Button increaseFontButton = (Button)findViewById(R.id.button_font_size_increase);
        increaseFontButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.increaseFontSize();
            }
        });

        Button decreaseFontButton = (Button)findViewById(R.id.button_font_size_decrease);
        decreaseFontButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.decreaseFontSize();
            }
        });

        ImageButton prevNavigationButton = (ImageButton)findViewById(R.id.button_previous_navigation);
        if (prevNavigationButton != null) {
        	prevNavigationButton.setOnClickListener(new View.OnClickListener()
        	{

        		@Override
        		public void onClick(View v)
        		{
        			mMenuHandler.previousNavigation();
        		}
        	});
        }

        ImageButton nextNavigationButton = (ImageButton)findViewById(R.id.button_next_navigation);
        if (nextNavigationButton != null) {
        	nextNavigationButton.setOnClickListener(new View.OnClickListener()
        	{

        		@Override
        		public void onClick(View v)
        		{
        			mMenuHandler.nextNavigation();
        		}
        	});
        }

        ImageButton prevPageButton = (ImageButton)findViewById(R.id.button_previous);
        prevPageButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.previousPage();
            }
        });
        ImageButton nextPageButton = (ImageButton)findViewById(R.id.button_next);
        nextPageButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.nextPage();
            }
        });

        prevPageButton.setNextFocusLeftId(R.id.button_next);
        nextPageButton.setNextFocusRightId(R.id.button_previous);

        Button rotationScreenButton = (Button)findViewById(R.id.button_back);
        rotationScreenButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.rotationScreen(sRotationScreen);
                sRotationScreen = -sRotationScreen;
                DialogReaderMenu.this.dismiss();
            }
        });

        final RelativeLayout layout_rotation = (RelativeLayout) mLayoutSecondaryMenu.findViewById(R.id.layout_rotation);
        layout_rotation.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                showChildMenu(layout_rotation, mRotationView);
            }
        });

        final RelativeLayout layout_directory = (RelativeLayout) mLayoutSecondaryMenu.findViewById(R.id.layout_toc);
        layout_directory.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                showChildMenu(layout_directory, mShowDirectory);
            }
        });

        RelativeLayout layout_toc = (RelativeLayout) mShowDirectory.findViewById(R.id.layout_toc);
        layout_toc.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogReaderMenu.this.dismiss();
                menuHandler.showTOC();
            }
        });

//        ImageView imageview_bookmark = (ImageView) findViewById(R.id.imageview_bookmark);
//        if (imageview_bookmark != null) {
//        	imageview_bookmark.setOnClickListener(new View.OnClickListener()
//        	{
//
//        		@Override
//        		public void onClick(View v)
//        		{
//        			DialogReaderMenu.this.dismiss();
//        			menuHandler.showBookMarks();
//        		}
//        	});
//        }

        RelativeLayout layout_bookmark = (RelativeLayout) mShowDirectory.findViewById(R.id.layout_bookmark);
        if (layout_bookmark != null) {
        	layout_bookmark.setOnClickListener(new View.OnClickListener()
        	{

        		@Override
        		public void onClick(View v)
        		{
        			DialogReaderMenu.this.dismiss();
        			menuHandler.showBookMarks();
        		}
        	});
        }

        RelativeLayout layout_annotation = (RelativeLayout) mShowDirectory.findViewById(R.id.layout_annotation);
        if(DeviceInfo.currentDevice.getTouchType(activity) == TouchType.None){
        	layout_annotation.setVisibility(View.GONE);
        } else {
        	layout_annotation.setOnClickListener(new View.OnClickListener()
        	{

        		@Override
        		public void onClick(View v)
        		{
        			DialogReaderMenu.this.dismiss();
        			menuHandler.showAnnotation();
        		}
        	});
        }

        RelativeLayout layout_dictionary = (RelativeLayout) mMoreView.findViewById(R.id.layout_dictionary);
        layout_dictionary.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogReaderMenu.this.dismiss();
                mMenuHandler.startDictionary();
            }
        });

        if (!DeviceInfo.currentDevice.hasAudio(activity)) {
            this.findViewById(R.id.layout_tts).setVisibility(View.GONE);
            this.findViewById(R.id.layout_dictionary_footer).setVisibility(View.VISIBLE);
            mMoreView.findViewById(R.id.layout_dictionary).setVisibility(View.GONE);
        }

        ImageView imageview_search = (ImageView) findViewById(R.id.imageview_search);
        if (imageview_search != null) {
        	imageview_search.setOnClickListener(new View.OnClickListener()
        	{

        		@Override
        		public void onClick(View v)
        		{
        			DialogReaderMenu.this.dismiss();
        			mMenuHandler.searchContent();
        		}
        	});
        }

        RelativeLayout layout_search = (RelativeLayout) mMoreView.findViewById(R.id.layout_search);
        if (layout_search != null) {
        	layout_search.setOnClickListener(new View.OnClickListener()
        	{

        		@Override
        		public void onClick(View v)
        		{
        			DialogReaderMenu.this.dismiss();
        			mMenuHandler.searchContent();
        		}
        	});
        }

        RelativeLayout layout_screen = (RelativeLayout) mMoreView.findViewById(R.id.layout_screen);
        ImageView imageView_screen = (ImageView) mMoreView.findViewById(R.id.imageview_screen);
        TextView textView_screen = (TextView) mMoreView.findViewById(R.id.textview_screen);
        if (mMenuHandler.isFullscreen()) {
            imageView_screen.setImageResource(R.drawable.cancel_full_screen);
            textView_screen.setText(R.string.menu_item_screen_exit_full);
        }
        else {
            imageView_screen.setImageResource(R.drawable.full_screen);
            textView_screen.setText(R.string.menu_item_screen_full);
        }
        layout_screen.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.toggleFullscreen();
            }
        });

        RelativeLayout layout_reading_mode = (RelativeLayout) mMoreView.findViewById(R.id.layout_reading_mode);
        layout_reading_mode.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogReaderMenu.this.dismiss();

                DialogReaderReadingMode dlg = new DialogReaderReadingMode(mActivity,
                        mMenuHandler.getReadingModeSupportList(),
                        mMenuHandler.getReadingMode(),
                        mMenuHandler);
                dlg.show();
            }
        });

        RelativeLayout layout_brightness = (RelativeLayout) mMoreView.findViewById(R.id.layout_brightness);
        if (!DeviceInfo.currentDevice.hasFrontLight(mActivity)) {
            layout_brightness.setVisibility(View.GONE);
        }
        layout_brightness.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DialogBrightness(mActivity).show();
            }
        });

        RelativeLayout layout_settings = (RelativeLayout) mMoreView.findViewById(R.id.layout_settings);
        layout_settings.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogReaderMenu.this.dismiss();

                mMenuHandler.showReaderSettings();
            }
        });

        RelativeLayout layout_refresh = (RelativeLayout) mMoreView.findViewById(R.id.layout_refresh);
        layout_refresh.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.setScreenRefresh();
            }
        });

        final RelativeLayout layout_tts = (RelativeLayout) mLayoutSecondaryMenu.findViewById(R.id.layout_tts);
        layout_tts.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                showChildMenu(layout_tts, mTTsView);
                menuHandler.ttsInit();
                setTtsState(menuHandler.ttsIsSpeaking());
            }
        });

        mToggleStartStop = (ImageButton) mTTsView.findViewById(R.id.imagebutton_tts_start);
        mToggleStartStop.isFocusable();
        mToggleStartStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (menuHandler.ttsIsSpeaking()) {
                    menuHandler.ttsPause();
                }
                else {
                    menuHandler.ttsSpeak();
                }
                setTtsState(menuHandler.ttsIsSpeaking());
            }
        });

        ImageButton tts_stop = (ImageButton) mTTsView.findViewById(R.id.imagebutton_tts_stop);
        tts_stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                menuHandler.ttsStop();
                setTtsState(menuHandler.ttsIsSpeaking());
            }
        });

        ImageButton volume_increase = (ImageButton) mTTsView.findViewById(R.id.imagebutton_tts_volume_increase);
        volume_increase.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                adjustVolume(true);
            }
        });

        ImageButton volume_down = (ImageButton) mTTsView.findViewById(R.id.imagebutton_tts_volume_down);
        volume_down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                adjustVolume(false);
            }
        });

        mVolumeSeekBar = (SeekBar) mTTsView.findViewById(R.id.progressbar_tts_volume);
        mVolumeSeekBar.setMax(100);
        mVolumeSeekBar.setProgress(getVolume());
        mVolumeSeekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                setVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        updateLineSpacingOrZoomSettings();

        final RelativeLayout layout_font = (RelativeLayout) mLayoutSecondaryMenu.findViewById(R.id.layout_font);
        layout_font.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mButtonFontFace.setText(mMenuHandler.getFontFace());
                showChildMenu(layout_font, mFontSettings);
            }
        });

        RelativeLayout layout_dictionary_footer = (RelativeLayout)findViewById(R.id.layout_dictionary_footer);
        layout_dictionary_footer.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.startDictionary();
            }
        });

        RelativeLayout layout_search_footer = (RelativeLayout)findViewById(R.id.layout_search_footer);
        layout_search_footer.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                DialogReaderMenu.this.dismiss();
                mMenuHandler.searchContent();
            }
        });

        RelativeLayout layout_refresh_footer = (RelativeLayout)findViewById(R.id.layout_refresh_footer);
        layout_refresh_footer.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mMenuHandler.setScreenRefresh();
            }
        });

        mPageInfo = (LinearLayout) findViewById(R.id.page_info_panel);
        mPageInfo.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                menuHandler.showGoToPageDialog();
            }
        });

        mCurrentPageTextView.setText(String.valueOf(mMenuHandler.getPageIndex()));
        mTotalPageTextView.setText(String.valueOf(mMenuHandler.getPageCount()));


        this.fitDialogToWindow();
        this.setOnDismissListener(new OnDismissListener()
        {

            @Override
            public void onDismiss(DialogInterface dialog)
            {
//                EpdController.invalidate(mActivity.getWindow().getDecorView(), UpdateMode.GC);

            }
        });
        
        if (Build.DEVICE.contentEquals("C65S_ARTATECH")) {
            layout_dictionary_footer.setVisibility(View.GONE);
            layout_settings.setVisibility(View.GONE);
            layout_search.setVisibility(View.GONE);
            layout_reading_mode.setVisibility(View.GONE);
            
            RelativeLayout layout_back = (RelativeLayout)findViewById(R.id.layout_back);
            layout_back.setVisibility(View.VISIBLE);
            layout_back.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    DialogReaderMenu.this.dismiss();
                }
            });
            layout_spacing.setVisibility(View.GONE);
        }
    }

    private void fitDialogToWindow() {
        mWindow = getWindow();
        mParams = mWindow.getAttributes();
        mParams.width = mWindow.getWindowManager().getDefaultDisplay().getWidth();
        mParams.y = mWindow.getWindowManager().getDefaultDisplay().getHeight();
        mWindow.setAttributes(mParams);
    }

	private void updateFontEmboldenIcon() {
	    if (mImageViewFontEmbolden == null) {
	        return;
	    }

		if (mMenuHandler.isFontEmboldenOn()) {
        	mImageViewFontEmbolden.setImageResource(R.drawable.font_embolden);
        } else {
        	mImageViewFontEmbolden.setImageResource(R.drawable.font_standard);
        }
	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (mIsInitReaderMenu) {
            final RelativeLayout layout_more = (RelativeLayout) mLayoutSecondaryMenu.findViewById(R.id.layout_more);
            showChildMenu(layout_more, mMoreView);
            mIsInitReaderMenu = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(this.getContext(), new SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e)
                {
                    // if single tap happens outside of dialog, then auto close dialog for convenience
                    // assume dialog at bottom of the screen
                    int out_range = -10;
                    if (e.getY() < out_range) {
                        DialogReaderMenu.this.dismiss();
                        return true;
                    }
                    else {
                        return super.onSingleTapConfirmed(e);
                    }
                }
            });
        }

        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void show()
    {
        this.fitDialogToWindow();

        mEpdModeBackup = EpdController.getMode();
        Log.d(TAG, "backup original EPD mode: " + mEpdModeBackup);
        EpdController.setMode(this.getWindow().getDecorView(), EPDMode.AUTO);

        if (WindowUtil.isFullScreen(mActivity.getWindow())) {
            mWindowFlags = mActivity.getWindow().getAttributes().flags;
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        this.setTtsState(mMenuHandler.ttsIsSpeaking());
        this.updateNavigationBar();

        super.show();
    }

    @Override
    public void dismiss()
    {
        this.onClose();
        super.dismiss();
    }

    @Override
    public void hide() {
        this.onClose();
        super.hide();
    }

    private void onClose() {
        EpdController.setMode(this.getWindow().getDecorView(), mEpdModeBackup);
        Log.d(TAG, "restore EPD mode: " + mEpdModeBackup);

        if (WindowUtil.isFullScreen(mActivity.getWindow())) {
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mActivity.getWindow().setFlags(mWindowFlags, mWindowFlags);
        }
    }

    /**
     * dirty hacking for DJVU's menu, must be called before menu dialog is shown
     */
    public void enterDJVUModeUI()
    {
        this.findViewById(R.id.layout_tts).setVisibility(View.GONE);
        this.findViewById(R.id.layout_dictionary_footer).setVisibility(View.VISIBLE);

        mMoreView.findViewById(R.id.layout_dictionary).setVisibility(View.GONE);
        mMoreView.findViewById(R.id.layout_search).setVisibility(View.GONE);

        mFontSettings.findViewById(R.id.layout_font_decrease).setVisibility(View.GONE);
        mFontSettings.findViewById(R.id.layout_font_increase).setVisibility(View.GONE);
        mFontSettings.findViewById(R.id.relativelayout_font_face).setVisibility(View.GONE);
        mFontSettings.findViewById(R.id.relativelayout_font_type).setVisibility(View.GONE);
        mShowDirectory.findViewById(R.id.layout_toc).setVisibility(View.GONE);
        mShowDirectory.findViewById(R.id.layout_annotation).setVisibility(View.GONE);
    }

    public void setButtonFontFaceText(String text)
    {
        mButtonFontFace.setText(text);
    }

    public interface onShowTTsViewLinsener
    {
        public void showTTsView();
    }

    public onShowTTsViewLinsener mOnShowTTsViewLinsener = new onShowTTsViewLinsener()
    {

        @Override
        public void showTTsView()
        {
            //do nothing
        }
    };

    public void setOnShowTTsViewLinsener(onShowTTsViewLinsener l)
    {
        mOnShowTTsViewLinsener = l;
    }

    private void showChildMenu(View parentMenuView, View childView)
    {
        LinearLayout menu_group = (LinearLayout)this.findViewById(R.id.layout_menu_group);

        int selection_count = 0;
        for (int i = 0; i < menu_group.getChildCount(); i++) {
            View v = menu_group.getChildAt(i);
            if (v.getVisibility() != View.VISIBLE) {
                continue;
            }

            if (parentMenuView == menu_group.getChildAt(i)) {
                break;
            }
            selection_count++;
        }
        if (selection_count == menu_group.getChildCount()) {
            return;
        }

        int selection_drawable = 0;
        switch (selection_count) {
        case 0:
            selection_drawable = R.drawable.item_selected_1;
            break;
        case 1:
            selection_drawable = R.drawable.item_selected_2;
            break;
        case 2:
            selection_drawable = R.drawable.item_selected_3;
            break;
        case 3:
            selection_drawable = R.drawable.item_selected_4;
            break;
        case 4:
            selection_drawable = R.drawable.item_selected_5;
            break;
        case 5:
            selection_drawable = R.drawable.item_selected_6;
            break;
        default:
            assert(false);
            return;
        }

        this.showChildMenu(selection_drawable, childView);
    }

    private void showChildMenu(int backgroundresoruce, View childView)
    {
        android.view.ViewGroup.LayoutParams params = mLayoutSecondaryMenu.getLayoutParams();

        if (mIsShowChildMenu && (backgroundresoruce != mTextViewChildLineResoruce)) {
            mTextViewChildLineResoruce = backgroundresoruce;
            mTextViewChildLines.setBackgroundResource(backgroundresoruce);

            mLayoutChild.removeAllViews();
            childView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mLayoutChild.addView(childView);
        }
        else if (!mIsShowChildMenu) {
            mTextViewChildLineResoruce = backgroundresoruce;

            params.height = mLayoutSecondaryMenu.getHeight() * 2 + mChildLines;
            mLayoutSecondaryMenu.setLayoutParams(params);
            mLayoutChild.setVisibility(View.VISIBLE);
            mTextViewChildLines.setVisibility(View.VISIBLE);
            mTextViewChildLines.setBackgroundResource(backgroundresoruce);

            mLayoutChild.removeAllViews();
            childView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mLayoutChild.addView(childView);

            mIsShowChildMenu = true;
        }
    }

    public interface onShowLineSpacingViewLinsener
    {
        public void showLineSpacingView();
    }
    public onShowLineSpacingViewLinsener mOnShowLineSpacingViewLinsener = new onShowLineSpacingViewLinsener()
    {

        @Override
        public void showLineSpacingView()
        {
            //do nothing
        }
    };
    public void setOnShowLineSpacingViewLinsener(onShowLineSpacingViewLinsener l)
    {
        mOnShowLineSpacingViewLinsener = l;
    }

    public LinearLayout getPageInfoPanel()
    {
        return mPageInfo;
    }

    public void setPageIndex(final int current) {
        if (Thread.currentThread().getId() == mThreadId) {
            mCurrentPageTextView.setText(String.valueOf(current));
            this.updateNavigationBar();
        }
        else {
            mHandler.post(new Runnable()
            {

                @Override
                public void run()
                {
                    mCurrentPageTextView.setText(String.valueOf(current));
                    DialogReaderMenu.this.updateNavigationBar();
                }
            });
        }
    }
    public void setPageCount(final int total) {
        if (Thread.currentThread().getId() == mThreadId) {
            mTotalPageTextView.setText(String.valueOf(total));
        }
        else {
            mHandler.post(new Runnable()
            {

                @Override
                public void run()
                {
                    mTotalPageTextView.setText(String.valueOf(total));
                }
            });
        }
    }

    public void setTtsState(final boolean state) {
        if (Thread.currentThread().getId() == mThreadId) {
            if(!state) {
                mToggleStartStop.setImageResource(R.drawable.tts_start);
            }
            else {
                mToggleStartStop.setImageResource(R.drawable.tts_pause);
            }
        }
        else {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if(!state) {
                        mToggleStartStop.setImageResource(R.drawable.tts_start);
                    }
                    else {
                        mToggleStartStop.setImageResource(R.drawable.tts_pause);
                    }

                }
            });
        }
    }

    private void updateLineSpacingOrZoomSettings()
    {
        layout_spacing = (RelativeLayout) mLayoutSecondaryMenu.findViewById(R.id.layout_spacing);

        if (!mMenuHandler.showZoomSettings() && !mMenuHandler.showSpacingSettings()) {
            layout_spacing.setVisibility(View.GONE);
            this.findViewById(R.id.layout_search_footer).setVisibility(View.VISIBLE);
            mMoreView.findViewById(R.id.layout_search).setVisibility(View.GONE);
        }
        else {
            ImageView imageView = (ImageView) mLayoutSecondaryMenu.findViewById(R.id.imageview_line_spacing);
            TextView textView = (TextView) mLayoutSecondaryMenu.findViewById(R.id.textview_line_spacing);

            if (mMenuHandler.showZoomSettings()) {
                imageView.setImageResource(R.drawable.zoom);
                textView.setText(R.string.menu_item_zoom);
                layout_spacing.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        showChildMenu(layout_spacing, mZoomSettings);
                    }
                });
            }
            else if (mMenuHandler.showSpacingSettings()) {
                imageView.setImageResource(R.drawable.line_spacing);
                textView.setText(R.string.menu_item_line_spacing);
                layout_spacing.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        showChildMenu(layout_spacing, mLineSpacingSettings);
                    }
                });
            }
            else {
                assert(false);
            }
        }
    }

    private void updateNavigationBar()
    {
        ImageButton prevNavigationButton = (ImageButton)findViewById(R.id.button_previous_navigation);
        if (prevNavigationButton != null && !mMenuHandler.canPreviousNavigation()) {
            prevNavigationButton.setImageResource(R.drawable.toolbar_backward_disabled);
            prevNavigationButton.setEnabled(false);
            prevNavigationButton.setVisibility(View.INVISIBLE);
            prevNavigationButton.invalidate();
        }
        else if (prevNavigationButton != null && mMenuHandler.canPreviousNavigation()){
            prevNavigationButton.setImageResource(R.drawable.toolbar_backward);
            prevNavigationButton.setEnabled(true);
            prevNavigationButton.setVisibility(View.VISIBLE);
            prevNavigationButton.invalidate();
        }

        ImageButton nextNavigationButton = (ImageButton)findViewById(R.id.button_next_navigation);
        if (nextNavigationButton != null && !mMenuHandler.canNextNavigation()) {
            nextNavigationButton.setImageResource(R.drawable.toolbar_forward_disabled);
            nextNavigationButton.setEnabled(false);
            nextNavigationButton.setVisibility(View.INVISIBLE);
            nextNavigationButton.invalidate();
        }
        else if (nextNavigationButton != null && mMenuHandler.canNextNavigation()) {
            nextNavigationButton.setImageResource(R.drawable.toolbar_forward);
            nextNavigationButton.setEnabled(true);
            nextNavigationButton.setVisibility(View.VISIBLE);
            nextNavigationButton.invalidate();
        }

        ImageButton prevPageButton = (ImageButton)findViewById(R.id.button_previous);
        if (!mMenuHandler.canPreviousPage()) {
            prevPageButton.setEnabled(false);
        }
        else {
            prevPageButton.setEnabled(true);
        }

        ImageButton nextPageButton = (ImageButton)findViewById(R.id.button_next);
        if (!mMenuHandler.canNextPage()) {
            nextPageButton.setEnabled(false);
        }
        else {
            nextPageButton.setEnabled(true);
        }
    }

    private AudioManager getAudioManager() {
        if ( mAudioManager==null ) {
            mAudioManager = (AudioManager)this.getContext().getSystemService(Context.AUDIO_SERVICE);
            if(mAudioManager != null) {
                mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            }
        }
        return mAudioManager;
    }

    private int getVolume() {
        AudioManager am = getAudioManager();
        if (am!=null) {
            return am.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / mMaxVolume;
        }
        return 0;
    }

    private void setVolume( int volume ) {
        AudioManager am = getAudioManager();
        if (am!=null) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, volume * mMaxVolume / 100, 0);
        }
    }

    private void adjustVolume(boolean opition) {
        AudioManager audioManager = (AudioManager) getContext().getSystemService(
                Context.AUDIO_SERVICE);
        if (audioManager != null) {
            if (opition) {
                audioManager.adjustSuggestedStreamVolume(AudioManager.ADJUST_RAISE,
                                AudioManager.STREAM_MUSIC,
                                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE );
            } else {
                audioManager.adjustSuggestedStreamVolume(AudioManager.ADJUST_LOWER,
                                AudioManager.STREAM_MUSIC,
                                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE );
            }
        }
        updateVolumeSeekBar();
    }

    private void updateVolumeSeekBar() {
        mVolumeSeekBar.setProgress(getVolume());
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private int getOrientation(RotationScreenProperty property)
    {
        int orientation = -1;
        int current_orientation = mActivity.getRequestedOrientation();
        if (property == RotationScreenProperty.rotation_90) {
            if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
            else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
            else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
            else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
            else {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
        }
        else if (property == RotationScreenProperty.rotation_180) {
            if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
            else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
            else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
            else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
            else {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
        }
        else if (property == RotationScreenProperty.rotation_270) {
            if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
            else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            }
            else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            }
            else if (current_orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
            else {
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            }
        }

        return orientation;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return false;
        }
        EpdController.invalidate(DialogReaderMenu.this.getWindow().getDecorView(), UpdateMode.DW);
        return super.onKeyDown(keyCode, event);
    }

}
