package me.azulflame.trainmarch.dmhelper.repository;

public enum Market {
    BLACK_MARKET("market", 0.35),
    BAZAAR("items", 0.9),
    TRADE_MARKET("trade_market", 0.0);

    private final String value;
    private final double multiplier;

    Market(String value, double multiplier) {
        this.value = value;
        this.multiplier = multiplier;
    }

    public String getValue() {
        return value;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
