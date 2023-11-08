package me.azulflame.trainmarch.dmhelper.service;

public enum QuestType {
    PLAY_BY_POST("Play By Post"),
    VOICE_CHAT_TEXT("Voice Chat Text"),
    VOICE_CHAT("Voice Chat");

    private final String name;
    QuestType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // get any difficulty that matches, lazy matching
    public static QuestType getClosestDifficulty(String text) {
        QuestType closest = VOICE_CHAT;
        for (QuestType questType : values()) {
            if (questType.getName().toLowerCase().contains(text.toLowerCase()) || text.toLowerCase().contains(questType.getName().toLowerCase()))
            {
                closest = questType;
            }
        }
        return closest;
    }
}
