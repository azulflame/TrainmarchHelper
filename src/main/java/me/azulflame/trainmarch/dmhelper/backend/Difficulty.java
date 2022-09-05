package me.azulflame.trainmarch.dmhelper.backend;

public enum Difficulty {
    EASY(4, "easy"),
    MEDIUM(3, "medium"),
    HARD(2, "hard"),
    DEADLY(1,"deadly"),
    VERY_DEADLY(0, "very deadly"),
    TPK_POSSIBLE(5,"TPK possible");

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

    // closest difficulty by levenshtein distance
    public static Difficulty getClosestDifficulty(String text) {
        Difficulty closest = EASY;
        int distance = text.length();
        for (Difficulty difficulty : values()) {
            int dist = calculate(text, difficulty.getName());
            if (dist < distance) {
                closest = difficulty;
                distance = dist;
            }
        }
        return closest;
    }

    private static int calculate(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }
        return dp[x.length()][y.length()];
    }

    private static int min(int a, int b, int c)
    {
        return Math.min(a, Math.min(b,c));
    }
    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
}
