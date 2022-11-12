package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.service.Difficulty;
import me.azulflame.trainmarch.dmhelper.service.Scrolls;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ScrollsCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("scrolls")) {
            if (!event.getChannel().getId().equals("931707960491671552") && !event.getChannel().getId().equals("1015883484545433685")) {
                event.reply("This must be run in the staff-bots channel").setEphemeral(true).queue();
            } else {
                int players = event.getOption("players").getAsInt();
                int tier = event.getOption("tier").getAsInt();
                Difficulty difficulty = Difficulty.getClosestDifficulty(event.getOption("difficulty").getAsString());
                event.reply(Scrolls.getMinimumItems(players, tier, difficulty)).setEphemeral(false).queue();
            }
        }
    }
}
