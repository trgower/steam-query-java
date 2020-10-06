package com.github.boubari97.serversquery;

import java.util.Objects;

public class Player {

    private byte index;
    private String name;
    private int score;
    private float duration;

    public Player(byte index, String name, int score, float duration) {
        this.index = index;
        this.score = score;
        this.duration = duration;

        if (name.length() == 0) {
            this.name = "Unknown";
        } else {
            this.name = name;
        }
    }

    public byte getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public float getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Player{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return index == player.index &&
                score == player.score &&
                Float.compare(player.duration, duration) == 0 &&
                Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, name, score, duration);
    }
}
