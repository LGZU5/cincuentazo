package com.example.cincuentazo.models;

import java.util.Arrays;
import java.util.List;

public class CardModel {

    public static final List<String> RANKS = Arrays.asList(
            "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"
    );

    public static final List<String> SUITS = Arrays.asList(
            "picas", "corazones", "diamantes", "treboles"
    );

    public final String rank;
    public final String suit;

    public CardModel(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int baseValue() {
        if (rank == null) return 0;
        switch (rank) {
            case "2": return 2;
            case "3": return 3;
            case "4": return 4;
            case "5": return 5;
            case "6": return 6;
            case "7": return 7;
            case "8": return 8;
            case "9": return 0;
            case "10": return 10;
            case "J": return -10;
            case "Q": return -10;
            case "K": return -10;
            case "A": return 1;
            default: return 0;
        }
    }

    public int valueWhenPlayed(int currentSum) {
        if ("A".equals(rank)) {
            return (currentSum + 10 <= 50) ? 10 : 1;
        }
        return baseValue();
    }

    public boolean isPlayable(int currentSum) {
        int v = valueWhenPlayed(currentSum);
        return (currentSum + v) <= 50;
    }

    @Override
    public String toString() {
        return rank + (suit == null ? "" : " " + suit);
    }
}
