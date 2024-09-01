package com.scratchGame.service;

import com.scratchGame.enums.EnumBonusImpact;
import com.scratchGame.enums.EnumWinningCombinationType;
import com.scratchGame.exceptions.InvalidArgumentException;
import com.scratchGame.models.Game;
import com.scratchGame.models.GameResult;
import com.scratchGame.models.Symbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private Game gameConfig;
    private MatrixGenerator matrixGenerator;
    private RewardCalculator rewardCalculator;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        // Mock MatrixGenerator and RewardCalculator
        matrixGenerator = new MatrixGenerator(new Game()); // Just for initialization
        rewardCalculator = new RewardCalculator() {
            @Override
            public double calculateReward(EnumWinningCombinationType combinationType, List<List<String>> matrix) throws InvalidArgumentException {
                // Simple reward calculation for testing
                return switch (combinationType) {
                    case same_symbol_3_times -> 10;
                    case same_symbol_4_times -> 20;
                    case same_symbol_5_times -> 30;
                    default -> 0;
                };
            }
        };

        gameConfig = new Game();
        gameConfig.setRows(3);
        gameConfig.setColumns(3);
        gameConfig.setSymbols(Map.of(
                "A", new Symbol("A", 2.0, 1, "typeA", "impactA"),
                "B", new Symbol("B", 1.5, 2, "typeB", "impactB"),
                "C", new Symbol("C", 1.0, 0, "typeC", "impactC"),
                "10x", new Symbol("10x", 0, 0, "bonus", "multiply"),
                "+1000", new Symbol("+1000", 0, 0, "bonus", "extra")
        ));

        matrixGenerator = new MatrixGenerator(gameConfig);
        gameService = new GameService(gameConfig, matrixGenerator, rewardCalculator);
    }

    @Test
    public void testStartGame_ValidGame() {
        // Prepare a matrix with known values
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("A", "A", "A"),
                Arrays.asList("B", "B", "B"),
                Arrays.asList("C", "C", "C")
        );
        MatrixGenerator mockMatrixGenerator = new MatrixGenerator(gameConfig) {
            @Override
            public List<List<String>> generateMatrix() {
                return matrix;
            }
        };
        gameService = new GameService(gameConfig, mockMatrixGenerator, rewardCalculator);

        GameResult result = gameService.startGame(10);

        assertNotNull(result);
        assertEquals(matrix, result.getMatrix());
        assertEquals(300, result.getReward()); // Based on reward calculation logic and betting amount
        assertEquals(Collections.emptyList(), result.getAppliedBonusSymbol());
    }

    @Test
    public void testStartGame_WithBonus() {
        // Prepare a matrix with bonus symbols
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("A", "10x", "A"),
                Arrays.asList("C", "+1000", "B"),
                Arrays.asList("B", "C", "C")
        );
        MatrixGenerator mockMatrixGenerator = new MatrixGenerator(gameConfig) {
            @Override
            public List<List<String>> generateMatrix() {
                return matrix;
            }
        };
        gameService = new GameService(gameConfig, mockMatrixGenerator, rewardCalculator);

        GameResult result = gameService.startGame(10);

        assertNotNull(result);
        assertEquals(matrix, result.getMatrix());
        assertEquals(11000, result.getReward());
        assertEquals(List.of("10x", "+1000"), result.getAppliedBonusSymbol());
    }

    @Test
    public void testStartGame_NoWins() {
        // Prepare a matrix with no winning symbols
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("A", "B", "C"),
                Arrays.asList("B", "C", "F"),
                Arrays.asList("F", "A", "E")
        );
        MatrixGenerator mockMatrixGenerator = new MatrixGenerator(gameConfig) {
            @Override
            public List<List<String>> generateMatrix() {
                return matrix;
            }
        };
        gameService = new GameService(gameConfig, mockMatrixGenerator, rewardCalculator);

        GameResult result = gameService.startGame(10);

        assertNotNull(result);
        assertEquals(matrix, result.getMatrix());
        assertEquals(0, result.getReward());
        assertEquals(Collections.emptyList(), result.getAppliedBonusSymbol());
    }

    @Test
    public void testStartGame_HandlingExceptions() {
        MatrixGenerator mockMatrixGenerator = new MatrixGenerator(gameConfig) {
            @Override
            public List<List<String>> generateMatrix() {
                throw new RuntimeException("Matrix generation failed");
            }
        };
        gameService = new GameService(gameConfig, mockMatrixGenerator, rewardCalculator);

        assertThrows(RuntimeException.class, () -> gameService.startGame(10), "Matrix generation failed");
    }

    @Test
    public void testConstructor_NullArguments() {
        assertThrows(InvalidArgumentException.class, () -> new GameService(null, matrixGenerator, rewardCalculator),
                "Constructor should throw exception for null GameConfig");

        assertThrows(InvalidArgumentException.class, () -> new GameService(gameConfig, null, rewardCalculator),
                "Constructor should throw exception for null MatrixGenerator");

        assertThrows(InvalidArgumentException.class, () -> new GameService(gameConfig, matrixGenerator, null),
                "Constructor should throw exception for null RewardCalculator");
    }

    @Test
    public void testStartGame_MultipleBonuses() {
        // Prepare a matrix with multiple bonus symbols
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("A", "10x", "+1000"),
                Arrays.asList("B", "A", "B"),
                Arrays.asList("C", "C", "C")
        );
        MatrixGenerator mockMatrixGenerator = new MatrixGenerator(gameConfig) {
            @Override
            public List<List<String>> generateMatrix() {
                return matrix;
            }
        };
        gameService = new GameService(gameConfig, mockMatrixGenerator, rewardCalculator);

        GameResult result = gameService.startGame(10);

        assertNotNull(result);
        assertEquals(matrix, result.getMatrix());
        assertEquals(11000, result.getReward()); // Based on combining both bonuses
        assertEquals(List.of("10x","+1000"), result.getAppliedBonusSymbol()); // Assuming "10x" takes precedence
    }

    @Test
    public void testStartGame_MultipleWinningCombinations() {
        // Prepare a matrix with multiple winning combinations
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("A", "A", "A"),
                Arrays.asList("B", "B", "B"),
                Arrays.asList("C", "C", "C")
        );
        MatrixGenerator mockMatrixGenerator = new MatrixGenerator(gameConfig) {
            @Override
            public List<List<String>> generateMatrix() {
                return matrix;
            }
        };
        gameService = new GameService(gameConfig, mockMatrixGenerator, rewardCalculator);

        GameResult result = gameService.startGame(10);

        assertNotNull(result);
        assertEquals(matrix, result.getMatrix());
        assertEquals(300, result.getReward()); // Ensure correct reward based on multiple combinations
        assertEquals(Collections.emptyList(), result.getAppliedBonusSymbol());
    }

    @Test
    public void testStartGame_BonusPrioritization() {
        // Prepare a matrix where the bonus symbols have different priorities
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("10x", "B", "C"),
                Arrays.asList("B", "+1000", "C"),
                Arrays.asList("C", "C", "C")
        );
        MatrixGenerator mockMatrixGenerator = new MatrixGenerator(gameConfig) {
            @Override
            public List<List<String>> generateMatrix() {
                return matrix;
            }
        };
        gameService = new GameService(gameConfig, mockMatrixGenerator, rewardCalculator);

        GameResult result = gameService.startGame(10);

        assertNotNull(result);
        assertEquals(matrix, result.getMatrix());
        assertEquals(16000, result.getReward()); // Assuming the higher priority bonus "10x" is applied
        assertEquals(List.of("10x","+1000"), result.getAppliedBonusSymbol());
    }
}
