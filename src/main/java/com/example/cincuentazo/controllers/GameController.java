package com.example.cincuentazo.controllers;

public class GameController {
    public void setNumberOfPlayers(int players) {
        if (players < 2) players = 2;
        if (players > 4) players = 4;

    }
}
