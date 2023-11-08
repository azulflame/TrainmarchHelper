package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.service.Difficulty;
import me.azulflame.trainmarch.dmhelper.service.Items;
import me.azulflame.trainmarch.dmhelper.service.QuestType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static java.lang.Math.min;

public class NewRewardsCommand extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("rewards")) {
            if (!event.getChannel().getId().equals("931707960491671552") && !event.getChannel().getId().equals("1015883484545433685")) {
                event.reply("This must be run in the <#931707960491671552> channel").setEphemeral(true).queue();
            } else {
                int t1 = (int) event.getOption("t1").getAsDouble();
                int t2 = (int) event.getOption("t2").getAsDouble();
                int t3 = (int) event.getOption("t3").getAsDouble();
                int t4 = (int) event.getOption("t4").getAsDouble();
                double length = event.getOption("time").getAsDouble();

                Difficulty difficulty = Difficulty.getClosestDifficulty(event.getOption("difficulty").getAsString());
                QuestType type = QuestType.getClosestDifficulty(event.getOption("quest-type").getAsString());
                event.reply(getRewards(difficulty, length, type, t1, t2, t3, t4)).setEphemeral(false).queue();
            }
        }
    }

    private double getOverallTier(int t1, int t2, int t3, int t4) {
        return (0.0 + min(t1, 1) + 2 * min(t2, 1) + 3 * min(t3, 1) + 4 * min(t4, 1)) / (0.0 + min(t1, 1) + min(t2, 1) + min(t3, 1) + min(t4, 1));
    }

    public int getDmxp(double time, double rating) {
        return (int) (time + time * 5 * rating / 10);
    }

    public String getRewards(Difficulty difficulty, double length, QuestType questType, int t1, int t2, int t3, int t4) {

        // calculate rewards
        double realTime = switch (questType) {
            case VOICE_CHAT -> length;
            case VOICE_CHAT_TEXT -> length * 60 / 65;
            case PLAY_BY_POST -> length * 60 / 70;
        };
        int[] maxBracket = {28, 25, 20, 12, 6};
        int[] minBracket = {26, 21, 15, 12, 6};

        int goldMax = (int) (maxBracket[difficulty.getValue()] * realTime * getOverallTier(t1, t2, t3, t4));
        int goldMin = (int) (minBracket[difficulty.getValue()] * realTime * getOverallTier(t1, t2, t3, t4));
        int stamps = (int) Math.round(realTime * 1.5);
        int dt = (int) Math.round(realTime * 1.25);
        // begin formatting

        // format response
        String type = switch (questType) {
            case VOICE_CHAT -> "vc";
            case VOICE_CHAT_TEXT -> "vct";
            case PLAY_BY_POST -> "pbp";
        };

        String side = "side ";
        if (length >= 2.0) {
            side = "";
        }
        String output = "Your quest rewards for the tier " + getOverallTier(t1, t2, t3, t4) + ", " + length + " hour, " + difficulty.getName()
                + " " + type + " "
                + side + "quest:\nStamps: " + stamps + "\nGold: " + randomGold(goldMin, goldMax) + "\nDT: "
                + Math.round(dt) + "\n";

        if (realTime >= 2.0) {
            if (t1 > 0) {
                output += "\nTier 1 item rewards:\n" + Items.getBasicItems(1, difficulty, t1);
            }
            if (t2 > 0) {
                output += "\nTier 2 item rewards:\n" + Items.getBasicItems(2, difficulty, t2);
            }
            if (t3 > 0) {
                output += "\nTier 3 item rewards:\n" + Items.getBasicItems(3, difficulty, t3);
            }
            if (t4 > 0) {
                output += "\nTier 4 item rewards:\n" + Items.getBasicItems(4, difficulty, t4);
            }
        }


        return output;
    }

    private int randomGold(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
