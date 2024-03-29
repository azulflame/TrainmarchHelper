package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.service.Difficulty;
import me.azulflame.trainmarch.dmhelper.service.Items;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ItemCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("items")) {
            if (!event.getChannel().getId().equals("931707960491671552") && !event.getChannel().getId().equals("1015883484545433685")) {
                event.reply("This must be run in the staff-bots channel").setEphemeral(true).queue();
            } else {
                int players = event.getOption("players").getAsInt();
                int tier = event.getOption("tier").getAsInt();
                Difficulty difficulty = Difficulty.getClosestDifficulty(event.getOption("difficulty").getAsString());
                event.reply(Items.getMinimumItems(tier, players, difficulty)).setEphemeral(false).queue();
            }
        }
    }
}
