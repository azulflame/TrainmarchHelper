package me.azulflame.trainmarch.dmhelper.repository;

public enum Market {
    BLACK_MARKET("market"),
    BAZAAR("items"),
    TRADE_MARKET("trade_market");

    private final String value;

    Market(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
