package com.onyx.android.dr.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.dr.R;


/**
 * Created by hehai on 17-7-1.
 */

public class ImageTextButton extends LinearLayout {
    private ImageView image;
    private TextView name;

    public ImageTextButton(Context context) {
        this(context, null);
    }

    public ImageTextButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View inflate = View.inflate(context, R.layout.image_text_layout, this);
        image = (ImageView) inflate.findViewById(R.id.image);
        name = (TextView) inflate.findViewById(R.id.text);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageTextButton);
        int image_drawable = typedArray.getResourceId(R.styleable.ImageTextButton_drawable, R.drawable.ic_good_sentence);
        int name_text = typedArray.getResourceId(R.styleable.ImageTextButton_name, 0);
        image.setImageResource(image_drawable);
        name.setText(name_text);
        typedArray.recycle();
    }

    public void setOnClickListener(OnClickListener listener) {
        if (listener != null) {
            image.setOnClickListener(listener);
            name.setOnClickListener(listener);
        }
    }

    public void setImageResource(int resourceID) {
        image.setImageResource(resourceID);
    }

    public void setText(String text) {
        name.setText(text);
    }

    public ImageView getImageView() {
        return image;
    }

    public TextView getTextView() {
        return name;
    }
}
