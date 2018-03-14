package com.onyx.jdread.reader.actions;

import android.content.Intent;
import android.os.Build;

import com.jingdong.app.reader.data.DrmTools;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2017/12/21.
 */

public class ParserOpenDocumentInfoAction extends BaseReaderAction {
    private Intent intent;
    private DocumentInfo documentInfo;

    public ParserOpenDocumentInfoAction(Intent intent) {
        this.intent = intent;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        String bookPath;
        if(!intent.hasExtra(DocumentInfo.BOOK_PATH)){
            final CheckPreloadBookStateAction action = new CheckPreloadBookStateAction();
            action.execute(readerDataHolder, new RxCallback() {
                @Override
                public void onNext(Object o) {
                    readerDataHolder.setPreload(true);
                    setDocumentInfo(readerDataHolder,action.getPreloadBook());
                    if(baseCallback != null){
                        baseCallback.onNext(o);
                    }
                }
            });
        }else {
            bookPath = intent.getStringExtra(DocumentInfo.BOOK_PATH);
            setDocumentInfo(readerDataHolder,bookPath);
            if(baseCallback != null){
                baseCallback.onNext(this);
            }
        }
    }

    private void setDocumentInfo(ReaderDataHolder readerDataHolder,String bookPath){
        documentInfo = new DocumentInfo();
        if(StringUtils.isNullOrEmpty(bookPath)){
            documentInfo.setMessageId(R.string.document_path_error);
            return;
        }
        if(!FileUtils.fileExist(bookPath)){
            documentInfo.setMessageId(R.string.file_does_not_exist);
            return;
        }
        documentInfo.setBookPath(bookPath);
        if(intent.hasExtra(DocumentInfo.BOOK_NAME)){
            documentInfo.setBookName(intent.getStringExtra(DocumentInfo.BOOK_NAME));
        }else{
            documentInfo.setBookName(FileUtils.getFileName(documentInfo.getBookPath()));
        }

        if(intent.hasExtra(DocumentInfo.PASSWORD)){
            documentInfo.setBookName(intent.getStringExtra(DocumentInfo.PASSWORD));
        }
        if(intent.hasExtra(DocumentInfo.WHOLE_BOOK_DOWNLOAD)){
            documentInfo.setWholeBookDownLoad(intent.getBooleanExtra(DocumentInfo.WHOLE_BOOK_DOWNLOAD,true));
        }
        if(intent.hasExtra(DocumentInfo.CLOUD_ID)){
            documentInfo.setCloudId(intent.getLongExtra(DocumentInfo.CLOUD_ID,Integer.MAX_VALUE));
        }
        if(intent.hasExtra(DocumentInfo.OPEN_TYPE)){
            documentInfo.setOpenType(intent.getIntExtra(DocumentInfo.OPEN_TYPE,DocumentInfo.OPEN_BOOK));
        }
        setSecurityInfo();
    }

    private void setSecurityInfo(){
        if(documentInfo == null){
            return;
        }
        if(intent.hasExtra(DocumentInfo.SecurityInfo.KEY)){
            documentInfo.getSecurityInfo().setKey(intent.getStringExtra(DocumentInfo.SecurityInfo.KEY));
        }
        if(intent.hasExtra(DocumentInfo.SecurityInfo.RANDOM)){
            documentInfo.getSecurityInfo().setRandom(intent.getStringExtra(DocumentInfo.SecurityInfo.RANDOM));
        }
        if(intent.hasExtra(DocumentInfo.SecurityInfo.UU_ID)){
            documentInfo.getSecurityInfo().setUuId(intent.getStringExtra(DocumentInfo.SecurityInfo.UU_ID));
        }
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }
}
