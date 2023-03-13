package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.service.Difficulty;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RewardCommand extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("old-rewards")) {
            if (!event.getChannel().getId().equals("931707960491671552") && !event.getChannel().getId().equals("1015883484545433685")) {
                event.reply("This must be run in the <#931707960491671552> channel").setEphemeral(true).queue();
            } else {
                double tier = event.getOption("tier").getAsDouble();
                double length = event.getOption("time").getAsDouble();
                Difficulty difficulty = Difficulty.getClosestDifficulty(event.getOption("difficulty").getAsString());
                boolean vc = event.getOption("vc").getAsBoolean();
                event.reply(getRewards(difficulty, length, vc, tier)).setEphemeral(false).queue();
            }
        }
    }

    public String getRewards(Difficulty difficulty, double length, boolean isVc, double tier) {
        int goldMin = 10;
        int stamps = (int) Math.round(length);
        int goldMax = 10;
        double dt = Math.round(length / 2);

        // Lookup tables
        int[] vcGoldMax = {28, 25, 20, 12, 6};
        int[] vcGoldMin = {26, 21, 15, 12, 6};
        int[] txtGoldMax = {28, 25, 15, 0, -2};
        int[] txtGoldMin = {26, 20, 15, 0, -2};
        int[] vcStamps = {5, 4, 2, 1, 0};
        int[] txtStamps = {4, 3, 1, 0, -1};
        double[] dtMult = {2.5, 2, 1.5, 1, 0.75};

        // actual calculations
        if (isVc) {
            stamps += vcStamps[difficulty.getValue()];
            goldMin += vcGoldMin[difficulty.getValue()];
            goldMax += vcGoldMax[difficulty.getValue()];
        } else {
            stamps += txtStamps[difficulty.getValue()];
            goldMax += txtGoldMax[difficulty.getValue()];
            goldMin += txtGoldMin[difficulty.getValue()];
        }

        // Side quest check
        if (length < 2.0) {
            dt = 0;
            goldMax /= 2;
            goldMin /= 2;
            stamps /= 2;
        }

        // apply calculations
        dt *= dtMult[difficulty.getValue()];
        goldMax *= length * tier;
        goldMin *= length * tier;

        // begin formatting

        // format response
        String type = "vc";
        if (!isVc) {
            type = "pbp";
        }

        String side = "side ";
        if (length >= 2.0) {
            side = "";
        }
        return "Your quest rewards for the tier " + tier + ", " + length + " hour, " + difficulty.getName()
                + " " + type + " "
                + side + "quest:\nStamps: " + stamps + "\nGold: " + randomGold(goldMin, goldMax) + "\nDT: "
                + Math.round(dt);
    }

    private int randomGold(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
