package com.onyx.jdread.reader.actions;

import android.content.Intent;

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
    private static final String BOOK_PATH = "/sdcard/Books/飘.txt";
    public ParserOpenDocumentInfoAction(Intent intent) {
        this.intent = intent;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        setDocumentInfo(readerDataHolder);
        setSecurityInfo();
    }

    private void setDocumentInfo(ReaderDataHolder readerDataHolder){
        documentInfo = new DocumentInfo();
        String bookPath;
        if(!intent.hasExtra(DocumentInfo.BOOK_PATH)){
            bookPath = BOOK_PATH;
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
        }
        if(intent.hasExtra(DocumentInfo.PASSWORD)){
            documentInfo.setBookName(intent.getStringExtra(DocumentInfo.PASSWORD));
        }else{
            documentInfo.setBookName(FileUtils.getFileName(documentInfo.getBookPath()));
        }
    }

    private void setSecurityInfo(){
        if(documentInfo == null){
            return;
        }
        if(intent.hasExtra(DocumentInfo.SecurityInfo.KEY)){
            documentInfo.setBookName(intent.getStringExtra(DocumentInfo.SecurityInfo.KEY));
        }
        if(intent.hasExtra(DocumentInfo.SecurityInfo.RANDOM)){
            documentInfo.setBookName(intent.getStringExtra(DocumentInfo.SecurityInfo.RANDOM));
        }
        if(intent.hasExtra(DocumentInfo.SecurityInfo.UU_ID)){
            documentInfo.setBookName(intent.getStringExtra(DocumentInfo.SecurityInfo.UU_ID));
        }
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }
}
