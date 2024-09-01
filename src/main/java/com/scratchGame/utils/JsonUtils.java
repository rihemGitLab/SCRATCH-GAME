package com.scratchGame.utils;

import com.scratchGame.enums.EnumWinningCombinationType;
import com.scratchGame.exceptions.ConfigurationException;
import com.scratchGame.exceptions.InvalidArgumentException;
import com.scratchGame.models.Game;
import com.scratchGame.models.Probability;
import com.scratchGame.models.Symbol;
import com.scratchGame.models.WinningCombination;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class JsonUtils {

    public static Game readGameConfig(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }

            JSONObject jsonObject = new JSONObject(jsonStringBuilder.toString());

            int columns = jsonObject.getInt("columns");
            int rows = jsonObject.getInt("rows");

            // Parse symbols
            Map<String, Symbol> symbols = parseSymbols(jsonObject.getJSONObject("symbols"));

            // Parse probabilities
            Probability probabilities = parseProbabilities(jsonObject.getJSONObject("probabilities"));

            // Parse win combinations
            Map<String, WinningCombination> winCombinations = parseWinningCombinations(jsonObject.getJSONObject("win_combinations"));

            return new Game(columns, rows, symbols, probabilities, winCombinations);
        } catch (IOException e) {
            throw new ConfigurationException("Failed to read config file", e);
        } catch (JSONException e) {
            throw new ConfigurationException("Failed to parse config file", e);
        }
    }

    private static Map<String, Symbol> parseSymbols(JSONObject symbolsJson) throws JSONException {
        Map<String, Symbol> symbols = new HashMap<>();
        Iterator<String> keys = symbolsJson.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject symbolJson = symbolsJson.getJSONObject(key);
            double rewardMultiplier = symbolJson.optDouble("reward_multiplier", 0.0);
            int extra = symbolJson.optInt("extra", 0);
            String type = symbolJson.optString("type", "standard");
            String impact = symbolJson.optString("impact", "");

            Symbol symbolConfig = new Symbol(key, rewardMultiplier, extra, type, impact);
            symbols.put(key, symbolConfig);

            System.out.println("Symbol Added to Map: " + symbols.get(key));
        }

        return symbols;
    }



    private static Probability parseProbabilities(JSONObject probabilitiesJson) throws JSONException {
        JSONArray standardSymbolsArray = probabilitiesJson.getJSONArray("standard_symbols");
        List<Map<String, Integer>> standardSymbols = parseStandardSymbols(standardSymbolsArray);

        JSONObject bonusSymbolsJson = probabilitiesJson.getJSONObject("bonus_symbols");
        Map<String, Integer> bonusSymbols = parseBonusSymbols(bonusSymbolsJson);

        return new Probability(standardSymbols, bonusSymbols);
    }

    private static List<Map<String, Integer>> parseStandardSymbols(JSONArray standardSymbolsArray) throws JSONException {
        List<Map<String, Integer>> standardSymbolsList = new ArrayList<>();

        for (int i = 0; i < standardSymbolsArray.length(); i++) {
            JSONObject item = standardSymbolsArray.getJSONObject(i);
            Map<String, Integer> symbolMap = new HashMap<>();

            JSONObject symbolsObject = item.getJSONObject("symbols");
            Iterator<String> keys = symbolsObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                symbolMap.put(key, symbolsObject.getInt(key));
            }

            standardSymbolsList.add(symbolMap);
        }

        return standardSymbolsList;
    }

    private static Map<String, Integer> parseBonusSymbols(JSONObject bonusSymbolsJson) throws JSONException {
        Map<String, Integer> bonusSymbols = new HashMap<>();
        JSONObject symbolsObject = bonusSymbolsJson.getJSONObject("symbols");
        Iterator<String> keys = symbolsObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            bonusSymbols.put(key, symbolsObject.getInt(key));
        }

        return bonusSymbols;
    }

    private static Map<String, WinningCombination> parseWinningCombinations(JSONObject winCombinationsJson) throws JSONException {
        Map<String, WinningCombination> winCombinations = new HashMap<>();
        Iterator<String> keys = winCombinationsJson.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject wcJson = winCombinationsJson.getJSONObject(key);

            // Extract values
            EnumWinningCombinationType combinationType;
            try {
                combinationType = EnumWinningCombinationType.valueOf(key);
            } catch (InvalidArgumentException e) {
                // Handle unknown enum types gracefully
                combinationType = EnumWinningCombinationType.same_symbol_3_times; // Default or handle accordingly
                System.err.println("Warning: Invalid combination type '" + key + "'. Defaulting to 'same_symbol_3_times'.");
            }

            double rewardMultiplier = wcJson.optDouble("reward_multiplier", 0.0);
            String when = wcJson.optString("when", "");
            int count = wcJson.optInt("count", 0);
            JSONArray coveredAreasArray = wcJson.optJSONArray("covered_areas");

            // Parse coveredAreas
            List<List<String>> coveredAreas = new ArrayList<>();
            if (coveredAreasArray != null) {
                for (int i = 0; i < coveredAreasArray.length(); i++) {
                    JSONArray areaArray = coveredAreasArray.getJSONArray(i);
                    List<String> areaList = new ArrayList<>();
                    for (int j = 0; j < areaArray.length(); j++) {
                        areaList.add(areaArray.getString(j));
                    }
                    coveredAreas.add(areaList);
                }
            }

            // Create WinningCombination object
            WinningCombination wcConfig = new WinningCombination(combinationType, rewardMultiplier, when, count, coveredAreas);

            // Add to map
            winCombinations.put(key, wcConfig);

            // Debugging statements
            System.out.println("Parsed winning combination: " + key);
            System.out.println("Winning Combination: " + wcConfig);
        }

        return winCombinations;
    }

}
