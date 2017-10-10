package com.onyx.android.sun.cloud.cache;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created on 2017/04/21.
 *
 * @author hehai
 */

public interface EnhancedCallback<T> {
    void onResponse(Call<T> call, Response<T> response);

    void onFailure(Call<T> call, Throwable t);

    void onGetCache(T t);
}
