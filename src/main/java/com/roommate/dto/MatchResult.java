package com.roommate.dto;

import com.roommate.model.Room;

public class MatchResult {
    private final Room room;
    private final int score;
    private final String summary;

    public MatchResult(Room room, int score, String summary) {
        this.room = room;
        this.score = score;
        this.summary = summary;
    }

    public Room getRoom() {
        return room;
    }

    public int getScore() {
        return score;
    }

    public String getSummary() {
        return summary;
    }
}
