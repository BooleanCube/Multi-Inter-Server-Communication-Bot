package bots;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class Commands extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        String msg = event.getMessage().getContentRaw();
        if(msg.equalsIgnoreCase(Constants.PREFIX + "help")) {
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                    .setTitle("Help Command")
                    .setDescription("This bot was a tutorial bot created by BooleanCube to showcase and explain how inter server communication can be done!\nDiscord Server: https://discord.gg/Mut53cedg9\nYoutube Channel: https://www.youtube.com/channel/UCsivrachJyFVLi7V60lrd6g")
                    .addField("?help", "Shows you some info about the bot and it's commands!", false)
                    .addField("?connect", "This connects the channel to a a multiple server guild where you can communicate with server out of your own server!", false)
                    .addField("?disconnect", "This disconnects your server from the multi-server guild that it was previously connected to!", false)
                    .addField("?randomserver", "Gives you the invite link to a random server that is currently connected to the multi-server guild!", false)
                    .build()
            ).queue();
        } else if(msg.equalsIgnoreCase(Constants.PREFIX + "randomserver")) {
            if(InterServerCommunication.connectedServers.size()==0) {
                event.getChannel().sendMessage(new EmbedBuilder().setDescription("There are currently no servers connected to the multi-server guild!").build()).queue();
                return;
            }
            int rand = (int)Math.random()*InterServerCommunication.connectedServers.size();
            TextChannel channel = null;
            while(true) {
                channel = InterServerCommunication.connectedServers.get(rand);
                if(!event.getGuild().getTextChannels().contains(channel) || InterServerCommunication.connectedServers.size()==1) break;
            }
            Guild g = channel.getGuild();
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setAuthor(g.getName(), g.getIconUrl(), g.getIconUrl())
                            .setDescription("[Server Invite](" + channel.createInvite().complete().getUrl() + ")")
                            .addField("Server Info:", "Members: " + g.getMembers().size() + "\nRegion: " + g.getRegionRaw() + "\nOwner: " + g.getOwner().getUser().getAsTag(), false)
                    .build()
            ).queue();
        }
    }
}
