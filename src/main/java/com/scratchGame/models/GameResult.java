package com.scratchGame.models;

import com.scratchGame.enums.EnumBonusImpact;
import com.scratchGame.enums.EnumWinningCombinationType;

import java.util.List;
import java.util.Map;

public class GameResult {
    private final List<List<String>> matrix;
    private final double reward;
    private final Map<String, List<EnumWinningCombinationType>> appliedWinningCombinations;
    private final List<String> appliedBonusSymbol;

    public GameResult(List<List<String>> matrix, double reward,
                      Map<String, List<EnumWinningCombinationType>> appliedWinningCombinations,
                      List<String> appliedBonusSymbol) {
        this.matrix = matrix;
        this.reward = reward;
        this.appliedWinningCombinations = appliedWinningCombinations;
        this.appliedBonusSymbol = appliedBonusSymbol;
    }

    // Getters for JSON serialization
    public List<List<String>> getMatrix() {
        return matrix;
    }

    public double getReward() {
        return reward;
    }

    public Map<String, List<EnumWinningCombinationType>> getAppliedWinningCombinations() {
        return appliedWinningCombinations;
    }

    public List<String> getAppliedBonusSymbol() {
        return appliedBonusSymbol;
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "matrix=" + matrix +
                ", reward=" + reward +
                ", appliedWinningCombinations=" + appliedWinningCombinations +
                ", appliedBonusSymbol='" + appliedBonusSymbol + '\'' +
                '}';
    }
}
