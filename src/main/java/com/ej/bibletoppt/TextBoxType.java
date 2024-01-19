package com.ej.bibletoppt;

public enum TextBoxType {
    TITLE(10, "제목"),

    SCRIPTURE_VERSE(20, "구절"),
    CONTENT(30, "내용");

    private final int code;

    private final String description;

    TextBoxType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return this.code;
    }

    public static TextBoxType getByCode(int code) {
        for (TextBoxType type : TextBoxType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("No such TextBoxType with code: " + code);
    }
}
