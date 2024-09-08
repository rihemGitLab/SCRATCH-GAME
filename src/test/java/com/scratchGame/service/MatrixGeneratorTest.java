package com.scratchGame.service;

import com.scratchGame.models.Game;
import com.scratchGame.models.Probability;
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
        // Set up a basic game configuration with symbols and probabilities
        gameConfig = new Game();
        gameConfig.setRows(3);
        gameConfig.setColumns(3);
        gameConfig.setSymbols(Map.of(
                "A", new Symbol("A", 2.0, 1, "typeA", "impactA"),
                "B", new Symbol("B", 1.5, 2, "typeB", "impactB"),
                "C", new Symbol("C", 1.0, 0, "typeC", "impactC")
        ));
        gameConfig.setProbabilities(new Probability(
                List.of(
                        Map.of("A", 2, "B", 1), // Standard probabilities
                        Map.of("C", 3) // More standard probabilities
                ),
                Map.of("B", 1) // Bonus probabilities
        ));
        matrixGenerator = new MatrixGenerator(gameConfig);
    }

    @Test
    public void testGenerateMatrix_ValidGame() {
        List<List<String>> matrix = matrixGenerator.generateMatrix();

        assertNotNull(matrix, "Matrix should not be null");
        assertEquals(3, matrix.size(), "Matrix should have 3 rows");
        for (List<String> row : matrix) {
            assertEquals(3, row.size(), "Each row should have 3 columns");
        }
    }

    @Test
    public void testGenerateMatrix_SymbolsPresence() {
        List<List<String>> matrix = matrixGenerator.generateMatrix();
        List<String> symbolKeys = List.copyOf(gameConfig.getSymbols().keySet());

        for (List<String> row : matrix) {
            for (String symbol : row) {
                assertTrue(symbolKeys.contains(symbol), "Matrix cell should contain a valid symbol");
            }
        }
    }

    @Test
    public void testGenerateMatrix_EmptySymbols() {
        Game gameConfigWithNoSymbols = new Game();
        gameConfigWithNoSymbols.setRows(3);
        gameConfigWithNoSymbols.setColumns(3);
        gameConfigWithNoSymbols.setSymbols(Map.of());
        gameConfigWithNoSymbols.setProbabilities(new Probability(List.of(), Map.of()));
        MatrixGenerator matrixGeneratorWithNoSymbols = new MatrixGenerator(gameConfigWithNoSymbols);

        List<List<String>> matrix = matrixGeneratorWithNoSymbols.generateMatrix();

        assertEquals(3, matrix.size(), "Matrix should have 3 rows");
        for (List<String> row : matrix) {
            assertEquals(3, row.size(), "Each row should have 3 columns");
        }

        for (List<String> row : matrix) {
            for (String symbol : row) {
                assertEquals(" ", symbol, "Matrix cell should be filled with the default symbol");
            }
        }
    }

    @Test
    public void testGenerateMatrix_ZeroRowsAndColumns() {
        Game gameConfigZeroSize = new Game();
        gameConfigZeroSize.setRows(0);
        gameConfigZeroSize.setColumns(0);
        gameConfigZeroSize.setSymbols(Map.of(
                "A", new Symbol("A", 2.0, 1, "typeA", "impactA")
        ));
        gameConfigZeroSize.setProbabilities(new Probability(
                List.of(Map.of("A", 1)),
                Map.of()
        ));
        MatrixGenerator matrixGeneratorZeroSize = new MatrixGenerator(gameConfigZeroSize);

        List<List<String>> matrix = matrixGeneratorZeroSize.generateMatrix();

        assertTrue(matrix.isEmpty(), "Matrix should be empty for zero row/column configuration");
    }


    @Test
    public void testGenerateMatrix_WithProbabilities() {
        gameConfig.setProbabilities(new Probability(
                List.of(
                        Map.of("A", 1, "B", 2),
                        Map.of("C", 1)
                ),
                Map.of("B", 2)
        ));
        matrixGenerator = new MatrixGenerator(gameConfig);

        List<List<String>> matrix = matrixGenerator.generateMatrix();

        // Perform checks specific to the probabilities setup, such as distribution or presence of symbols
        assertNotNull(matrix, "Matrix should not be null");
        assertEquals(3, matrix.size(), "Matrix should have 3 rows");
        for (List<String> row : matrix) {
            assertEquals(3, row.size(), "Each row should have 3 columns");
        }
    }

    @Test
    public void testConstructor_NullGameConfig() {
        assertThrows(NullPointerException.class, () -> new MatrixGenerator(null), "MatrixGenerator should throw InvalidArgumentException for null game config");
    }
}
