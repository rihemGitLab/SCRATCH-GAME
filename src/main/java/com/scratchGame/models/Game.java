package com.scratchGame.models;

import java.util.Map;

public class Game {
    private int columns;
    private int rows;
    private Map<String, Symbol> symbols;
    private Probability probabilities;
    private Map<String, WinningCombination> winCombinations;

    public Game(int columns, int rows, Map<String, Symbol> symbols, Probability probabilities, Map<String, WinningCombination> winCombinations) {
        this.columns = columns;
        this.rows = rows;
        this.symbols = symbols;
        this.probabilities = probabilities;
        this.winCombinations = winCombinations;
    }

    public Game() {

    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, Symbol> symbols) {
        this.symbols = symbols;
    }

    public Probability getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(Probability probabilities) {
        this.probabilities = probabilities;
    }

    public Map<String, WinningCombination> getWinCombinations() {
        return winCombinations;
    }

    public void setWinCombinations(Map<String, WinningCombination> winCombinations) {
        this.winCombinations = winCombinations;
    }

    @Override
    public String toString() {
        return "GameConfig{" +
                "columns=" + columns +
                ", rows=" + rows +
                ", symbols=" + symbols +
                ", probabilities=" + probabilities +
                ", winCombinations=" + winCombinations +
                '}';
    }
}
