package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.service.Dmxp;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DmxpCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("dmxp")) {
            if (event.getSubcommandName().equals("check")) {
                Member m = event.getOption("target").getAsMember();
                event.reply(m.getEffectiveName() + Dmxp.check(m.getId(), m.getEffectiveName())).setEphemeral(true).queue();
            } else if (event.getSubcommandName().equals("update")) {
                if (!event.getChannel().getId().equals("935004390438338560") && !event.getChannel().getId().equals("1015883484545433685")) {
                    event.reply("This command must be run in #staff-xp-log").setEphemeral(true).queue();
                }
                else {
                    String userId = event.getUser().getId();
                    int change = event.getOption("change").getAsInt();
                    String reason = event.getOption("reason").getAsString();
                    event.reply(Dmxp.change(userId, event.getUser().getAsMention(), change, reason)).setEphemeral(false).queue();
                }
            }
            else if(event.getSubcommandName().equals("give"))
            {
                if (!event.getChannel().getId().equals("935004390438338560") && !event.getChannel().getId().equals("1015883484545433685")) {
                    event.reply("This command must be run in #staff-xp-log").setEphemeral(true).queue();
                }
                else {
                    int change = event.getOption("amount").getAsInt();
                    String reason = event.getOption("reason").getAsString();
                    String userId = event.getUser().getId();
                    String targetUserId = event.getOption("recipient").getAsUser().getId();
                    String userName = event.getUser().getAsMention();
                    String targetUserName = event.getOption("recipient").getAsUser().getAsMention();

                    event.reply(Dmxp.give(userId, userName, targetUserId, targetUserName, change, reason)).setEphemeral(false).queue();
                }
            }
        }
    }
}
