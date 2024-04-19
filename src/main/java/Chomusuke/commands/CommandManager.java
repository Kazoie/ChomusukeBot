package Chomusuke.commands;

import Chomusuke.functionalities.Team;
import Chomusuke.functionalities.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.List;

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
                        Connection conn = connectToDB();
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

                        //INSERT DATE
                        for (JsonObject jsonObject : allPlanet){

                            int index = jsonObject.get("planetIndex").getAsInt();
                            int players = jsonObject.get("players").getAsInt();
                            float percentage = jsonObject.get("percentage").getAsFloat();

                            PreparedStatement st = conn.prepareStatement("INSERT INTO ensemble_planetes (id,nb_joueur,pourcentageliberation) VALUES (?,?,?)");
                            st.setObject(1,index);
                            st.setObject(2,players);
                            st.setObject(3,percentage);
                            st.executeUpdate();
                            st.close();
                        }



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
                } catch (SQLException e) {
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
                            event.reply("Roll initial : "+ bound +", Resultat du roll : "+result + " défaite !").queue();
                        } else {
                            event.reply("Roll initial : "+ bound +", Resultat du roll : " + result).queue();
                        }
                    }
                }
            } else if (command.equals("hd2graph")){
                System.out.println("hd2graph asked by " + event.getUser().getName());
                HashMap<Timestamp,Float> evolution = new HashMap<>();
                OptionMapping option = event.getOption("planetname");
                String planetName = option.getAsString();
                if (option != null){
                    try {

                    //RETRIEVE PLANET'S ID BY NAME
                    Connection conn = connectToDB();
                    PreparedStatement st = null;
                    st = conn.prepareStatement("SELECT (id) FROM planete WHERE nomplanete LIKE ?");
                    st.setObject(1,"%"+ planetName +"%");
                    ResultSet rs = st.executeQuery();

                    PreparedStatement st2 = null;
                    st2 = conn.prepareStatement("SELECT (pourcentageliberation),(date) FROM ensemble_planetes WHERE id = ?");
                    int id = 0;
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                    st2.setObject(1,id);
                    ResultSet rs2 = st2.executeQuery();
                    while (rs2.next()){
                        evolution.put(rs2.getTimestamp(2),rs2.getFloat(1));
                    }
                    rs.close();
                    rs2.close();
                    st.close();
                    st2.close();

                        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
                        for (Map.Entry m : evolution.entrySet()){
                            Float percentage = (Float) m.getValue();
                            Timestamp ts = (Timestamp) m.getKey();
                         line_chart_dataset.addValue(percentage,"Date",ts);
                        }

                        JFreeChart lineChartObject = ChartFactory.createLineChart(
                                "Evolve of Liberation percentage", "time",
                                "Percentage",
                                line_chart_dataset, PlotOrientation.VERTICAL,
                                true, true, false);

                        int width = 640;
                        int height = 480;
                        File lineChart = new File("LineChart.jpeg");
                        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);

                        FileUpload fileUpload = FileUpload.fromData(lineChart);
                        event.reply("Result").addFiles(fileUpload).queue();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
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

        OptionData option4 = new OptionData(OptionType.STRING,"planetname","The planet's name you want to see information",true);
        commandDataList.add(Commands.slash("hd2graph","Display a graph for a chosen planet").addOptions(option4));

        event.getGuild().updateCommands().addCommands(commandDataList).queue();
    }

    //Global command
//    @Override
//    public void onReady(@NotNull ReadyEvent event){
//        List<CommandData> commandDataList = new ArrayList<>();
//        event.getJDA().updateCommands().addCommands(commandDataList).queue();
//    }

    public Connection connectToDB() {
        Dotenv config = Dotenv.configure().load(); //load my config
        String dbURL = config.get("DBURL");
        String username = config.get("DBUSERNAME");
        String password = config.get("DBPASSWORD");
        try {
            //connection to DB
            String urlpg = dbURL;
            Properties props = new Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            Connection conn = DriverManager.getConnection(urlpg, props);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
