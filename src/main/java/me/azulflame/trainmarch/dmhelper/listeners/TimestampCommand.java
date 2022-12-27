package me.azulflame.trainmarch.dmhelper.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class TimestampCommand extends ListenerAdapter {

    private static final Set<String> zones = ZoneId.getAvailableZoneIds();
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("timestamp"))
        {
            int year, month, day, hour, minute, second;
            String timezone = event.getOption("timezone").getAsString();

            try {
                ZonedDateTime.now(ZoneId.of(timezone)).getHour();
            }
            catch (Exception e)
            {
                event.reply("Unable to find the specified timezone.").setEphemeral(true).queue();
                return;
            }


            if (event.getOption("year") == null)
                year = ZonedDateTime.now(ZoneId.of(timezone)).getYear();
            else
                year = event.getOption("year").getAsInt();

            if (event.getOption("month") == null)
                month = ZonedDateTime.now(ZoneId.of(timezone)).getMonthValue();
            else
                month = event.getOption("month").getAsInt();

            if (event.getOption("day") == null)
                day = ZonedDateTime.now(ZoneId.of(timezone)).getDayOfMonth();
            else
                day = event.getOption("day").getAsInt();

            if (event.getOption("hour") == null)
                hour = ZonedDateTime.now(ZoneId.of(timezone)).getHour();
            else
                hour = event.getOption("hour").getAsInt();

            if (event.getOption("minute") == null)
                minute = 0;
            else
                minute = event.getOption("minute").getAsInt();

            if (event.getOption("second") == null)
                second = 0;
            else
                second = event.getOption("second").getAsInt();

            if (event.getOption("is-pm") != null && event.getOption("is-pm").getAsBoolean())
            {
                hour += 12;
            }

            String time = String.valueOf(ZonedDateTime.of(year, month, day, hour, minute, second, 0, ZoneId.of(timezone)).toEpochSecond());

            event.reply(
                    String.format("Your timestamps:\n" +
                            "`<t:%s:d>`\t<t:%s:d>\n" +
                            "`<t:%s:D>`\t<t:%s:D>\n" +
                            "`<t:%s:t>`\t<t:%s:t>\n" +
                            "`<t:%s:T>`\t<t:%s:T>\n" +
                            "`<t:%s:f>`\t<t:%s:f>\n" +
                            "`<t:%s:F>`\t<t:%s:F>\n" +
                            "`<t:%s:R>`\t<t:%s:R>\n" +
                            "`<t:%s>`\t\t<t:%s>\n", time,time,time,time,time,time,time,time,time,time,time,time,time,time,time,time)).setEphemeral(true).queue();
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("timestamp") && event.getFocusedOption().getName().equals("timezone")) {
            Set<Command.Choice> ids = zones.stream().filter(x -> x.toLowerCase().contains(event.getFocusedOption().getValue().toLowerCase())).map(x -> new Command.Choice(x, x)).limit(25).collect(Collectors.toSet());
            event.replyChoices(ids).queue();
        }
    }
}