/**
 *
 */
package com.onyx.kreader.tagus;

/**
 * These types of encryption that listed in this enumeration are
 * for the documents that protected by methods other than DRM and
 * pdf passwords.
 * @author jim
 * Created by jim on 17-4-20.
 *
 */
public enum TagusEncryptionType {
    TYPE_UNKNOWN,
    ZIP_COMPRESSED;

    private static final String STR_ZIP_COMPRESSED = "zip_compressed";
    private static final String STR_TYPE_UNKNOWN = "encryption_type_unknown";

    public static TagusEncryptionType stringToValue(String string) {
        if (string.equals(STR_ZIP_COMPRESSED)) {
            return ZIP_COMPRESSED;
        } else {
            return TYPE_UNKNOWN;
        }
    }

    @Override
    public String toString() {
        switch (this) {
        case ZIP_COMPRESSED:
            return STR_ZIP_COMPRESSED;
        default:
            return STR_TYPE_UNKNOWN;
        }
    }

}
