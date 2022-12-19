package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.service.Difficulty;
import me.azulflame.trainmarch.dmhelper.service.Items;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static java.lang.Math.min;

public class NewRewardsCommand extends ListenerAdapter {
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("rewards")) {
            if (!event.getChannel().getId().equals("931707960491671552") && !event.getChannel().getId().equals("1015883484545433685")) {
                event.reply("This must be run in the staff-bots channel").setEphemeral(true).queue();
            } else {
                int t1 = (int)event.getOption("t1").getAsDouble();
                int t2 = (int)event.getOption("t2").getAsDouble();
                int t3 = (int)event.getOption("t3").getAsDouble();
                int t4 = (int)event.getOption("t4").getAsDouble();
                Double length = event.getOption("time").getAsDouble();
                // Double rating = event.getOption("rating").getAsDouble();
                Difficulty difficulty = Difficulty.getClosestDifficulty(event.getOption("difficulty").getAsString());
                boolean vc = event.getOption("vc").getAsBoolean();
                event.reply(getRewards(difficulty, length, vc, t1, t2, t3, t4)).setEphemeral(false).queue();
            }
        }
    }

    private double getOverallTier(int t1, int t2, int t3, int t4)
    {
        return (0.0 + min(t1, 1) + 2*min(t2, 1) + 3*min(t3, 1) + 4*min(t4, 1))/(0.0 + min(t1, 1) + min(t2, 1) + min(t3, 1) + min(t4, 1));
    }
    public int getDmxp(double time, double rating)
    {
        return (int) (time + time * 5 * rating / 10);
    }

    public String getRewards(Difficulty difficulty, double length, boolean isVc, int t1, int t2, int t3, int t4) {
        double tier = getOverallTier(t1, t2, t3, t4);
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
        String output = "Your quest rewards for the tier " + tier + ", " + length + " hour, " + difficulty.getName()
                + " " + type + " "
                + side + "quest" + "\nStamps: " + stamps + "\nGold: " + randomGold(goldMin, goldMax) + "\nDT: "
                + Math.round(dt)
                + "\n";
        if (t1 > 0)
        {
            output += "\nTier 1 item rewards:\n" + Items.getBasicItems(1, difficulty, t1);
        }
        if (t2 > 0)
        {
            output += "\nTier 2 item rewards:\n" + Items.getBasicItems(2, difficulty, t2);
        }
        if (t3 > 0)
        {
            output += "\nTier 3 item rewards:\n" + Items.getBasicItems(3, difficulty, t3);
        }
        if (t4 > 0)
        {
            output += "\nTier 4 item rewards:\n" + Items.getBasicItems(4, difficulty, t4);
        }


        return output;
    }

    private int randomGold(int min, int max)
    {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
