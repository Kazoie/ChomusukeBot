package Chomusuke.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();

            if(command.equals("hd2")) {
                try {
                    System.out.println("HellDiversWarStatus asked");
                    URL url = new URL("https://helldiverstrainingmanual.com/api/v1/war/campaign");
                    HttpsURLConnection req = (HttpsURLConnection) url.openConnection();
                    req.setRequestMethod("GET");

                    if (req.getResponseCode() == 200){
                        InputStream inputStream = req.getInputStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                        String input;
                        StringBuffer response = new StringBuffer();
                        while ((input = in.readLine()) !=null){
                            response.append(input);
                        }
                        in.close();

                        String info = response.toString();
                        JsonArray res = JsonParser.parseString(info).getAsJsonArray();
                        JsonObject[] allPlanet = new JsonObject[res.size()];
                        for (int i = 0; i < res.size();i++){
                            JsonObject planet = res.get(i).getAsJsonObject();
                            allPlanet[i] = planet;
                        }

                        String begintext = "# :rotating_light: **Actual Galactic War Status** :rotating_light: \n";
                        String planetinfo = "";
                        for (JsonObject jsonObject : allPlanet) {
                            planetinfo += (jsonObject.get("name")
                                    + "is controlled by the"
                                    + jsonObject.get("faction")
                                    + "liberation still in progress, actual percentage : "
                                    + jsonObject.get("percentage")
                                    + "\n \n"
                            );
                        }
                        planetinfo = planetinfo.replaceAll("\""," ");

                        String majorOrder = "**HIGH PRIORITY BROADCAST : MAJOR ORDER COMPLETION** \n";
                        String focusOrder = "";
                        for(JsonObject jsonObject : allPlanet){
                            if (jsonObject.get("majorOrder").getAsBoolean() || jsonObject.get("defense").getAsBoolean()){
                                focusOrder += (jsonObject.get("name")
                                    + "is part of the major order, FOCUS IT HELLDIVER ! \n"
                                );
                            }
                        }
                        focusOrder = focusOrder.replaceAll("\""," ");

                        event.reply(begintext + planetinfo + majorOrder + focusOrder).queue();                    }


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }

    //Guild command
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event){
        List<CommandData> commandDataList = new ArrayList<>();
        commandDataList.add(Commands.slash("hd2","Provides the current status of all planets along with their player count"));
        event.getGuild().updateCommands().addCommands(commandDataList).queue();
    }

    //Global command
//    @Override
//    public void onReady(@NotNull ReadyEvent event){
//        List<CommandData> commandDataList = new ArrayList<>();
//        commandDataList.add(Commands.slash("helldiverswarstatus","Provides the current status of all planets along with their player count"));
//        event.getJDA().updateCommands().addCommands(commandDataList).queue();
//    }
}
