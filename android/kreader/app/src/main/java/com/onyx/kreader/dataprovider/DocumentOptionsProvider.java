package com.onyx.kreader.dataprovider;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.utils.FileUtils;
import com.onyx.kreader.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.List;

/**
 * Created by zhuzeng on 5/27/16.
 * serves as proxy between request and function provider. it may forward request to real impl provider like
 * onyx android sdk or new sdk.
 */
public class DocumentOptionsProvider {

    public static BaseOptions loadDocumentOptions(final Context context, final String path, String md5) {
        BaseOptions baseOptions = new BaseOptions();
        try {
            if (StringUtils.isNullOrEmpty(md5)) {
                md5 = FileUtils.computeMD5(new File(path));
            }
            final DocumentOptions options = new Select().from(DocumentOptions.class).where(DocumentOptions_Table.md5.eq(md5)).querySingle();
            if (options == null) {
                return baseOptions;
            }
            return options.getBaseOptions();
        } catch (Exception e) {
        }
        return baseOptions;
    }

    public static void saveDocumentOptions(final Context context, final String path, String md5, final BaseOptions baseOptions) {
        DocumentOptions documentOptions;
        try {
            if (StringUtils.isNullOrEmpty(md5)) {
                md5 = FileUtils.computeMD5(new File(path));
            }

            final DocumentOptions options = new Select().from(DocumentOptions.class).where(DocumentOptions_Table.md5.eq(md5)).querySingle();
            if (options == null) {
                documentOptions = new DocumentOptions();
                documentOptions.setMd5(md5);
            } else {
                documentOptions = options;
            }
            documentOptions.setExtraAttributes(baseOptions.toJSONString());
            if (options == null) {
                documentOptions.save();
            } else {
                documentOptions.update();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
