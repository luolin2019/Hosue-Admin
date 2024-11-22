package cn.itcast.hotel.enums;

public enum HotelQueryParams {
    keyword("keyword", false),
    brand("brand", false),
    city("city", false),
    star("star", false),
    price("price", false);

    private final String value;
    private boolean required;

    HotelQueryParams(String value, boolean required) {
        this.value = value;
        this.required = required;
    }


    public String getValue() {
        return value;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
