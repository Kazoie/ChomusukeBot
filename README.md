# CHOMUSUKE BOT

## Summary
Chomusuke bot is my new personnal project made in java to offer a functionnal bot that can track game data from the famouse game "HellDivers2", it provide commands to retrieve data from the game at a time T and to show them simply.
but he can also connect to a DataBase made with PostgreSQL and running on a docker to show chart about the game in discord.

## Information
This bot is basically a new version of my old first java bot that we can found on my gitHub page, upgraded with JDA 5 and also working with a dataBase made with pgSQL on a docker container, the bot have following commands :
`/hd2` command to show the actual war status in the game and also gather data to the dataBase
`/hd2graph (planetName)` show a line chart of the evolving liberation of a specified planet by gathering registered data from the game
`/teammaker (players) (teamlength)` is used to create random team with a bunch of user for some online games, the parameters of the commands are well explained in discord
`/deathroll (roll)` is used to create a deathroll (a sort of bet) well know in the game "World of Warcraft"

## How to use ?
Well, for now i tried to make it easily usable by other people but idk if its really working... Anyway you have a .env.example to show you how to configure the bot for yourself (Token,DBURL,DBPASSWORD,DBUSERNAME) but do not forget
that DBURL is configured to connect to a postgres database and the used libraries are configured to it so i don't think you can use another DB for now.

##  Special Thanks
A special Thanks to @Xemuth and @diviperr for helping me making this bot working fine and providing me some good advice during develoment

