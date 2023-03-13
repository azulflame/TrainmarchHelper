package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.dto.PlayerCharacter;
import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StampCommand extends ListenerAdapter {

    final int[] stampThreshold = {0, 3, 7, 13, 20, 29, 39, 49, 60, 74, 90, 107, 124, 143, 162, 183, 206, 229, 255, 285};

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("stamps"))
        {
            if (event.getSubcommandName().equals("add"))
            {
                String name = event.getOption("character").getAsString();
                int amount = event.getOption("amount").getAsInt();
                if (amount < 0)
                {
                    event.reply("Unable to add negative stamps. Please use a positive amount.").setEphemeral(true).queue();
                    return;
                }
                PlayerCharacter pc = DatabaseManager.getCharacter(event.getUser().getId(), name);
                if (pc == null) {
                    event.reply("Unable to find a PC by that name.").setEphemeral(true).queue();
                    return;
                }
                int current = pc.getStamps();
                if (current < 0)
                {
                    event.reply("Unable to find a character by that name. Did you type it correctly?").setEphemeral(true).queue();
                    return;
                }
                event.deferReply().queue();
                pc.setStamps(pc.getStamps() + amount);
                DatabaseManager.saveCharacter(pc);
                int level = pc.getLevel();
                event.reply(getFormattedStamps(name, current, amount, level)).queue();
            }
            else if (event.getSubcommandName().equals("remove"))
            {
                String name = event.getOption("character").getAsString();
                int amount = event.getOption("amount").getAsInt();

                if (amount < 0)
                {
                    event.reply("Unable to remove negative stamps. Please use a positive amount.").setEphemeral(true).queue();
                    return;
                }
                PlayerCharacter pc = DatabaseManager.getCharacter(event.getUser().getId(), name);
                if (pc == null) {
                    event.reply("Unable to find a PC by that name.").setEphemeral(true).queue();
                    return;
                }

                int current = pc.getStamps();
                if (current < 0)
                {
                    event.reply("Unable to find a character by that name. Did you type it correctly?").setEphemeral(true).queue();
                    return;
                }
                event.deferReply().queue();

                pc.setStamps(pc.getStamps()-amount);
                DatabaseManager.saveCharacter(pc);
                int level = pc.getLevel();
                event.reply(getFormattedStamps(name, current, amount, level)).queue();
            }
            else if(event.getSubcommandName().equals("check"))
            {
                String name = event.getOption("character").getAsString();
                PlayerCharacter pc = DatabaseManager.getCharacter(event.getUser().getId(), name);
                if (pc == null) {
                    event.reply("Unable to find a PC by that name.").setEphemeral(true).queue();
                    return;
                }
                int stamps = pc.getStamps();
                int level = pc.getLevel();
                event.reply(getFormattedStamps(name, stamps, 0, level)).setEphemeral(true).queue();
            }
        }
    }

    private String getFormattedStamps(String name, int old, int change, int currLevel)
    {
        String toReturn = name + "'s stamps:\n" + (old+change) + " " + getAppend(change);
        toReturn += "\nWhich makes " + name + " a level " + getLevel(old + change) + "character.";
        if (getLevel(old + change) < currLevel)
        {
            toReturn += "\n\nPlease update " + name + " by updating their sheet, using `!update`, `!export`, and then `/character update`";
        }
        return toReturn;
    }

    private String getAppend(int change)
    {
        if (change > 0)
        {
            return "(+" + change + ")";
        }
        if (change < 0)
        {
            return "(" + change + ")";
        }
        return "";
    }

    private int getLevel(int stamps)
    {
        int level = 0;
        for(int i = 0; i < stampThreshold.length; i++)
        {
            if (stamps <= stampThreshold[i])
            {
                return i+1;
            }
        }
        return 0;
    }
}
