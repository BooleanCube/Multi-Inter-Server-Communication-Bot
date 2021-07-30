package bots;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;

public class InterServerCommunication extends ListenerAdapter {

    public static ArrayList<TextChannel> connectedServers = new ArrayList<>();
    public int maxServerConnections = 100;

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        String msg = event.getMessage().getContentRaw();
        if(msg.equalsIgnoreCase(Constants.PREFIX + "connect")) {
            for(TextChannel c : event.getGuild().getTextChannels()) {
                if(connectedServers.contains(c)) {
                    event.getChannel().sendMessage(
                            new EmbedBuilder()
                            .setTitle("Failure!")
                            .setColor(Color.RED)
                            .setDescription("This action can't be done because one of your guild's channels are already connected.")
                            .build()
                    ).queue();
                    return;
                }
            }
            EmbedBuilder sendMsg = new EmbedBuilder()
                    .setAuthor(event.getChannel().getGuild().getName(), event.getGuild().getIconUrl(), event.getGuild().getIconUrl())
                    .setColor(Color.GREEN)
                    .setTitle(event.getGuild().getName() + " joined the guild!");
            connectedServers.add(event.getChannel());
            if(connectedServers.size() > maxServerConnections) connectedServers.remove(0);
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setTitle("Success!")
                            .setColor(Color.GREEN)
                            .setDescription("You were successfully added to the guild!")
                            .build()
            ).queue();
            for(TextChannel c : connectedServers) {
                if(!event.getGuild().getTextChannels().contains(c)) c.sendMessage(sendMsg.build()).queue();
            }
        }
        else if(msg.equalsIgnoreCase(Constants.PREFIX + "disconnect")) {
            if(!connectedServers.contains(event.getChannel())) event.getChannel().sendMessage(
                    new EmbedBuilder()
                    .setTitle("Failure!")
                    .setColor(Color.RED)
                    .setDescription("This action can't be done because you are not currently connected to the multi-server guild!")
                    .build()
            ).queue();
            else {
                EmbedBuilder sendMsg = new EmbedBuilder()
                        .setAuthor(event.getChannel().getGuild().getName(), event.getGuild().getIconUrl(), event.getGuild().getIconUrl())
                        .setColor(Color.RED)
                        .setTitle(event.getGuild().getName() + " left the guild!");
                connectedServers.remove(event.getChannel());
                event.getChannel().sendMessage(
                        new EmbedBuilder()
                                .setTitle("Success!")
                                .setColor(Color.GREEN)
                                .setDescription("You were successfully removed from the guild!")
                                .build()
                ).queue();
                for(TextChannel c : connectedServers) {
                    if(!event.getGuild().getTextChannels().contains(c)) c.sendMessage(sendMsg.build()).queue();
                }
            }

        }
        else if(connectedServers.contains(event.getChannel())) {
            if(msg.equalsIgnoreCase("?help") || msg.equalsIgnoreCase("?randomserver")) return;
            EmbedBuilder sendMsg = new EmbedBuilder()
                    .setAuthor(event.getMember().getEffectiveName(), event.getAuthor().getAvatarUrl(), event.getAuthor().getEffectiveAvatarUrl())
                    .setDescription(msg);
            for(TextChannel c : connectedServers) {
                if(!event.getGuild().getTextChannels().contains(c)) c.sendMessage(sendMsg.build()).queue();
            }
        }
    }
}
