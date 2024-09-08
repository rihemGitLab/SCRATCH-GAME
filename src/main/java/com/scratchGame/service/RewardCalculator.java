package com.scratchGame.service;

import com.scratchGame.enums.EnumWinningCombinationType;
import com.scratchGame.enums.WinningGroup;
import com.scratchGame.enums.WinningCondition;
import com.scratchGame.exceptions.GameException;
import com.scratchGame.exceptions.InvalidArgumentException;
import com.scratchGame.models.Game;
import com.scratchGame.models.Symbol;
import com.scratchGame.models.WinningCombination;

import java.util.*;
import java.util.stream.Collectors;

public class RewardCalculator {

    private Game game;

    public RewardCalculator(Game game) {
        if (game == null) {
            throw new InvalidArgumentException("Game cannot be null");
        }
        this.game = game;
    }

    public double calculateReward(EnumWinningCombinationType winningCombinationType, List<List<String>> gameMatrix) {
        if (winningCombinationType == null || gameMatrix == null) {
            throw new InvalidArgumentException("Arguments cannot be null");
        }

        WinningCombination combination = getWinningCombinationConfig(winningCombinationType);
        double baseRewardMultiplier = combination.getRewardMultiplier();
        int countRequired = combination.getCount();
        WinningCondition condition = combination.getWhen();
        WinningGroup group = combination.getGroup();

        switch (condition) {
            case same_symbols:
                return calculateRewardForSameSymbols(gameMatrix, countRequired, baseRewardMultiplier);
            case linear_symbols:
                return calculateRewardForLinearSymbols(gameMatrix, group, baseRewardMultiplier);
            default:
                throw new GameException("Unsupported winning condition: " + condition);
        }
    }

    private WinningCombination getWinningCombinationConfig(EnumWinningCombinationType winningCombinationType) {
        return Optional.ofNullable(game.getWinCombinations().get(winningCombinationType.name()))
                .orElseThrow(() -> new GameException("Winning combination configuration not found for: " + winningCombinationType));
    }

    private double calculateRewardForSameSymbols(List<List<String>> gameMatrix, int countRequired, double baseRewardMultiplier) {
        Map<String, Long> symbolOccurrences = countSymbolOccurrences(gameMatrix);

        return symbolOccurrences.entrySet().stream()
                .filter(entry -> entry.getValue() >= countRequired)
                .mapToDouble(entry -> {
                    Symbol symbol = game.getSymbols().get(entry.getKey());
                    double symbolMultiplier = (symbol != null) ? symbol.getRewardMultiplier() : 1;
                    return baseRewardMultiplier * symbolMultiplier * entry.getValue();
                })
                .sum();
    }

    private double calculateRewardForLinearSymbols(List<List<String>> gameMatrix, WinningGroup group, double baseRewardMultiplier) {
        switch (group) {
            case horizontally_linear_symbols:
                return calculateLinearReward(gameMatrix, baseRewardMultiplier, true);
            case vertically_linear_symbols:
                return calculateLinearReward(gameMatrix, baseRewardMultiplier, false);
            case ltr_diagonally_linear_symbols:
                return calculateDiagonalReward(gameMatrix, baseRewardMultiplier, true);
            case rtl_diagonally_linear_symbols:
                return calculateDiagonalReward(gameMatrix, baseRewardMultiplier, false);
            default:
                throw new GameException("Unsupported winning group: " + group);
        }
    }

    private double calculateLinearReward(List<List<String>> gameMatrix, double baseRewardMultiplier, boolean horizontal) {
        int rows = gameMatrix.size();
        int cols = horizontal ? gameMatrix.get(0).size() : rows;

        for (int i = 0; i < rows; i++) {
            int consecutiveCount = 0;
            String currentSymbol = null;

            for (int j = 0; j < cols; j++) {
                String symbol = horizontal ? gameMatrix.get(i).get(j) : gameMatrix.get(j).get(i);

                if (symbol.equals(currentSymbol)) {
                    consecutiveCount++;
                } else {
                    currentSymbol = symbol;
                    consecutiveCount = 1;
                }

                if (consecutiveCount >= 3) { // Example count, adjust as needed
                    Symbol symbolObj = game.getSymbols().get(symbol);
                    double symbolMultiplier = (symbolObj != null) ? symbolObj.getRewardMultiplier() : 1;
                    return baseRewardMultiplier * symbolMultiplier;
                }
            }
        }
        return 0.0;
    }

    private double calculateDiagonalReward(List<List<String>> gameMatrix, double baseRewardMultiplier, boolean leftToRight) {
        int numRows = gameMatrix.size();
        int numCols = gameMatrix.get(0).size();

        for (int row = 0; row <= numRows - 3; row++) {
            for (int col = (leftToRight ? 0 : 3 - 1); leftToRight ? (col <= numCols - 3) : (col >= 3 - 1); col += (leftToRight ? 1 : -1)) {
                if (checkDiagonal(gameMatrix, row, col, 3, leftToRight)) { // Example count, adjust as needed
                    String symbol = gameMatrix.get(row).get(col);
                    Symbol symbolObj = game.getSymbols().get(symbol);
                    double symbolMultiplier = (symbolObj != null) ? symbolObj.getRewardMultiplier() : 1;
                    return baseRewardMultiplier * symbolMultiplier;
                }
            }
        }
        return 0.0;
    }

    private Map<String, Long> countSymbolOccurrences(List<List<String>> gameMatrix) {
        return gameMatrix.stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(symbol -> symbol, Collectors.counting()));
    }

    private boolean checkDiagonal(List<List<String>> gameMatrix, int startRow, int startCol, int countRequired, boolean leftToRight) {
        String symbol = gameMatrix.get(startRow).get(startCol);
        for (int i = 0; i < countRequired; i++) {
            int row = startRow + i;
            int col = leftToRight ? startCol + i : startCol - i;
            if (row >= gameMatrix.size() || col < 0 || col >= gameMatrix.get(0).size() || !gameMatrix.get(row).get(col).equals(symbol)) {
                return false;
            }
        }
        return true;
    }
}
