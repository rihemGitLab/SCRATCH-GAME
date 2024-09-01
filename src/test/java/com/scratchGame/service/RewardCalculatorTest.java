package com.scratchGame.service;

import com.scratchGame.enums.EnumWinningCombinationType;
import com.scratchGame.exceptions.InvalidArgumentException;
import com.scratchGame.models.Game;
import com.scratchGame.models.Probability;
import com.scratchGame.models.Symbol;
import com.scratchGame.models.WinningCombination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RewardCalculatorTest {

    private RewardCalculator rewardCalculator;
    private Game game;

    @BeforeEach
    public void setup() {
        // Create Symbol objects
        Map<String, Symbol> symbols = new HashMap<>();
        symbols.put("A", new Symbol("A", 2.0, 1, "typeA", "impactA"));
        symbols.put("B", new Symbol("B", 1.5, 2, "typeB", "impactB"));
        symbols.put("C", new Symbol("C", 1.0, 0, "typeC", "impactC"));

        // Create WinningCombination objects
        Map<String, WinningCombination> winCombinations = new HashMap<>();
        winCombinations.put("same_symbol_3_times", new WinningCombination(EnumWinningCombinationType.same_symbol_3_times, 5.0, "When symbol appears", 3, Collections.emptyList()));
        winCombinations.put("same_symbols_horizontally", new WinningCombination(EnumWinningCombinationType.same_symbols_horizontally, 4.0, "When symbols appear horizontally", 3, Collections.emptyList()));
        winCombinations.put("same_symbols_vertically", new WinningCombination(EnumWinningCombinationType.same_symbols_vertically, 3.0, "When symbols appear vertically", 3, Collections.emptyList()));
        winCombinations.put("same_symbols_diagonally_left_to_right", new WinningCombination(EnumWinningCombinationType.same_symbols_diagonally_left_to_right, 6.0, "When symbols appear diagonally from left to right", 3, Collections.emptyList()));
        winCombinations.put("same_symbols_diagonally_right_to_left", new WinningCombination(EnumWinningCombinationType.same_symbols_diagonally_right_to_left, 6.0, "When symbols appear diagonally from right to left", 3, Collections.emptyList()));

        // Create Probability object
        List<Map<String, Integer>> standardSymbolsProbabilities = Arrays.asList(
                Map.of("A", 10, "B", 20, "C", 30),
                Map.of("A", 15, "B", 25, "C", 35)
        );
        Map<String, Integer> bonusSymbolsProbabilities = Map.of(
                "BonusA", 5,
                "BonusB", 10
        );
        Probability probabilities = new Probability(standardSymbolsProbabilities, bonusSymbolsProbabilities);

        game = new Game(3, 3, symbols, probabilities, winCombinations);
        rewardCalculator = new RewardCalculator(game);


        // Create Game instance with parameters
        game = new Game(3, 3, symbols, probabilities, winCombinations);
        rewardCalculator = new RewardCalculator(game);
    }

    @Test
    public void testConstructor_WithNullGame_ThrowsException() {
        assertThrows(InvalidArgumentException.class, () -> new RewardCalculator(null));
    }

    @Test
    public void testCalculateReward_ForSameSymbol3Times() {
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("A", "A", "A"),
                Arrays.asList("E", "B", "B"),
                Arrays.asList("C", "F", "C")
        );

        double reward = rewardCalculator.calculateReward(EnumWinningCombinationType.same_symbol_3_times, matrix);

        assertEquals(5.0 * 2.0 * 3, reward, 0.001); // baseRewardMultiplier * symbolMultiplier * count
    }

    @Test
    public void testCalculateReward_Horizontally() {
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("A", "A", "A"),
                Arrays.asList("B", "10x", "B"),
                Arrays.asList("5x", "+1000", "C")
        );

        double reward = rewardCalculator.calculateReward(EnumWinningCombinationType.same_symbols_horizontally, matrix);

        assertEquals(4.0 * 2.0, reward, 0.001); // baseRewardMultiplier * symbolMultiplier for one line
    }

    @Test
    public void testCalculateReward_Vertically() {
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("A", "B", "C"),
                Arrays.asList("A", "B", "C"),
                Arrays.asList("A", "B", "C")
        );

        double reward = rewardCalculator.calculateReward(EnumWinningCombinationType.same_symbols_vertically, matrix);

        assertEquals(3.0 * 2.0, reward, 0.001); // baseRewardMultiplier * symbolMultiplier for one line
    }

    @Test
    public void testCalculateReward_DiagonallyLeftToRight() {
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("A", "B", "C"),
                Arrays.asList("D", "A", "E"),
                Arrays.asList("F", "G", "A")
        );

        double reward = rewardCalculator.calculateReward(EnumWinningCombinationType.same_symbols_diagonally_left_to_right, matrix);

        assertEquals(6.0 * 2.0, reward, 0.001); // baseRewardMultiplier * symbolMultiplier for one diagonal
    }

    @Test
    public void testCalculateReward_DiagonallyRightToLeft() {
        // Matrix with a winning diagonal from top-right to bottom-left
        List<List<String>> matrix = Arrays.asList(
                Arrays.asList("C", "B", "A"),
                Arrays.asList("D", "A", "E"),
                Arrays.asList("A", "G", "A")  // Diagonal "A", "A", "A"
        );

        // Calculate reward for the diagonal right to left winning combination
        double reward = rewardCalculator.calculateReward(
                EnumWinningCombinationType.same_symbols_diagonally_right_to_left,
                matrix
        );

        // Expected reward: baseMultiplier * symbolMultiplier
        // For the diagonal "A", "A", "A" with base multiplier 6.0 and symbol multiplier 2.0
        assertEquals(6.0 * 2.0, reward, 0.001); // baseRewardMultiplier * symbolMultiplier
    }

    @Test
    public void testCalculateReward_NullArguments() {
        assertThrows(InvalidArgumentException.class, () -> rewardCalculator.calculateReward(null, null));
        assertThrows(InvalidArgumentException.class, () -> rewardCalculator.calculateReward(EnumWinningCombinationType.same_symbol_3_times, null));
        assertThrows(InvalidArgumentException.class, () -> rewardCalculator.calculateReward(null, Arrays.asList(Arrays.asList("A"))));
    }
}
