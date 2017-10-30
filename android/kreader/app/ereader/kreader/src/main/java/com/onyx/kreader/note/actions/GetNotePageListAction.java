package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.host.request.GetPageNumberFromPositionListRequest;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.request.GetNotePageListRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/9/23.
 */
public class GetNotePageListAction extends BaseAction{

    List<PageInfo> scribblePages;

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        final GetNotePageListRequest noteRequest = new GetNotePageListRequest();

        noteManager.submit(readerDataHolder.getContext(), noteRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                scribblePages = noteRequest.getPageList();
                if (readerDataHolder.supportScalable() || scribblePages == null) {
                    BaseCallback.invoke(callback, request, e);
                    return;
                }

                ArrayList<String> positionList = new ArrayList<>();
                for (PageInfo page : scribblePages) {
                    positionList.add(page.getPosition());
                }
                final GetPageNumberFromPositionListRequest pageNumberRequest = new GetPageNumberFromPositionListRequest(positionList);
                readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), pageNumberRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e == null && pageNumberRequest.getPositionNumberMap() != null) {
                            for (PageInfo page : scribblePages) {
                                page.setName(String.valueOf(pageNumberRequest.getPositionNumberMap().get(page.getPosition())));
                            }
                        }
                        BaseCallback.invoke(callback, request, e);
                    }
                });
            }
        });
    }

    public List<PageInfo> getScribblePages() {
        return scribblePages;
    }

}
