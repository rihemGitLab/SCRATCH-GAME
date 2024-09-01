package com.scratchGame;

import com.scratchGame.models.Game;
import com.scratchGame.models.GameResult;
import com.scratchGame.service.GameService;
import com.scratchGame.service.MatrixGenerator;
import com.scratchGame.service.RewardCalculator;
import com.scratchGame.utils.JsonUtils;

public class Application {

    public static void main(String[] args) {
        String configFilePath = null;
        double bettingAmount = 100;// Default betting amount

        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            if ("--config".equals(args[i]) && i + 1 < args.length) {
                configFilePath = args[i + 1];
                i++; // Skip the next argument
            } else if ("--betting-amount".equals(args[i]) && i + 1 < args.length) {
                try {
                    bettingAmount = Double.parseDouble(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid betting amount provided. Using default value 1000.0.");
                }
                i++; // Skip the next argument
            }
        }

        if (configFilePath == null) {
            System.err.println("Configuration file path is required. Use --config <path> to specify.");
            return;
        }

        Game gameConfig = null;

        try {
            // Load the game configuration from JSON
            gameConfig = JsonUtils.readGameConfig(configFilePath);

            // Verify that the game configuration was loaded correctly
            if (gameConfig == null) {
                System.err.println("Failed to load game configuration.");
                return;
            }

            System.out.println("Game Configuration Loaded:");
            System.out.println(gameConfig);

            // Initialize services
            MatrixGenerator matrixGenerator = new MatrixGenerator(gameConfig);
            RewardCalculator rewardCalculator = new RewardCalculator(gameConfig);
            GameService gameService = new GameService(gameConfig, matrixGenerator, rewardCalculator);

            // Start the game
            GameResult gameResult = gameService.startGame(bettingAmount);

            // Print the game result
            gameService.printGameResult(gameResult);


        } catch (Exception e) {
            System.err.println("An error occurred during the game execution:");
            e.printStackTrace();
        }
    }
}
