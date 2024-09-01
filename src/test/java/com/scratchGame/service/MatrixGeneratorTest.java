package com.scratchGame.service;

import com.scratchGame.exceptions.InvalidArgumentException;
import com.scratchGame.models.Game;
import com.scratchGame.models.Symbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixGeneratorTest {

    private Game gameConfig;
    private MatrixGenerator matrixGenerator;

    @BeforeEach
    public void setUp() {
        // Set up a basic game configuration
        gameConfig = new Game();
        gameConfig.setRows(3);
        gameConfig.setColumns(3);
        gameConfig.setSymbols(Map.of(
                "A", new Symbol("A", 2.0, 1, "typeA", "impactA"),
                "B", new Symbol("B", 1.5, 2, "typeB", "impactB"),
                "C", new Symbol("C", 1.0, 0, "typeC", "impactC")
        ));
        matrixGenerator = new MatrixGenerator(gameConfig);
    }

    @Test
    public void testGenerateMatrix_ValidGame() {
        List<List<String>> matrix = matrixGenerator.generateMatrix();

        // Check that the matrix is not null
        assertNotNull(matrix);
        // Check that the matrix has the correct number of rows
        assertEquals(3, matrix.size());
        // Check that each row has the correct number of columns
        for (List<String> row : matrix) {
            assertEquals(3, row.size());
        }
    }

    @Test
    public void testGenerateMatrix_SymbolsPresence() {
        List<List<String>> matrix = matrixGenerator.generateMatrix();
        List<String> symbolKeys = List.copyOf(gameConfig.getSymbols().keySet());

        // Check that all elements in the matrix are from the valid set of symbols
        for (List<String> row : matrix) {
            for (String symbol : row) {
                assertTrue(symbolKeys.contains(symbol));
            }
        }
    }

    @Test
    public void testGenerateMatrix_EmptySymbols() {
        // Configure the game with no symbols
        Game gameConfigWithNoSymbols = new Game();
        gameConfigWithNoSymbols.setRows(3);
        gameConfigWithNoSymbols.setColumns(3);
        gameConfigWithNoSymbols.setSymbols(Map.of());
        MatrixGenerator matrixGeneratorWithNoSymbols = new MatrixGenerator(gameConfigWithNoSymbols);

        List<List<String>> matrix = matrixGeneratorWithNoSymbols.generateMatrix();

        // Check that the matrix has the correct number of rows and columns
        assertEquals(3, matrix.size());
        for (List<String> row : matrix) {
            assertEquals(3, row.size());
        }

        // Check that all cells contain the DEFAULT_SYMBOL when no symbols are defined
        for (List<String> row : matrix) {
            for (String symbol : row) {
                assertEquals(" ", symbol);
            }
        }
    }

    @Test
    public void testConstructor_NullGameConfig() {
        // Expect an InvalidArgumentException to be thrown when initializing with a null game config
        assertThrows(InvalidArgumentException.class, () -> new MatrixGenerator(null));
    }

    @Test
    public void testGenerateMatrix_ZeroRowsAndColumns() {
        // Configure the game with zero rows and columns
        Game gameConfigZeroSize = new Game();
        gameConfigZeroSize.setRows(0);
        gameConfigZeroSize.setColumns(0);
        gameConfigZeroSize.setSymbols(Map.of(
                "A", new Symbol("A", 2.0, 1, "typeA", "impactA")
        ));
        MatrixGenerator matrixGeneratorZeroSize = new MatrixGenerator(gameConfigZeroSize);

        List<List<String>> matrix = matrixGeneratorZeroSize.generateMatrix();

        // Check that the matrix has no rows
        assertEquals(0, matrix.size());
    }
}
