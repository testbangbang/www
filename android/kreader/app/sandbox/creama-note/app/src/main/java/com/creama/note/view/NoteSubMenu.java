package com.creama.note.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.creama.note.R;
import com.creama.note.event.ChangePenWidthEvent;
import com.creama.note.event.EraseEvent;
import com.creama.note.event.ShapeChangeEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by solskjaer49 on 2017/11/17 15:14.
 */
public class NoteSubMenu extends RelativeLayout {
    /**
     * onItemSelected(int item):let operator(current is Activity) to handle different function.
     * <p>
     * onCancel():let operator know the no operate but user dismiss the menu.
     * <p>
     * onLayoutStateChanged():use in custom pos.
     */
    public static abstract class MenuCallback {
        public abstract void onItemSelect(int item);

        public abstract void onLayoutStateChanged();

        public abstract void onCancel();
    }

    private final MenuCallback mMenuCallback;
    private int mPositionID;
    private int currentCategory;
    private EventBus eventBus;


    public NoteSubMenu(Context context, RelativeLayout parentLayout, MenuCallback callback, int positionID) {
        super(context);
        eventBus = EventBus.getDefault();
        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.note_submenu, this, true);
        setBackgroundColor(Color.TRANSPARENT);
        View dismissZone = findViewById(R.id.dismiss_zone);
        dismissZone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(true);
            }
        });
        mPositionID = positionID;
        mMenuCallback = callback;
        parentLayout.addView(this, setMenuPosition());
        this.setVisibility(View.GONE);
    }

    public void show(int category) {
        currentCategory = category;
        LinearLayout functionLayout = (LinearLayout) findViewById(R.id.layout_sub_menu);
        int submenuLayoutID = 0;
        switch (category) {
            case 0:
                submenuLayoutID = R.layout.width_submenu;
                break;
            case 1:
                submenuLayoutID = R.layout.shape_submenu;
                break;
            case 2:
                submenuLayoutID = R.layout.eraser_submenu;
                break;
        }
        functionLayout.removeAllViewsInLayout();
        functionLayout.addView(LayoutInflater.from(getContext()).inflate(submenuLayoutID, null, false));
        bindButtonByCategory(category);
        setFocusable(true);
        setVisibility(View.VISIBLE);
    }

    private void buildPenWidthFunction() {
        LinearLayout widthButton1 = (LinearLayout) findViewById(R.id.button_width_1);
        LinearLayout widthButton2 = (LinearLayout) findViewById(R.id.button_width_2);
        LinearLayout widthButton3 = (LinearLayout) findViewById(R.id.button_width_3);
        LinearLayout widthButton4 = (LinearLayout) findViewById(R.id.button_width_4);
        LinearLayout widthButton5 = (LinearLayout) findViewById(R.id.button_width_5);
        widthButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new ChangePenWidthEvent(3));
                dismiss();
            }
        });
        widthButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new ChangePenWidthEvent(5));
                dismiss();
            }
        });
        widthButton3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new ChangePenWidthEvent(7));
                dismiss();
            }
        });
        widthButton4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new ChangePenWidthEvent(9));
                dismiss();
            }
        });
        widthButton5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new ChangePenWidthEvent(11));
                dismiss();
            }
        });
    }

    private void buildShapeFunction() {
        LinearLayout brushPen = (LinearLayout) findViewById(R.id.button_shape_brush);
        LinearLayout linePen = (LinearLayout) findViewById(R.id.button_shape_line);
        LinearLayout trianglePen = (LinearLayout) findViewById(R.id.button_shape_triangle);
        brushPen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new ShapeChangeEvent(ShapeFactory.SHAPE_PENCIL_SCRIBBLE));
                dismiss();
            }
        });
        linePen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new ShapeChangeEvent(ShapeFactory.SHAPE_LINE));
                dismiss();
            }
        });
        trianglePen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new ShapeChangeEvent(ShapeFactory.SHAPE_TRIANGLE));
                dismiss();
            }
        });
    }

    private void buildEraserFunction() {
        LinearLayout eraserPartialButton = (LinearLayout) findViewById(R.id.button_eraser_partial);
        LinearLayout eraserAllButton = (LinearLayout) findViewById(R.id.button_eraser_all);
        eraserPartialButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new EraseEvent(false));
                dismiss();
            }
        });
        eraserAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new EraseEvent(true));
                dismiss();
            }
        });
    }

    private void bindButtonByCategory(int category) {
        switch (category) {
            case 0:
                buildPenWidthFunction();
                break;
            case 1:
                buildShapeFunction();
                break;
            case 2:
                buildEraserFunction();
                break;
        }
    }

    public void dismiss() {
        dismiss(true);
    }

    /**
     * @param isCancel if no submenu item was previous selected -> true,otherwise false.
     */
    private void dismiss(boolean isCancel) {
        setVisibility(GONE);
        if (mMenuCallback != null && isCancel) {
            mMenuCallback.onCancel();
        }
    }

    public boolean isShow() {
        return getVisibility() == VISIBLE;
    }

    private RelativeLayout.LayoutParams setMenuPosition() {
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.BELOW, mPositionID);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        return p;
    }
}
