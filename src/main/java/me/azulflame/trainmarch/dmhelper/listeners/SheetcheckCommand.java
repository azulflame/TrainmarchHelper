package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.dto.PlayerCharacter;
import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

public class SheetcheckCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("sheet")) {
            System.out.println(event.getCommandString());
            if (event.getSubcommandName().equals("approve")) {
                event.deferReply().queue();
                Member m = event.getOption("player").getAsMember();
                String character = event.getOption("character").getAsString();
                String sheet = event.getOption("sheet-link").getAsString();

                EmbedBuilder eb = new EmbedBuilder();

                eb.setTitle("Your character has been approved!");
                eb.setDescription("<@" + m.getId() + "> your character \""+character+"\" has been approved.");
                eb.setColor(Color.GREEN);
                eb.addField("Please run these commands in this channel", "`!import " + sheet + "`\n`!stamps 7`", false);
                eb.addField("Please run these commands in <#927791499398238238>", "`tul!register CHARACTER_NAME PREFIX:text`\n`tul!avatar CHARACTER_NAME LINK_TO_ART`", false);
                eb.addField("Next Steps", "After you finish the commands, head over to <#821854558690869269> to give yourself some roles, roleplay over in <#821932488191639553> or another channel, or watch for quests in <#822255986302517258>.", false);
                eb.addField("New Character?", "If this is your first character, you can add the bonuses from any Mee6 levels to this character. Future levels can only be applied to one character per level.", false);

                Role r = event.getGuild().getRoleById(929907664589295616L);
                if (r == null) {
                    r = event.getGuild().getRoleById(1095065058591113378L);
                }
                if (null != r) {
                    event.getMember().getGuild().addRoleToMember(m, r).queue();
                }
                DatabaseManager.registerCharacter(m.getId(), character, sheet);
                event.getHook().sendMessageEmbeds(eb.build()).queue();
            }
            else if (event.getSubcommandName().equals("find")) {
                event.deferReply(true).queue();
                String character = event.getOption("character").getAsString();

                String output = "Characters that match the name \"" + character + "\":\n";
                List<PlayerCharacter> chars = DatabaseManager.getCharacters(character);
                for (PlayerCharacter pc : chars) {
                    output += pc.toString() + "\n";
                }
                event.getHook().sendMessage(output).queue();
            }
        }
    }
}
