package com.scratchGame.models;

import java.util.List;
import java.util.Map;

public class Probability {
    private List<Map<String, Integer>> standardSymbolsProbabilities;
    private Map<String, Integer> bonusSymbolsProbabilities;

    public Probability(List<Map<String, Integer>> standardSymbolsProbabilities, Map<String, Integer> bonusSymbolsProbabilities) {
        this.standardSymbolsProbabilities = standardSymbolsProbabilities;
        this.bonusSymbolsProbabilities = bonusSymbolsProbabilities;
    }

    public List<Map<String, Integer>> getStandardSymbolsProbabilities() {
        return standardSymbolsProbabilities;
    }

    public void setStandardSymbolsProbabilities(List<Map<String, Integer>> standardSymbolsProbabilities) {
        this.standardSymbolsProbabilities = standardSymbolsProbabilities;
    }

    public Map<String, Integer> getBonusSymbolsProbabilities() {
        return bonusSymbolsProbabilities;
    }

    public void setBonusSymbolsProbabilities(Map<String, Integer> bonusSymbolsProbabilities) {
        this.bonusSymbolsProbabilities = bonusSymbolsProbabilities;
    }

    @Override
    public String toString() {
        return "ProbabilityConfig{" +
                "standardSymbolsProbabilities=" + standardSymbolsProbabilities +
                ", bonusSymbolsProbabilities=" + bonusSymbolsProbabilities +
                '}';
    }
}
