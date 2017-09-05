package com.onyx.android.dr.webview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.onyx.android.dr.R;

import java.util.ArrayList;
import java.util.List;

/**
 * QuickAction dialog, shows action list as icon and text like the one in Gallery3D app. Currently supports vertical
 * and horizontal layout.
 *
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *         <p>
 *         Contributors:
 *         - Kevin Peck <kevinwpeck@gmail.com>
 */
public class QuickAction extends PopupWindows implements OnDismissListener {
    private View rootView;
    private ImageView arrowUp;
    private ImageView arrowDown;
    private LayoutInflater inflater;
    private ViewGroup track;
    private ScrollView scroller;
    private OnActionItemClickListener itemClickListener;
    private OnDismissListener dismissListener;
    private List<ActionItem> actionItems = new ArrayList<ActionItem>();
    private boolean didAction;
    private int childPos;
    private int insertPos;
    private int animStyle;
    private int orientation;
    private int rootWidth = 0;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int ANIM_GROW_FROM_LEFT = 1;
    public static final int ANIM_GROW_FROM_RIGHT = 2;
    public static final int ANIM_GROW_FROM_CENTER = 3;
    public static final int ANIM_REFLECT = 4;
    public static final int ANIM_AUTO = 5;

    /**
     * Constructor for default vertical layout
     *
     * @param context Context
     */
    public QuickAction(Context context) {
        this(context, VERTICAL);
    }

    /**
     * Constructor allowing orientation override
     *
     * @param context     Context
     * @param orientation Layout orientation, can be vartical or horizontal
     */
    public QuickAction(Context context, int orientation) {
        super(context);
        this.orientation = orientation;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (orientation == HORIZONTAL) {
            setRootViewId(R.layout.popup_horizontal);
        } else {
            setRootViewId(R.layout.popup_vertical);
        }
        animStyle = ANIM_AUTO;
        childPos = 0;
    }

    /**
     * Get action item at an index
     *
     * @param index Index of item (position from callback)
     * @return Action Item at the position
     */
    public ActionItem getActionItem(int index) {
        return actionItems.get(index);
    }

    /**
     * Set root view.
     *
     * @param id Layout resource id
     */
    public void setRootViewId(int id) {
        rootView = (ViewGroup) inflater.inflate(id, null);
        track = (ViewGroup) rootView.findViewById(R.id.tracks);
        arrowDown = (ImageView) rootView.findViewById(R.id.arrow_down);
        arrowUp = (ImageView) rootView.findViewById(R.id.arrow_up);
        scroller = (ScrollView) rootView.findViewById(R.id.scroller);
        //This was previously defined on show() method, moved here to prevent force close that occured
        //when tapping fastly on a view to show quickaction dialog.
        //Thanx to zammbi (github.com/zammbi)
        rootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setContentView(rootView);
    }

    /**
     * Set animation style
     *
     * @param animStyle animation style, default is set to ANIM_AUTO
     */
    public void setAnimStyle(int animStyle) {
        this.animStyle = animStyle;
    }

    /**
     * Set listener for action item clicked.
     *
     * @param listener Listener
     */
    public void setOnActionItemClickListener(OnActionItemClickListener listener) {
        itemClickListener = listener;
    }

    /**
     * Add action item
     *
     * @param action {@link ActionItem}
     */
    public void addActionItem(ActionItem action) {
        actionItems.add(action);
        String title = action.getTitle();
        Drawable icon = action.getIcon();
        View container;
        if (orientation == HORIZONTAL) {
            container = inflater.inflate(R.layout.action_item_horizontal, null);
        } else {
            container = inflater.inflate(R.layout.action_item_vertical, null);
        }
        ImageView img = (ImageView) container.findViewById(R.id.iv_icon);
        TextView text = (TextView) container.findViewById(R.id.tv_title);
        if (icon != null) {
            img.setImageDrawable(icon);
        } else {
            img.setVisibility(View.GONE);
        }
        if (title != null) {
            text.setText(title);
        } else {
            text.setVisibility(View.GONE);
        }
        final int pos = childPos;
        final int actionId = action.getActionId();
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(QuickAction.this, pos, actionId);
                }
                if (!getActionItem(pos).isSticky()) {
                    didAction = true;
                    dismiss();
                }
            }
        });
        // Since I removed focusable from the popup window,
        // I need to control the selection drawable here
        container.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.drawable.action_item_selected);
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL
                        || event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    v.setBackgroundResource(android.R.color.transparent);
                }
                return false;
            }

        });
        container.setFocusable(true);
        container.setClickable(true);
        if (orientation == HORIZONTAL && childPos != 0) {
            View separator = inflater.inflate(R.layout.horiz_separator, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
            separator.setLayoutParams(params);
            separator.setPadding(5, 0, 5, 0);
            track.addView(separator, insertPos);
            insertPos++;
        }
        track.addView(container, insertPos);
        childPos++;
        insertPos++;
    }

    /**
     * Shows the quick action menu using the given Rect as the anchor.
     *
     * @param parent
     * @param rect
     */
    public void show(View parent, Rect rect) {
        preShow();
        int xPos, yPos, arrowPos;
        didAction = false;
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        int parentXPos = location[0];
        int parentYPos = location[1];
        Rect anchorRect = new Rect(parentXPos + rect.left, parentYPos + rect.top, parentXPos + rect.left + rect.width(), parentYPos + rect.top
                + rect.height());
        int width = anchorRect.width();
        int height = anchorRect.height();
        //rootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int rootHeight = rootView.getMeasuredHeight();
        if (rootWidth == 0) {
            rootWidth = rootView.getMeasuredWidth();
        }
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        //automatically get X coord of popup (top left)
        if ((anchorRect.left + parentXPos + rootWidth) > screenWidth) {
            xPos = anchorRect.left - (rootWidth - width);
            xPos = (xPos < 0) ? 0 : xPos;
            arrowPos = anchorRect.centerX() - xPos;
        } else {
            if (width > rootWidth) {
                xPos = anchorRect.centerX() - (rootWidth / 2);
            } else {
                xPos = anchorRect.left;
            }
            arrowPos = anchorRect.centerX() - xPos;
        }
        int dyTop = anchorRect.top;
        int dyBottom = screenHeight - anchorRect.bottom;
        boolean onTop = (dyTop > dyBottom) ? true : false;
        if (onTop) {
            if (rootHeight > dyTop) {
                yPos = 15;
                LayoutParams l = scroller.getLayoutParams();
                l.height = dyTop - height;
            } else {
                yPos = anchorRect.top - rootHeight;
            }
        } else {
            yPos = anchorRect.bottom;
            if (rootHeight > dyBottom) {
                LayoutParams l = scroller.getLayoutParams();
                l.height = dyBottom;
            }
        }
        // No arrows
        arrowUp.setVisibility(View.INVISIBLE);
        arrowDown.setVisibility(View.INVISIBLE);
        setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
        window.showAtLocation(parent, Gravity.NO_GRAVITY, xPos, yPos);
    }

    /**
     * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
     */
    public void show(View anchor) {
        preShow();
        int xPos, yPos, arrowPos;
        didAction = false;
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1]
                + anchor.getHeight());
        rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int rootHeight = rootView.getMeasuredHeight();
        if (rootWidth == 0) {
            rootWidth = rootView.getMeasuredWidth();
        }
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        //automatically get X coord of popup (top left)
        if ((anchorRect.left + rootWidth) > screenWidth) {
            xPos = anchorRect.left - (rootWidth - anchor.getWidth());
            xPos = (xPos < 0) ? 0 : xPos;
            arrowPos = anchorRect.centerX() - xPos;
        } else {
            if (anchor.getWidth() > rootWidth) {
                xPos = anchorRect.centerX() - (rootWidth / 2);
            } else {
                xPos = anchorRect.left;
            }
            arrowPos = anchorRect.centerX() - xPos;
        }
        int dyTop = anchorRect.top;
        int dyBottom = screenHeight - anchorRect.bottom;
        boolean onTop = (dyTop > dyBottom) ? true : false;
        if (onTop) {
            if (rootHeight > dyTop) {
                yPos = 15;
                LayoutParams l = scroller.getLayoutParams();
                l.height = dyTop - anchor.getHeight();
            } else {
                yPos = anchorRect.top - rootHeight;
            }
        } else {
            yPos = anchorRect.bottom;

            if (rootHeight > dyBottom) {
                LayoutParams l = scroller.getLayoutParams();
                l.height = dyBottom;
            }
        }
        showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);
        setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
        window.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    /**
     * Set animation style
     *
     * @param screenWidth screen width
     * @param requestedX  distance from left edge
     * @param onTop       flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor view
     *                    and vice versa
     */
    private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
        int arrowPos = requestedX - arrowUp.getMeasuredWidth() / 2;
        switch (animStyle) {
            case ANIM_GROW_FROM_LEFT:
                window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
                break;
            case ANIM_GROW_FROM_RIGHT:
                window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
                break;
            case ANIM_GROW_FROM_CENTER:
                window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
                break;
            case ANIM_REFLECT:
                window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect : R.style.Animations_PopDownMenu_Reflect);
                break;
            case ANIM_AUTO:
                if (arrowPos <= screenWidth / 4) {
                    window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
                } else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
                    window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
                } else {
                    window.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
                }
                break;
        }
    }

    /**
     * Show arrow
     *
     * @param whichArrow arrow type resource id
     * @param requestedX distance from left screen
     */
    private void showArrow(int whichArrow, int requestedX) {
        final View showArrow = (whichArrow == R.id.arrow_up) ? arrowUp : arrowDown;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? arrowDown : arrowUp;
        final int arrowWidth = arrowUp.getMeasuredWidth();
        showArrow.setVisibility(View.VISIBLE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow.getLayoutParams();
        param.leftMargin = requestedX - arrowWidth / 2;
        hideArrow.setVisibility(View.INVISIBLE);
    }

    /**
     * Set listener for window dismissed. This listener will only be fired if the quicakction dialog is dismissed
     * by clicking outside the dialog or clicking on sticky item.
     */
    public void setOnDismissListener(OnDismissListener listener) {
        setOnDismissListener(this);
        dismissListener = listener;
    }

    @Override
    public void onDismiss() {
        if (!didAction && dismissListener != null) {
            dismissListener.onDismiss();
        }
    }

    /**
     * Listener for item click
     */
    public interface OnActionItemClickListener {
        public abstract void onItemClick(QuickAction source, int pos, int actionId);
    }

    /**
     * Listener for window dismiss
     */
    public interface OnDismissListener {
        public abstract void onDismiss();
    }
}