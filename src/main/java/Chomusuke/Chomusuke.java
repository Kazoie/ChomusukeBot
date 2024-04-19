package Chomusuke;

import Chomusuke.commands.CommandManager;
import Chomusuke.listeners.EventListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import java.sql.*;

import javax.security.auth.login.LoginException;

public class Chomusuke {

    private final ShardManager shardManager;
    private final Dotenv config;

    public Chomusuke() throws LoginException {
        config = Dotenv.configure().load(); //load my config
        String token = config.get("TOKEN");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE); //show im online
        builder.setActivity(Activity.playing("HellDivers 2")); // what am i doing ?
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT,GatewayIntent.GUILD_MEMBERS);

        //builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        //builder.setChunkingFilter(ChunkingFilter.ALL);

        shardManager = builder.build();

        //List of Event we are listening to
        shardManager.addEventListener(new CommandManager());
    }

    public ShardManager getShardManager(){
        return shardManager;
    }

    public Dotenv getConfig(){
        return config;
    }

    public static void main(String[] args){
        try {
            Chomusuke chomusuke = new Chomusuke();
        } catch (LoginException e){
            System.out.println("INVALID TOKEN PROVIDED");
        }
    }
}
