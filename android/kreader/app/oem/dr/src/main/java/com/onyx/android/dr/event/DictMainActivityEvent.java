package com.onyx.android.dr.event;

import java.util.List;

/**
 * Created by huxiaomao on 2016/10/25.
 */

public class DictMainActivityEvent {
//    public static class WebviewPageChangedEvent extends BaseEvent {
//    }

    public static class ClearHistoryEvent extends BaseEvent {
    }

    public static class SelectHeadwordEvent extends BaseEvent {
    }

//    public static class UpdateVoiceStatusEvent extends BaseEvent {
//    }

    public static class LoadDictionaryExceptionEvent extends BaseEvent {

    }


//    public static class PlaySoundEvent extends BaseEvent {
//
//    }

//    public static class ReloadDictImageEvent extends BaseEvent {
//
//    }

//    public static class RefreshWebviewEvent extends BaseEvent {
//    }

    public static class JumpQueryEvent extends BaseEvent {
        private String headword;

        public JumpQueryEvent(String headword) {
            this.headword = headword;
        }

        public String getHeadword() {
            return headword;
        }

        public void setHeadword(String headword) {
            this.headword = headword;
        }
    }

    public static class ShowQueryListResultEvent {
        List<String> result;

        public ShowQueryListResultEvent(List<String> result) {
            this.result = result;
        }

        public List<String> getResult() {
            return result;
        }

        public void setResult(List<String> result) {
            this.result = result;
        }
    }
}
