package me.azulflame.trainmarch.dmhelper.listeners;

import me.azulflame.trainmarch.dmhelper.repository.DatabaseManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import javax.xml.crypto.Data;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public class HousingCommand extends ListenerAdapter {

    private static final List<String> types = List.of("campsite","cabin","farmhouse","apartment","bungalow","stilt house","treehouse","tower","cottage","boat house","house","lighthouse","mansion","manor","castle");
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("create-housing")) {
            Member member = event.getOption("player").getAsMember();
            String character = event.getOption("character").getAsString();
            String houseType = event.getOption("housing-type").getAsString();

            try {
                createTextChannel(member, character, houseType);
            } catch (Exception e) {
                e.printStackTrace();
                event.reply("There was an issue creating the housing channel. Contact Azulflame for more information.").setEphemeral(true).queue();
                return;
            }

            event.reply("Housing channel created\nPlayer: " + member.getAsMention() + "\nCharacter: " + character + "\nHousing Type: " + houseType + "\n\nHousing channel created by " + event.getUser().getAsMention()).queue();
        }
        else if(event.getName().equals("register-housing")) {
            Member member = event.getOption("new-owner").getAsMember();
            event.getInteraction().getGuildChannel().getPermissionContainer().upsertPermissionOverride(member).grant(Permission.MANAGE_PERMISSIONS, Permission.VIEW_CHANNEL, Permission.MANAGE_CHANNEL, Permission.MESSAGE_MANAGE).queue();
            DatabaseManager.setOwner(member.getId().toString(), event.getChannel().getId().toString());
            event.reply(event.getUser().getAsMention() + " has set the owner of the channel to " + member.getAsMention()).queue();
        }
        else if(event.getName().equals("housing"))
        {
            if ("allow".equals(event.getSubcommandName()))
            {
                Member member = event.getOption("player").getAsMember();
                if (DatabaseManager.getOwner(event.getChannel().getId()).equals(event.getUser().getId().toString()) || event.getMember().hasPermission(Permission.ADMINISTRATOR))
                {
                    event.getInteraction().getGuildChannel().getPermissionContainer().upsertPermissionOverride(member).grant(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL).queue();
                    event.reply(event.getMember().getAsMention() + " has permitted " + member.getAsMention() + " in this channel.").queue();
                }
                else
                {
                    event.reply("Unable to set permission override, you are not the registered owner").setEphemeral(true).queue();
                    return;
                }
            }
            else if ("restrict".equals(event.getSubcommandName()))
            {
                Member member = event.getOption("player").getAsMember();
                if (DatabaseManager.getOwner(event.getChannel().getId()).equals(event.getUser().getId().toString()) || event.getMember().hasPermission(Permission.ADMINISTRATOR))
                {
                    event.getInteraction().getGuildChannel().getPermissionContainer().upsertPermissionOverride(member).deny(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL).queue();
                    event.reply(event.getMember().getAsMention() + " has restricted " + member.getAsMention() + " in this channel.").queue();
                }
                else
                {
                    event.reply("Unable to set permission override, you are not the registered owner").setEphemeral(true).queue();
                    return;
                }
            }
        }
    }


    private static void createTextChannel(Member member, String characterName, String houseType)
    {
        String title = (characterName + "-" + houseType).replaceAll(" ", "-");
        Guild guild = member.getGuild();
        Category category = guild.getCategoryById("855597286801145886");
        if (category == null)
        {
            category = guild.getCategoryById("1036408573476999208");
        }
        //staff
        Role role = guild.getRoleById("949110181365710879");
        if (role == null)
        {
            role = guild.getRoleById("1036414043965100122");
        }
        // trial staff
        Role trialStaff = guild.getRoleById("863505906125373471");
        if (trialStaff == null)
        {
            trialStaff = guild.getRoleById("1080562697613082674");
        }


        Consumer<TextChannel> channelConsumer = (channel) -> {
            Consumer<Message> messageConsumer = (message) -> message.pin().queue();
            channel.sendMessage("Housing channel automatically created!\nOwned by: " + characterName + "\nPlayer: " + member.getAsMention() + "\nHouse type: " + houseType).queue(messageConsumer);
            DatabaseManager.setOwner(member.getId(), channel.getId());
        };
        guild.createTextChannel(title, category)
                .addPermissionOverride(member, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL), null)
                .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .addPermissionOverride(role, EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(trialStaff, EnumSet.of(Permission.VIEW_CHANNEL), null)
                .setTopic("A " + houseType + " for " + characterName)
                .queue(channelConsumer);
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("create-housing") && event.getFocusedOption().getName().equals("housing-type"))
        {
            List<Command.Choice> options = types.stream()
                    .filter(word -> word.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase()))
                    .map(word -> new Command.Choice(word, word))
                    .limit(25)
                    .toList();
            event.replyChoices(options).queue();
        }
    }
}
