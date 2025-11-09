package com.example.cincuentazo.models;

public class PlayerModel {
    private final String name;
    private final HandModel hand;
    private final boolean human;
    private boolean eliminated = false;

    public PlayerModel(String name, boolean human) {
        this.name = name;
        this.human = human;
        this.hand = new HandModel();
    }

    public String getName() {
        return name;
    }

    public HandModel getHand() {
        return hand;
    }

    public boolean isHuman() {
        return human;
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
}