package com.scratchGame.service;

import com.scratchGame.enums.EnumBonusImpact;
import com.scratchGame.enums.EnumWinningCombinationType;
import com.scratchGame.exceptions.GameException;
import com.scratchGame.exceptions.InvalidArgumentException;
import com.scratchGame.models.Game;
import com.scratchGame.models.GameResult;

import java.util.*;
import java.util.stream.Collectors;

public class GameService {
    private final Game gameConfig;
    private final MatrixGenerator matrixGenerator;
    private final RewardCalculator rewardCalculator;

    // Hardcoded normal symbols
    private static final Set<String> NORMAL_SYMBOLS = Set.of("A", "B", "C", "D", "E"); // Add your normal symbols here

    public GameService(Game gameConfig, MatrixGenerator matrixGenerator, RewardCalculator rewardCalculator) {
        if (gameConfig == null || matrixGenerator == null || rewardCalculator == null) {
            throw new InvalidArgumentException("Arguments cannot be null");
        }
        this.gameConfig = gameConfig;
        this.matrixGenerator = matrixGenerator;
        this.rewardCalculator = rewardCalculator;
    }

    public GameResult startGame(double bettingAmount) {
        // Generate matrix
        List<List<String>> matrix = matrixGenerator.generateMatrix();

        // Check for wins
        Map<String, List<EnumWinningCombinationType>> winCombinations = checkForWins(matrix);

        // Calculate base reward
        double baseReward = calculateBaseRewards(winCombinations, matrix);

        // Check and apply bonus symbols
        double finalReward = applyBonusSymbols(baseReward, matrix);

        // Collect applied bonus symbol impact
        List<String> appliedBonusSymbols = collectAppliedBonus(matrix);

        return new GameResult(matrix, finalReward * bettingAmount, winCombinations, appliedBonusSymbols);
    }



    private Map<String, List<EnumWinningCombinationType>> checkForWins(List<List<String>> matrix) {
        Map<String, List<EnumWinningCombinationType>> winCombinations = new HashMap<>();

        // Check for each type of winning combination
        for (EnumWinningCombinationType type : EnumWinningCombinationType.values()) {
            switch (type) {
                case same_symbol_3_times:
                case same_symbol_4_times:
                case same_symbol_5_times:
                case same_symbol_6_times:
                case same_symbol_7_times:
                case same_symbol_8_times:
                case same_symbol_9_times:
                    addSymbolOccurrencesForCombination(matrix, type, winCombinations);
                    break;
                case same_symbols_horizontally:
                    addHorizontalSymbols(matrix, winCombinations);
                    break;
                case same_symbols_vertically:
                    addVerticalSymbols(matrix, winCombinations);
                    break;
                case same_symbols_diagonally_left_to_right:
                    addDiagonalSymbols(matrix, true, winCombinations);
                    break;
                case same_symbols_diagonally_right_to_left:
                    addDiagonalSymbols(matrix, false, winCombinations);
                    break;
                default:
                    throw new GameException("Unsupported winning combination type: " + type);
            }
        }

        return winCombinations;
    }

    private void addSymbolOccurrencesForCombination(List<List<String>> matrix, EnumWinningCombinationType type, Map<String, List<EnumWinningCombinationType>> winCombinations) {
        int countRequired = Integer.parseInt(type.name().split("_")[2]); // Extract the count from the type
        matrix.stream()
                .flatMap(List::stream)
                .filter(NORMAL_SYMBOLS::contains) // Filter normal symbols
                .collect(Collectors.groupingBy(symbol -> symbol, Collectors.counting())) // Group symbols and count their occurrences
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= countRequired) // Filter symbols that meet or exceed the required count
                .forEach(entry -> winCombinations.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(type));
    }

    private void addHorizontalSymbols(List<List<String>> matrix, Map<String, List<EnumWinningCombinationType>> winCombinations) {
        for (List<String> row : matrix) {
            addConsecutiveSymbolsToMap(row, EnumWinningCombinationType.same_symbols_horizontally, winCombinations);
        }
    }

    private void addVerticalSymbols(List<List<String>> matrix, Map<String, List<EnumWinningCombinationType>> winCombinations) {
        int matrixSize = matrix.size();

        for (int col = 0; col < matrixSize; col++) {
            List<String> column = new ArrayList<>();
            for (List<String> row : matrix) {
                column.add(row.get(col));
            }
            addConsecutiveSymbolsToMap(column, EnumWinningCombinationType.same_symbols_vertically, winCombinations);
        }
    }

    private void addDiagonalSymbols(List<List<String>> matrix, boolean leftToRight, Map<String, List<EnumWinningCombinationType>> winCombinations) {
        int matrixSize = matrix.size();
        int countRequired = 3; // Default value; adjust as needed for specific requirements

        for (int row = 0; row <= matrixSize - countRequired; row++) {
            for (int col = 0; col <= matrixSize - countRequired; col++) {
                if (leftToRight) {
                    String symbol = getDiagonalSymbol(matrix, row, col, countRequired, true);
                    if (symbol != null && NORMAL_SYMBOLS.contains(symbol)) {
                        winCombinations.computeIfAbsent(symbol, k -> new ArrayList<>()).add(EnumWinningCombinationType.same_symbols_diagonally_left_to_right);
                    }
                } else {
                    String symbol = getDiagonalSymbol(matrix, row, col, countRequired, false);
                    if (symbol != null && NORMAL_SYMBOLS.contains(symbol)) {
                        winCombinations.computeIfAbsent(symbol, k -> new ArrayList<>()).add(EnumWinningCombinationType.same_symbols_diagonally_right_to_left);
                    }
                }
            }
        }
    }

    private String getDiagonalSymbol(List<List<String>> matrix, int startRow, int startCol, int countRequired, boolean leftToRight) {
        String symbol = matrix.get(startRow).get(startCol);
        for (int i = 0; i < countRequired; i++) {
            int row = startRow + i;
            int col = leftToRight ? startCol + i : startCol - i;
            if (row >= matrix.size() || col < 0 || col >= matrix.get(0).size() || !matrix.get(row).get(col).equals(symbol)) {
                return null;
            }
        }
        return symbol;
    }

    private void addConsecutiveSymbolsToMap(List<String> symbols, EnumWinningCombinationType winType, Map<String, List<EnumWinningCombinationType>> matchesMap) {
        int countRequired = 3; // Default value; adjust as needed for specific requirements

        int consecutiveCount = 0;
        String previousSymbol = null;

        for (String symbol : symbols) {
            if (NORMAL_SYMBOLS.contains(symbol)) {
                if (symbol.equals(previousSymbol)) {
                    consecutiveCount++;
                } else {
                    previousSymbol = symbol;
                    consecutiveCount = 1;
                }

                if (consecutiveCount >= countRequired) {
                    matchesMap.computeIfAbsent(symbol, k -> new ArrayList<>()).add(winType);
                }
            }
        }
    }

    double calculateBaseRewards(Map<String, List<EnumWinningCombinationType>> winCombinations, List<List<String>> matrix) {
        return winCombinations.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(combination -> Map.entry(entry.getKey(), combination)))
                .mapToDouble(entry -> {
                    try {
                        return rewardCalculator.calculateReward(entry.getValue(), matrix);
                    } catch (InvalidArgumentException e) {
                        System.err.println("Error calculating reward for combination: " + entry.getValue() + " - " + e.getMessage());
                        return 0.0;
                    }
                })
                .sum();
    }

    double applyBonusSymbols(double baseReward, List<List<String>> matrix) {
        if (baseReward == 0) {
            // If no base reward, bonuses do not apply
            return 0;
        }

        // Collect all potential bonus impacts
        List<EnumBonusImpact> bonusImpacts = collectAppliedBonusImpacts(matrix);

        // Apply each impact sequentially
        double finalReward = baseReward;

        // Apply multipliers first
        for (EnumBonusImpact impact : bonusImpacts) {
            if (impact == EnumBonusImpact.MULTIPLY_REWARD) {
                finalReward *= getMultiplierFromBonus(matrix);
            }
        }

        // Apply extra bonuses
        for (EnumBonusImpact impact : bonusImpacts) {
            if (impact == EnumBonusImpact.EXTRA_BONUS) {
                finalReward += getExtraBonusFromBonus(matrix);
            }
        }

        return finalReward;
    }

    private List<EnumBonusImpact> collectAppliedBonusImpacts(List<List<String>> matrix) {
        return matrix.stream()
                .flatMap(List::stream)
                .filter(symbol -> symbol.matches("10x|5x|\\+1000|\\+500|MISS"))
                .map(this::determineBonusImpact)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private List<String> collectAppliedBonus(List<List<String>> matrix) {
        return matrix.stream()
                .flatMap(List::stream)
                .filter(symbol -> symbol.matches("10x|5x|\\+1000|\\+500|MISS"))
                .distinct() // Ensure unique symbols if needed
                .collect(Collectors.toList());
    }

    EnumBonusImpact determineBonusImpact(String bonusSymbol) {
        switch (bonusSymbol) {
            case "10x":
            case "5x":
                return EnumBonusImpact.MULTIPLY_REWARD;
            case "+1000":
            case "+500":
                return EnumBonusImpact.EXTRA_BONUS;
            case "MISS":
                return EnumBonusImpact.MISS;
            default:
                return null; // No bonus impact
        }
    }

    private double getMultiplierFromBonus(List<List<String>> matrix) {
        String bonusSymbol = matrix.stream()
                .flatMap(List::stream)
                .filter(symbol -> symbol.equals("10x") || symbol.equals("5x"))
                .findFirst()
                .orElse("1x"); // Default multiplier if none found

        if ("10x".equals(bonusSymbol)) {
            return 10;
        } else if ("5x".equals(bonusSymbol)) {
            return 5;
        } else {
            return 1;
        }
    }

    private double getExtraBonusFromBonus(List<List<String>> matrix) {
        String bonusSymbol = matrix.stream()
                .flatMap(List::stream)
                .filter(symbol -> symbol.equals("+1000") || symbol.equals("+500"))
                .findFirst()
                .orElse("+0"); // Default extra bonus if none found

        if ("+1000".equals(bonusSymbol)) {
            return 1000;
        } else if ("+500".equals(bonusSymbol)) {
            return 500;
        } else {
            return 0;
        }
    }

    public void printGameResult(GameResult gameResult) {
        System.out.println("Game Result:");
        System.out.println("Matrix:");
        gameResult.getMatrix().forEach(row -> System.out.println(row.stream().collect(Collectors.joining(", "))));
        System.out.println();
        System.out.println("Reward: " + gameResult.getReward());
        System.out.println();
        System.out.println("Applied Winning Combinations:");
        gameResult.getAppliedWinningCombinations().forEach((symbol, combinations) ->
                System.out.println(symbol + ": " + combinations.stream().map(EnumWinningCombinationType::name).collect(Collectors.joining(", "))));
        System.out.println();
        System.out.println("Applied Bonus Symbol: " + (gameResult.getAppliedBonusSymbol() != null ? gameResult.getAppliedBonusSymbol() : "MISS"));
    }
}
