package com.onyx.android.sdk.data.utils;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2016/9/5.
 */
public class MetadataUtils {

    static public int compareStringAsc(final String a, final String b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        return a.compareTo(b);
    }

    static public int compareCreatedAtDateAsc(final Metadata a, final Metadata b) {
        if (a.getCreatedAt() == null && b.getCreatedAt() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getCreatedAt() == null) {
            return -1;
        }
        if (b.getCreatedAt() == null) {
            return 1;
        }
        int value = a.getCreatedAt().compareTo(b.getCreatedAt());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return value;
    }

    static public int compareCreatedAtDateDesc(final Metadata a, final Metadata b) {
        if (a.getCreatedAt() == null && b.getCreatedAt() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getCreatedAt() == null) {
            return 1;
        }
        if (b.getCreatedAt() == null) {
            return -1;
        }
        int value = a.getCreatedAt().compareTo(b.getCreatedAt());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return -value;
    }

    static public int compareLastModifiedDateAsc(final Metadata a, final Metadata b) {
        if (a.getLastModified() == null && b.getLastModified() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getLastModified() == null) {
            return -1;
        }
        if (b.getLastModified() == null) {
            return 1;
        }
        int value = a.getLastModified().compareTo(b.getLastModified());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return value;
    }

    static public int compareLastModifiedDateDesc(final Metadata a, final Metadata b) {
        if (a.getLastModified() == null && b.getLastModified() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getLastModified() == null) {
            return 1;
        }
        if (b.getLastModified() == null) {
            return -1;
        }
        int value = a.getLastModified().compareTo(b.getLastModified());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return -value;
    }

    static public int compareLastAccessedDateAsc(final Metadata a, final Metadata b) {
        if (a.getLastAccess() == null && b.getLastAccess() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getLastAccess() == null) {
            return -1;
        }
        if (b.getLastAccess() == null) {
            return 1;
        }
        int value = a.getLastAccess().compareTo(b.getLastAccess());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return value;
    }

    static public int compareLastAccessedDateDesc(final Metadata a, final Metadata b) {
        if (a.getLastAccess() == null && b.getLastAccess() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getLastAccess() == null) {
            return 1;
        }
        if (b.getLastAccess() == null) {
            return -1;
        }
        int value = a.getLastAccess().compareTo(b.getLastAccess());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return -value;
    }


    static public int compareSizeAsc(final Metadata a, final Metadata b) {
        if (a.getSize() == b.getSize()) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return (int) a.getSize() - (int) b.getSize();
    }

    static public int compareSizeDesc(final Metadata a, final Metadata b) {
        if (a.getSize() == b.getSize()) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return (int) b.getSize() - (int) a.getSize();
    }

    static public int compareAuthorStringAsc(final Metadata a, final Metadata b) {
        if (a.getAuthors() == null && b.getAuthors() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getAuthors() == null) {
            return -1;
        }
        if (b.getAuthors() == null) {
            return 1;
        }
        int value = a.getAuthors().compareTo(b.getAuthors());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return value;
    }

    static public int compareAuthorStringDesc(final Metadata a, final Metadata b) {
        if (a.getAuthors() == null && b.getAuthors() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getAuthors() == null) {
            return 1;
        }
        if (b.getAuthors() == null) {
            return -1;
        }
        int value = a.getAuthors().compareTo(b.getAuthors());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return -value;
    }

    static public int compareTitleStringAsc(final Metadata a, final Metadata b) {
        if (a.getTitle() == null && b.getTitle() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getTitle() == null) {
            return -1;
        }
        if (b.getTitle() == null) {
            return 1;
        }
        int value = a.getTitle().compareTo(b.getTitle());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return value;
    }

    static public int compareTitleStringDesc(final Metadata a, final Metadata b) {
        if (a.getTitle() == null && b.getTitle() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getTitle() == null) {
            return 1;
        }
        if (b.getTitle() == null) {
            return -1;
        }
        int value = a.getTitle().compareTo(b.getTitle());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return -value;
    }

    static public int compareTypeAsc(final Metadata a, final Metadata b) {
        if (a.getType() == null && b.getType() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getType() == null) {
            return -1;
        }
        if (b.getType() == null) {
            return 1;
        }
        int value = a.getType().compareTo(b.getType());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return value;
    }

    static public int compareTypeDesc(final Metadata a, final Metadata b) {
        if (a.getType() == null && b.getType() == null) {
            return compareStringAsc(a.getName(), b.getName());
        }
        if (a.getType() == null) {
            return 1;
        }
        if (b.getType() == null) {
            return -1;
        }
        int value = a.getType().compareTo(b.getType());
        if (value == 0) {
            return compareStringAsc(a.getName(), b.getName());
        }
        return -value;
    }

    static public void sortByCreatedAtDateAsc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareCreatedAtDateAsc(metadata, metadata2);
            }
        });
    }

    static public void sortByCreatedAtDateDesc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareCreatedAtDateDesc(metadata, metadata2);
            }
        });
    }

    static public void sortByLastModifiedAsc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareLastModifiedDateAsc(metadata, metadata2);
            }
        });
    }

    static public void sortByLastModifiedDesc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareLastModifiedDateDesc(metadata, metadata2);
            }
        });
    }

    static public void sortByNameAsc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareStringAsc(metadata.getName(), metadata2.getName());
            }
        });
    }

    static public void sortByNameDesc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return -compareStringAsc(metadata.getName(), metadata2.getName());
            }
        });
    }

    static public void sortBySizeAsc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareSizeAsc(metadata, metadata2);
            }
        });
    }

    static public void sortBySizeDesc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareSizeDesc(metadata, metadata2);
            }
        });
    }


    static public void sortByTitleAsc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareTitleStringAsc(metadata, metadata2);
            }
        });
    }

    static public void sortByTitleDesc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareTitleStringDesc(metadata, metadata2);
            }
        });
    }

    static public void sortByFileTypeAsc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareTypeAsc(metadata, metadata2);
            }
        });
    }

    static public void sortByFileTypeDesc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareTypeDesc(metadata, metadata2);
            }
        });
    }

    static public void sortByAuthorAsc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareAuthorStringAsc(metadata, metadata2);
            }
        });
    }

    static public void sortByAuthorDesc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareAuthorStringDesc(metadata, metadata2);
            }
        });
    }

    static public void sortByPublisherAsc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareStringAsc(metadata.getPublisher(), metadata2.getPublisher());
            }
        });
    }

    static public void sortByPublisherDesc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return -compareStringAsc(metadata.getPublisher(), metadata2.getPublisher());
            }
        });
    }

    static public void sortByRecentlyReadAsc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareLastAccessedDateAsc(metadata, metadata2);
            }
        });
    }

    static public void sortByRecentlyReadDesc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareLastAccessedDateDesc(metadata, metadata2);
            }
        });
    }

    static public void sortByLastOpenTimeAsc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareLastAccessedDateAsc(metadata, metadata2);
            }
        });
    }

    static public void sortByLastOpenTimeDesc(final List<Metadata> list) {
        Collections.sort(list, new Comparator<Metadata>() {
            @Override
            public int compare(Metadata metadata, Metadata metadata2) {
                return compareLastAccessedDateDesc(metadata, metadata2);
            }
        });
    }

    // sort by: None, Name, FileType, Size, CreationTime, BookTitle, Author, Publisher, RecentlyRead, Total, StartTime, LastOpenTime, InstallTime
    static public void sort(final List<Metadata> list, SortBy sortBy, SortOrder order) {
        switch (sortBy) {
            case None:
                if (order == SortOrder.Asc) {
                    sortByNameAsc(list);
                } else {
                    sortByNameDesc(list);
                }
                break;
            case Name:
                if (order == SortOrder.Asc) {
                    sortByNameAsc(list);
                } else {
                    sortByNameDesc(list);
                }
                break;
            case FileType:
                if (order == SortOrder.Asc) {
                    sortByFileTypeAsc(list);
                } else {
                    sortByFileTypeDesc(list);
                }
                break;
            case Size:
                if (order == SortOrder.Asc) {
                    sortBySizeAsc(list);
                } else {
                    sortBySizeDesc(list);
                }
                break;
            case CreationTime:
                if (order == SortOrder.Asc) {
                    sortByCreatedAtDateAsc(list);
                } else {
                    sortByCreatedAtDateDesc(list);
                }
                break;
            case BookTitle:
                if (order == SortOrder.Asc) {
                    sortByTitleAsc(list);
                } else {
                    sortByTitleDesc(list);
                }
                break;
            case Author:
                if (order == SortOrder.Asc) {
                    sortByAuthorAsc(list);
                } else {
                    sortByAuthorDesc(list);
                }
                break;
            case Publisher:
                if (order == SortOrder.Asc) {
                    sortByPublisherAsc(list);
                } else {
                    sortByPublisherDesc(list);
                }
                break;
            case RecentlyRead:
                if (order == SortOrder.Asc) {
                    sortByRecentlyReadAsc(list);
                } else {
                    sortByRecentlyReadDesc(list);
                }
                break;
            case Total:
                if (order == SortOrder.Asc) {
                    sortByLastOpenTimeAsc(list);
                } else {
                    sortByLastOpenTimeDesc(list);
                }
                break;
            case StartTime:
                if (order == SortOrder.Asc) {
                    sortByLastOpenTimeAsc(list);
                } else {
                    sortByLastOpenTimeDesc(list);
                }
                break;
            case LastOpenTime:
                if (order == SortOrder.Asc) {
                    sortByLastOpenTimeAsc(list);
                } else {
                    sortByLastOpenTimeDesc(list);
                }
                break;
            case InstallTime:
                if (order == SortOrder.Asc) {
                    sortByLastOpenTimeAsc(list);
                } else {
                    sortByLastOpenTimeDesc(list);
                }
                break;
            default:
                break;
        }
    }

    static public boolean safelyContains(final Set<String> set, final String string) {
        if (set != null && set.size() > 0 && !set.contains(string)) {
            return false;
        }
        return true;
    }

    static public boolean safelyContains(final Set<String> set, final List<String> list) {
        if (set != null && list != null) {
            for (String string : list) {
                if (set.contains(string)) {
                    return true;
                }
            }
        }
        return false;
    }

    static public boolean safelyContains(final String parent, final String query) {
        return !(StringUtils.isNullOrEmpty(parent) || StringUtils.isNullOrEmpty(query)) && parent.contains(query);
    }

    static public boolean safelyContains(final Metadata metadata, final String query) {
        if (safelyContains(metadata.getName(), query)) {
            return true;
        }
        if (safelyContains(metadata.getAuthors(), query)) {
            return true;
        }
        if (safelyContains(metadata.getTitle(), query)) {
            return true;
        }
        return false;
    }

    static public boolean containsIfNotNull(final Set<String> source, final Collection<String> target) {
        if (source == null || source.size() == 0) {
            return true;
        }
        if (target == null) {
            return false;
        }

        for (String string : target) {
            if (source.contains(string)) {
                return true;
            }
        }
        return false;
    }

    static public Set<String> getStringSplitSet(String originString, String delimiter) {
        Set<String> set = new HashSet<>();
        if (originString == null) {
            return set;
        }
        String[] authors = originString.split(delimiter);
        for (String author : authors) {
            set.add(author);
        }
        return set;
    }

    static public boolean criteriaContains(final Metadata metadata, final QueryArgs criteria) {
        if (!CollectionUtils.contains(criteria.fileType, metadata.getType().toLowerCase())) {
            return false;
        }
        if (!containsIfNotNull(criteria.author, getStringSplitSet(metadata.getAuthors(), Metadata.DELIMITER))) {
            return false;
        }
        if (!containsIfNotNull(criteria.tags, getStringSplitSet(metadata.getTags(), Metadata.DELIMITER))) {
            return false;
        }
        if (!CollectionUtils.contains(criteria.title, metadata.getTitle())) {
            return false;
        }
        if (!containsIfNotNull(criteria.series, getStringSplitSet(metadata.getSeries(), Metadata.DELIMITER))) {
            return false;
        }
        return true;
    }

    static public List<Metadata> verifyReadedStatus(List<Metadata> list, BookFilter filter) {
        if (filter != BookFilter.FINISHED) {
            return list;
        }
        List<Metadata> readList = new ArrayList<>();
        for (Metadata metadata : list) {
            if (metadata.isFinished()) {
                readList.add(metadata);
            }
        }
        return readList;
    }

    public static Metadata ensureObject(final Metadata metadata) {
        return metadata != null ? metadata : new Metadata();
    }
}
