package Chomusuke.commands;

import Chomusuke.functionalities.Team;
import Chomusuke.functionalities.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();

            if(command.equals("hd2")) {
                try {
                    System.out.println("HellDiversWarStatus asked by "+event.getUser().getName());
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
                            //EXAMPLE OF ONE JsonObject
//                        {
//                            "planetIndex": 169,
//                                "name": "Estanu",
//                                "faction": "Terminids",
//                                "players": 247207,
//                                "health": 66933,
//                                "maxHealth": 1000000,
//                                "percentage": 93.3067,
//                                "defense": false,
//                                "majorOrder": false,
//                                "biome": {
//                            "slug": "icemoss",
//                                    "description": "Ice and moss-covered rock can be found across most of the surface of this planet."
//                        }


                        String begintext ="# :rotating_light: **Actual Galactic War Status** :rotating_light: \n";
                        String planetinfo = "```";
                        for (JsonObject jsonObject : allPlanet) {
                            planetinfo += (jsonObject.get("name")
                                    + " is controlled by the "
                                    + jsonObject.get("faction")
                                    + ", liberation still in progress, actual percentage : "
                                    + jsonObject.get("percentage")
                                    + "\n"
                            );
                        }
                        planetinfo = planetinfo.replaceAll("\"","");


                        event.reply(begintext + planetinfo + " ```").queue();                    }


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
            else if (command.equals("teammaker")) {
                System.out.println("Teammaker asked by " +event.getUser().getName());
                OptionMapping option = event.getOption("players");
                String result = "";

                if (option != null) {
                    String bunch = option.getAsString();
                    String[] users = bunch.split(";");
                    ArrayList<User> userList = new ArrayList<>();

                    for (String str : users) {
                        User user = new User(str);
                        userList.add(user);
                    }

                    option = event.getOption("teamlength"); //number of player per team
                    if (option != null) {
                        int teamlength = option.getAsInt();
                        if (teamlength <= 0) {
                            event.reply("Il est impossible d'avoir une taille d'équipe négative").queue();
                        } else {
                            Random r = new Random();

                            int numberOfTeam = userList.size() / teamlength; // total number of teams
                            if (userList.size() % teamlength != 0) {
                                event.reply("nombre de joueur incorrect").queue();
                            } else {

                                Team[] teamList = new Team[numberOfTeam];
                                for (int i = 0; i < numberOfTeam; i++) {

                                    Team team = new Team(teamlength); // i create a team

                                    for (int x = 0; x < team.getTeamLength(); x++) {
                                        int pickedIndex = r.nextInt(userList.size()); //i pick a random user in my userlist
                                        team.addTeamMember(userList.get(pickedIndex)); //i add it in my team
                                        userList.remove(userList.get(pickedIndex));// i remove the user picked from userlist
                                        teamList[i] = team; // i put my team in teamlist
                                    }
                                }
                                for (int i = 0; i < teamList.length; i++) {
                                    ArrayList<User> resultUser = teamList[i].getTeamcomp();
                                    result += "Team " + i + " : ";
                                    for (User user : resultUser) {
                                        result += user.getUsername() + "/";
                                    }
                                    result += " \n";
                                }
                                event.reply(result).queue();
                            }

                        }
                    }
                }
            } else if (command.equals("deathroll")) {
                System.out.println("deathroll asked by " + event.getUser().getName());
                OptionMapping option = event.getOption("roll");
                int bound = option.getAsInt();
                if (option != null) {
                    if (bound <= 0) {
                        event.reply("Votre borne ne peut pas être négative");
                    } else {
                        Random r = new Random();
                        int result = r.nextInt(1,bound+1);
                        if (result == 1 ){
                            event.reply("Resultat du roll : "+result + " défaite !").queue();
                        } else {
                            event.reply("Resultat du roll : " + result).queue();
                        }
                    }
                }
            }

    }

    //Guild command
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event){
        List<CommandData> commandDataList = new ArrayList<>();

        commandDataList.add(Commands.slash("hd2","Provides the current status of all planets along with their player count"));

        OptionData option1 = new OptionData(OptionType.STRING,"players","The bunch of user, separated by semicolon like this : player1;player2;... ",true);
        OptionData option2 = new OptionData(OptionType.INTEGER,"teamlength","The numbers of players in each team",true);
        commandDataList.add(Commands.slash("teammaker","create a team from a bunch of user").addOptions(option1,option2));

        OptionData option3 = new OptionData(OptionType.INTEGER,"roll","the maximum boundary of your roll",true);
        commandDataList.add(Commands.slash("deathroll","Run a deathroll from bound to 0").addOptions(option3));
        event.getGuild().updateCommands().addCommands(commandDataList).queue();
    }

    //Global command
//    @Override
//    public void onReady(@NotNull ReadyEvent event){
//        List<CommandData> commandDataList = new ArrayList<>();
//        event.getJDA().updateCommands().addCommands(commandDataList).queue();
//    }

}
