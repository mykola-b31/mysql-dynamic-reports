package ua.cn.stu.domain;

public enum LibrarianPosition {
    ГОЛОВНИЙ_БІБЛІОТЕКАР,
    СТАРШИЙ_БІБЛІОТЕКАР,
    МОЛОДШИЙ_БІБЛІОТЕКАР,
    ПОМІЧНИК_БІБЛІОТЕКАРЯ,
    ЗАВІДУВАЧ;

    public static LibrarianPosition fromString(String text) {
        if (text != null) {
            for (LibrarianPosition pos : LibrarianPosition.values()) {
                if (text.equalsIgnoreCase(pos.name().replace("_", " "))) {
                    return pos;
                }
                if (text.equalsIgnoreCase(pos.name())) {
                    return pos;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name().replace("_", " ");
    }
}