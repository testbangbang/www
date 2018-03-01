package com.onyx.jdread.reader.actions;

import android.content.Intent;
import android.os.Build;

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
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        setDocumentInfo(readerDataHolder);
        setSecurityInfo();
    }

    private String getPreloadBookPath(ReaderDataHolder readerDataHolder){
        CheckPreloadBookStateAction action = new CheckPreloadBookStateAction();
        action.execute(readerDataHolder,null);
        return action.getPreloadBook();
    }

    private void setDocumentInfo(ReaderDataHolder readerDataHolder){
        documentInfo = new DocumentInfo();
        String bookPath;
        if(!intent.hasExtra(DocumentInfo.BOOK_PATH)){
            bookPath = getPreloadBookPath(readerDataHolder);
            readerDataHolder.setPreload(true);
        }else {
            bookPath = intent.getStringExtra(DocumentInfo.BOOK_PATH);
        }
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

        documentInfo.getSecurityInfo().setUuId(Build.SERIAL);

    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }
}
