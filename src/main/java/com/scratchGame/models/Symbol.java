package com.scratchGame.models;

public class Symbol {
    private String symbol;
    private double rewardMultiplier;
    private int extra;
    private String type;
    private String impact;

    public Symbol(String symbol, double rewardMultiplier, int extra, String type, String impact) {
        this.symbol = symbol;
        this.rewardMultiplier = rewardMultiplier;
        this.extra = extra;
        this.type = type;
        this.impact = impact;
    }

    public String getSymbol() { return symbol; }
    public double getRewardMultiplier() { return rewardMultiplier; }
    public int getExtra() { return extra; }
    public String getType() { return type; }
    public String getImpact() { return impact; }

    @Override
    public String toString() {
        return "SymbolConfig{" +
                "symbol='" + symbol + '\'' +
                ", rewardMultiplier=" + rewardMultiplier +
                ", extra=" + extra +
                ", type='" + type + '\'' +
                ", impact='" + impact + '\'' +
                '}';
    }
}
