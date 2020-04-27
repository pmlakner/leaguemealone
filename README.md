# LeagueMeAlone
Appear offline to other players on league of legends (MAC OS). The riot client does not allow you to play without your
friends seeing that you're online. You have no option to appear offline friends. This application runs a proxy server
between the riot client and config/chat servers that alters HTTP packets to deceive online status. 

# Disclaimer
This is project is personal excercise and is not intended to be used by others. It aims to function at the level of a 
production application but only to be used by someone who understands the implementation and the outcomes of running it. 
For that reason I do not provide install instructions/compatability requirements. That being said any experienced programmer 
is free to perform their own setup or reuse the code however they please.

# Project Status
* [x] Riot game client launches with config server
* [x] Intercept first GET request from game client
* [x] Relay request to config server
* [x] Modify packets intended for config server
* [x] Modify response intended for app
* [ ] Collect chat server name and port
* [ ] Provide game client proxy chat server connection info
* [ ] Establish chat connections (game<->proxy) and (proxy<->riotchatservers)
* [ ] Intercept and modify chat packets to change online status
* [ ] Front-end to easily send status changes
* [ ] Platform independant

# Components
* REST web server running on local host to respond to the riot client, as well as the front-end of leaguemealone. Implemented using only built-in http entities 
of the Oracle JRE. (ie. No framework)
* Appropriate handlers/controllers for available resources and methods.
* Services for modifying http packets
* Client for relaying requests to riot servers
* Server and client SSL sockets for proxying game client and chat servers.
* Front-end (TODO)
