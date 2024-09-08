package com.scratchGame.models;

import com.scratchGame.enums.EnumWinningCombinationType;
import com.scratchGame.enums.WinningCondition;
import com.scratchGame.enums.WinningGroup;

import java.util.List;

public class WinningCombination {
    private EnumWinningCombinationType combinationType;
    private double rewardMultiplier;
    private WinningCondition when;
    private WinningGroup group;
    private int count;
    private List<List<String>> coveredAreas;

    public WinningCombination(EnumWinningCombinationType combinationType, double rewardMultiplier, WinningCondition when, WinningGroup group, int count, List<List<String>> coveredAreas) {
        this.combinationType = combinationType;
        this.rewardMultiplier = rewardMultiplier;
        this.when = when;
        this.group = group;
        this.count = count;
        this.coveredAreas = coveredAreas;
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

    public WinningCondition getWhen() {
        return when;
    }

    public void setWhen(WinningCondition when) {
        this.when = when;
    }

    public WinningGroup getGroup() {
        return group;
    }

    public void setGroup(WinningGroup group) {
        this.group = group;
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
        return "WinningCombination{" +
                "combinationType=" + combinationType +
                ", rewardMultiplier=" + rewardMultiplier +
                ", when=" + when +
                ", group=" + group +
                ", count=" + count +
                ", coveredAreas=" + coveredAreas +
                '}';
    }
}
