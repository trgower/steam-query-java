# A Java Library for Querying Steam's Servers
This library designed to be user-friendly, without bloat, and unnecessary dependencies. It was created for a website that displays real-time steam server data. It is a narrow and deep design but can also be wide and shallow depending on your use.

This library is an implementation of this protocol:
- [Master Server Query Protocol](https://developer.valvesoftware.com/wiki/Master_Server_Query_Protocol)
- [Server Queries](https://developer.valvesoftware.com/wiki/Server_queries)

## Usage
#### Getting a list of DayZ servers

    MasterServer ms = new MasterServer();
    ms.requestServers(new QueryFilterBuilder().game("dayz").build());
You can then get a list of these servers by calling **ms.getGameServers()** which returns a HashMap with an InetSocketAddress as the key and the GameServer object as the value.

#### Getting a server by ip and query port
Also, query port is port + 1. For example: port is 50110, query port is 50111.

    GameServer server = new GameServer("0.0.0.0", 50111);

#### Retrieving data for each GameServer
You must call requestAll() on each GameServer to request the challenge number, information, player list, and rules from a server. You can do this for all servers by calling **ms.requestServerData()**. This method blocks until it receives all data from each server. This can take a very long time. You can update all of the servers using **ms.updateServerData()**.