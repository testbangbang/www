package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.R;
import com.onyx.kreader.note.actions.GetNotePageListAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/9/26.
 */
public class GetDocumentPositionChain extends BaseAction {

    private List<String> documentPositions;
    private List<Integer> pageNumbers;

    public GetDocumentPositionChain(List<Integer> pageNumbers) {
        this.pageNumbers = pageNumbers;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        if (pageNumbers == null || pageNumbers.size() <= 0) {
            return;
        }
        final ActionChain actionChain = new ActionChain();
        final List<GetDocumentPositionAction> actions = new ArrayList<>();
        documentPositions = new ArrayList<>();

        for (Integer pageNumber : pageNumbers) {
            final GetDocumentPositionAction positionAction = new GetDocumentPositionAction(pageNumber);
            actionChain.addAction(positionAction);
            actions.add(positionAction);
        }
        actionChain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                for (GetDocumentPositionAction action : actions) {
                    documentPositions.add(action.getDocumentPosition());
                }
                baseCallback.done(request, e);
            }
        });
    }

    public List<String> getDocumentPositions() {
        return documentPositions;
    }
}
