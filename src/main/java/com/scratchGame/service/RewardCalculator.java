package com.scratchGame.service;

import com.scratchGame.enums.EnumWinningCombinationType;
import com.scratchGame.exceptions.GameException;
import com.scratchGame.exceptions.InvalidArgumentException;
import com.scratchGame.models.Game;
import com.scratchGame.models.Symbol;
import com.scratchGame.models.WinningCombination;

import java.util.*;
import java.util.stream.Collectors;

public class RewardCalculator {

    private  Game game;

    public RewardCalculator(Game game) {
        if (game == null) {
            throw new InvalidArgumentException("Game cannot be null");
        }
        this.game = game;
    }

    public RewardCalculator() {

    }

    public double calculateReward(EnumWinningCombinationType winningCombination, List<List<String>> gameMatrix) {
        if (winningCombination == null || gameMatrix == null) {
            throw new InvalidArgumentException("Arguments cannot be null");
        }

        WinningCombination combination = getWinningCombinationConfig(winningCombination);
        double baseRewardMultiplier = combination.getRewardMultiplier();
        int countRequired = combination.getCount();

        switch (winningCombination) {
            case same_symbol_3_times, same_symbol_4_times, same_symbol_5_times, same_symbol_6_times, same_symbol_7_times, same_symbol_8_times, same_symbol_9_times:
                return calculateRewardForCount(gameMatrix, countRequired, baseRewardMultiplier);
            case same_symbols_horizontally:
                return checkLines(gameMatrix, countRequired, baseRewardMultiplier, true);
            case same_symbols_vertically:
                return checkLines(gameMatrix, countRequired, baseRewardMultiplier, false);
            case same_symbols_diagonally_left_to_right:
                return checkDiagonals(gameMatrix, countRequired, baseRewardMultiplier, true);
            case same_symbols_diagonally_right_to_left:
                return checkDiagonals(gameMatrix, countRequired, baseRewardMultiplier, false);
            default:
                throw new GameException("Unsupported winning combination: " + winningCombination);
        }
    }

    private WinningCombination getWinningCombinationConfig(EnumWinningCombinationType winningCombination) {
        return Optional.ofNullable(game.getWinCombinations().get(winningCombination.name()))
                .orElseThrow(() -> new GameException("Winning combination configuration not found for: " + winningCombination));
    }

    private double calculateRewardForCount(List<List<String>> gameMatrix, int countRequired, double baseRewardMultiplier) {
        Map<String, Long> symbolOccurrences = countSymbolOccurrences(gameMatrix);

        return symbolOccurrences.entrySet().stream()
                .filter(entry -> entry.getValue() >= countRequired)
                .mapToDouble(entry -> {
                    Symbol symbol = game.getSymbols().get(entry.getKey());
                    double symbolMultiplier = (symbol != null) ? symbol.getRewardMultiplier() : 1;
                    // Total reward multiplier includes both the base and symbol multipliers
                    return baseRewardMultiplier * symbolMultiplier * entry.getValue();
                })
                .sum();
    }

    private Map<String, Long> countSymbolOccurrences(List<List<String>> gameMatrix) {
        return gameMatrix.stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(symbol -> symbol, Collectors.counting()));
    }

    private double checkLines(List<List<String>> gameMatrix, int countRequired, double baseRewardMultiplier, boolean horizontal) {
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

                if (consecutiveCount >= countRequired) {
                    Symbol symbolObj = game.getSymbols().get(symbol);
                    double symbolMultiplier = (symbolObj != null) ? symbolObj.getRewardMultiplier() : 1;
                    // Total reward multiplier includes both the base and symbol multipliers
                    return baseRewardMultiplier * symbolMultiplier;
                }
            }
        }
        return 0.0;
    }

    private double checkDiagonals(List<List<String>> gameMatrix, int countRequired, double baseRewardMultiplier, boolean leftToRight) {
        int numRows = gameMatrix.size();
        int numCols = gameMatrix.get(0).size();

        for (int row = 0; row <= numRows - countRequired; row++) {
            for (int col = (leftToRight ? 0 : countRequired - 1); leftToRight ? (col <= numCols - countRequired) : (col >= countRequired - 1); col += (leftToRight ? 1 : -1)) {
                if (checkDiagonal(gameMatrix, row, col, countRequired, leftToRight)) {
                    String symbol = gameMatrix.get(row).get(col);
                    Symbol symbolObj = game.getSymbols().get(symbol);
                    double symbolMultiplier = (symbolObj != null) ? symbolObj.getRewardMultiplier() : 1;
                    // Total reward multiplier includes both the base and symbol multipliers
                    return baseRewardMultiplier * symbolMultiplier;
                }
            }
        }
        return 0.0;
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
