package con.onyx.android.libsetting.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.view.PieChartView;

/**
 * Created by solskjaer49 on 2016/11/26 10:31.
 */

public class OnyxDataBindingUtil {
    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter({"imageState"})
    public static void setImageViewState(ImageView imageView, int[] value) {
        imageView.setImageState(value, true);
    }

    @BindingAdapter({"pieChartProgress"})
    public static void setPieChartProgress(PieChartView pieChartView, float value) {
        pieChartView.setProgress(value);
    }

    @BindingAdapter({"pieChartMax"})
    public static void setPieChartMax(PieChartView pieChartView, float value) {
        pieChartView.setMax(value);
    }
}
