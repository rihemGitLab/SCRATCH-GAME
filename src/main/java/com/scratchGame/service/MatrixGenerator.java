package com.scratchGame.service;

import com.scratchGame.models.Game;
import com.scratchGame.models.Probability;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Generates a game matrix with symbols based on their probabilities.
 */
public class MatrixGenerator {

    private static final String DEFAULT_SYMBOL = " "; // Default symbol for uninitialized cells

    private final Game game;
    private final Random random;

    /**
     * Constructs a MatrixGenerator with the specified game configuration.
     *
     * @param game the game configuration, must not be null and should have positive rows and columns
     * @throws IllegalArgumentException if the game configuration is null or has non-positive rows/columns
     */
    public MatrixGenerator(Game game) {
        Objects.requireNonNull(game, "Game cannot be null");
        if (game.getRows() < 0 || game.getColumns() < 0) {
            throw new IllegalArgumentException("Rows and columns must be positive");
        }
        this.game = game;
        this.random = ThreadLocalRandom.current(); // Use ThreadLocalRandom for better performance
    }

    /**
     * Generates a game matrix with symbols based on their probabilities.
     *
     * @return a List of Lists representing the game matrix
     */
    public List<List<String>> generateMatrix() {
        int rows = game.getRows();
        int columns = game.getColumns();
        List<List<String>> matrix = new ArrayList<>(rows);

        List<String> combinedSymbols = getCombinedSymbolList();

        for (int row = 0; row < rows; row++) {
            matrix.add(generateRow(columns, combinedSymbols));
        }

        return matrix;
    }

    /**
     * Combines and shuffles symbols from standard and bonus probabilities.
     *
     * @return a List of combined and shuffled symbols
     */
    private List<String> getCombinedSymbolList() {
        Probability probabilities = Optional.ofNullable(game.getProbabilities())
                .orElse(new Probability(Collections.emptyList(), Collections.emptyMap()));

        List<String> standardSymbols = flattenSymbolProbabilities(probabilities.getStandardSymbolsProbabilities());
        List<String> bonusSymbols = flattenSymbolProbabilities(probabilities.getBonusSymbolsProbabilities());

        // Combine symbols and shuffle for better randomness
        List<String> combinedSymbols = new ArrayList<>(standardSymbols.size() + bonusSymbols.size());
        combinedSymbols.addAll(standardSymbols);
        combinedSymbols.addAll(bonusSymbols);
        Collections.shuffle(combinedSymbols, random); // Shuffle for better distribution

        return combinedSymbols;
    }

    /**
     * Flattens a list of maps representing symbol probabilities into a list of symbols.
     *
     * @param probabilities a list of maps where each map represents symbol probabilities
     * @return a List of symbols based on the probabilities
     */
    private List<String> flattenSymbolProbabilities(List<Map<String, Integer>> probabilities) {
        return probabilities.stream()
                .flatMap(map -> map.entrySet().stream()
                        .flatMap(entry -> Collections.nCopies(entry.getValue(), entry.getKey()).stream()))
                .collect(Collectors.toList());
    }

    /**
     * Flattens a map of symbol probabilities into a list of symbols.
     *
     * @param probabilities a map where keys are symbols and values are their probabilities
     * @return a List of symbols based on the probabilities
     */
    private List<String> flattenSymbolProbabilities(Map<String, Integer> probabilities) {
        return probabilities.entrySet().stream()
                .flatMap(entry -> Collections.nCopies(entry.getValue(), entry.getKey()).stream())
                .collect(Collectors.toList());
    }

    /**
     * Generates a row of symbols for the matrix.
     *
     * @param columns the number of columns in the row
     * @param symbols the list of symbols to use
     * @return a List of strings representing the row
     */
    private List<String> generateRow(int columns, List<String> symbols) {
        List<String> row = new ArrayList<>(Collections.nCopies(columns, DEFAULT_SYMBOL));
        for (int i = 0; i < Math.min(columns, symbols.size()); i++) {
            row.set(i, symbols.get(i));
        }
        return row;
    }
}
