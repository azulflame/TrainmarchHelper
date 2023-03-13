package me.azulflame.trainmarch.dmhelper.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ComputeDmxpCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("compute-dmxp"))
        {
            if (!(event.getChannel().getId().equals("931707960491671552") || event.getChannel().getId().equals("1015883484545433685")))
            {
                event.reply("Please use this command in the <#931707960491671552> channel").setEphemeral(true).queue();
                return;
            }
            Double rating = event.getOption("average-rating").getAsDouble();
            Double time = event.getOption("time").getAsDouble();

            String result = "Your " + time + " hour quest, rated at " + rating + "/10 will give you " + ratingToDmxp((int)Math.floor(rating), time) + " dmxp";

            event.reply(result).setEphemeral(false).queue();
        }
    }
    private int ratingToDmxp(int rating, double time)
    {
        return (int)Math.floor(rating + (Math.min(2, Math.max(0.5, 0.5 * time - 0.5)) * rating * time / 10));
    }
}
