package com.scratchGame.service;

import com.scratchGame.exceptions.InvalidArgumentException;
import com.scratchGame.models.Game;
import com.scratchGame.models.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates a game matrix based on the provided configuration.
 */
public class MatrixGenerator {

    private static final String DEFAULT_SYMBOL = " "; // Default symbol for uninitialized cells

    private final Game game;
    private final Random random;

    public MatrixGenerator(Game game) {
        if (game == null) {
            throw new InvalidArgumentException("Game cannot be null");
        }
        this.game = game;
        this.random = ThreadLocalRandom.current(); // Use ThreadLocalRandom for better performance
    }

    /**
     * Generates a game matrix with random symbols.
     *
     * @return a List of Lists representing the game matrix
     */
    public List<List<String>> generateMatrix() {
        int rows = game.getRows();
        int columns = game.getColumns();
        List<List<String>> matrix = new ArrayList<>();

        List<String> symbolKeys = getSymbolKeys();

        for (int row = 0; row < rows; row++) {
            List<String> rowList = new ArrayList<>();
            for (int col = 0; col < columns; col++) {
                rowList.add(getRandomSymbol(symbolKeys));
            }
            matrix.add(rowList);
        }

        return matrix;
    }

    private List<String> getSymbolKeys() {
        Map<String, Symbol> symbols = game.getSymbols();
        return List.copyOf(symbols.keySet()); // Use List.copyOf for immutability
    }

    private String getRandomSymbol(List<String> symbolKeys) {
        if (symbolKeys.isEmpty()) {
            return DEFAULT_SYMBOL; // Handle case with no symbols
        }
        int index = random.nextInt(symbolKeys.size());
        return symbolKeys.get(index); // Assumes symbols are Strings
    }
}
