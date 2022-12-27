package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.dto.PlayerCharacter;
import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CharacterCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("character")) {
            if (event.getSubcommandName().equals("register")) {
                String user = event.getUser().getId();
                String blob = event.getOption("blob").getAsString();
                PlayerCharacter pc = PlayerCharacter.from(blob, user);
                String message = "That character already exists, please update with `/character update` instead";
                if (DatabaseManager.registerCharacter(pc))
                {
                    message = pc.toString();
                }
                event.reply(message).queue();
            }
            else if (event.getSubcommandName().equals("update"))
            {
                String user = event.getUser().getId();
                String blob = event.getOption("blob").getAsString();
                PlayerCharacter pc = PlayerCharacter.from(blob, user);
                event.reply(pc.toString()).queue();
            }
            else if (event.getSubcommandName().equals("view-link"))
            {
                String user = event.getUser().getId();
                List<String> names = DatabaseManager.getCharacters(user);
                String message = "Which character do you want from " + event.getUser().getAsMention() + "?";
                SelectMenu menu = SelectMenu.create("character-names").addOptions(names.stream().map(x -> {
                    return SelectOption.of(x, user + "|" + x);
                }).collect(Collectors.toList())).build();
                event.reply(message).addActionRow(menu).queue();
            }
        }
    }
    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
        if (event.getComponentId().equals("character-names"))
        {
            String message = "Unable to find a character by that name and owner.";
            String[] split = event.getValues().get(0).split("\\|");
            String userId = split[0];
            String[] name = new String[split.length-1];
            for (int i = 1; i < split.length; i++)
                name[i-1] = split[i];
            PlayerCharacter pc = DatabaseManager.getCharacter(userId, String.join("|", name));
            if (pc != null)
            {
                message = pc.getLink();
            }
            MessageEditData meda = new MessageEditBuilder().setComponents(new LinkedList<>()).setContent(message).build();
            event.editMessage(meda).queue();
        }
    }
}
