package me.azulflame.trainmarch.dmhelper.service;

public enum Difficulty {
    EASY(4, "Easy"),
    MEDIUM(3, "Medium"),
    HARD(2, "Hard"),
    DEADLY(1,"Deadly"),
    TPK_LIKELY(0,"TPK likely");

    private final int value;
    private final String name;
    private Difficulty(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    // get any difficulty that matches
    public static Difficulty getClosestDifficulty(String text) {
        Difficulty closest = EASY;
        for (Difficulty difficulty : values()) {
            if (difficulty.getName().toLowerCase().contains(text.toLowerCase()) || text.toLowerCase().contains(difficulty.getName().toLowerCase()))
            {
                closest = difficulty;
            }
        }
        return closest;
    }
}
