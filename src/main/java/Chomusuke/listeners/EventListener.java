package Chomusuke.listeners;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        IMentionable IMentionable = User.fromId("1226243315372916837");
        MessageChannelUnion channel = event.getChannel();
        User user = event.getAuthor();
        if (!user.isBot()) {
            Message message = event.getMessage();
            if (message.getContentRaw().contains("anime")) {
                String response = "Oh oui des animes !";
                channel.asTextChannel().sendMessage(response).queue();
                response = "https://tenor.com/bYe6i.gif";
                channel.asTextChannel().sendMessage(response).queue();
            } else if (message.getMentions().isMentioned(IMentionable)) {
                String response = "Me parle pas sale rat";
                channel.asTextChannel().sendMessage(response).queue();
            } else if (message.getContentRaw().contains("but")) {
                String response = "Mon but ici est de remplacer Rimuru qui est tombé dans l'alcoolisme et la dépréssion d'être un vieux bot";
                channel.asTextChannel().sendMessage(response).queue();
            }
            }
        }

    }
