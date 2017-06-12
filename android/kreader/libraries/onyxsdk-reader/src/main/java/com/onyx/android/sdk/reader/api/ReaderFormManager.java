package com.onyx.android.sdk.reader.api;

import java.util.List;

/**
 * Created by joy on 5/22/17.
 */

public interface ReaderFormManager {

    boolean isCustomFormEnabled();

    boolean loadFormFields(int page, List<ReaderFormField> fields);
}
