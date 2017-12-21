package com.onyx.jdread.library.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hehai on 17-12-20.
 */

public class LibrarySelectHelper {
    private Map<String, LibrarySelectedModel> childLibrarySelectedMap = new HashMap<>();
    private LibrarySelectedModel librarySelectedModel = new LibrarySelectedModel();


    public Map<String, LibrarySelectedModel> getChildLibrarySelectedMap() {
        return childLibrarySelectedMap;
    }

    public void putLibrarySelectedModelMap(String libraryId) {
        if (childLibrarySelectedMap.containsKey(libraryId)) {
            return;
        }
        LibrarySelectedModel selectedModel = new LibrarySelectedModel();
        childLibrarySelectedMap.put(libraryId, selectedModel);
    }

    public void removeLibrarySelectedModelMap(String libraryId) {
        childLibrarySelectedMap.remove(libraryId);
    }

    public LibrarySelectedModel getLibrarySelectedModel(String libraryId) {
        LibrarySelectedModel librarySelectedModel = childLibrarySelectedMap.get(libraryId);
        if (librarySelectedModel != null) {
            return librarySelectedModel;
        }
        return this.librarySelectedModel;
    }
}
