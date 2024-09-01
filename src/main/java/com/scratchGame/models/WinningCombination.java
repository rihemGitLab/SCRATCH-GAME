package com.scratchGame.models;

import com.scratchGame.enums.EnumWinningCombinationType;
import org.json.JSONArray;

import java.util.List;

public class WinningCombination {
    private EnumWinningCombinationType combinationType;
    private double rewardMultiplier;
    private String when;
    private int count;
    private List<List<String>> coveredAreas;

    public WinningCombination(EnumWinningCombinationType combinationType, double rewardMultiplier, String when, int count, List<List<String>> coveredAreas) {
        this.combinationType = combinationType;
        this.rewardMultiplier = rewardMultiplier;
        this.when = when;
        this.count = count;
        this.coveredAreas = coveredAreas;
    }

    public WinningCombination(double rewardMultiplier, String when, int count, String group, JSONArray coveredAreas) {
    }


    public EnumWinningCombinationType getCombinationType() {
        return combinationType;
    }

    public void setCombinationType(EnumWinningCombinationType combinationType) {
        this.combinationType = combinationType;
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public void setRewardMultiplier(double rewardMultiplier) {
        this.rewardMultiplier = rewardMultiplier;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<List<String>> getCoveredAreas() {
        return coveredAreas;
    }

    public void setCoveredAreas(List<List<String>> coveredAreas) {
        this.coveredAreas = coveredAreas;
    }

    @Override
    public String toString() {
        return "WinningCombinationConfig{" +
                "combinationType=" + combinationType +
                ", rewardMultiplier=" + rewardMultiplier +
                ", when='" + when + '\'' +
                ", count=" + count +
                ", coveredAreas=" + coveredAreas +
                '}';
    }
}
