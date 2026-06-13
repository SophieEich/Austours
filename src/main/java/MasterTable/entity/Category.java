package MasterTable.entity;

public enum Category {
    ALL("ALL"),
    FIVE_STAR("*****"),
    FOUR_STAR("****"),
    THREE_STAR("***"),
    TWO_STAR("**") ,
    ONE_STAR("*");

    private final String display;

    Category(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
