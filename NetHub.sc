
NetHub {
    var <>clients,
        hostName,
        <receiverCallbacks,
        <clientBusses;

    *new {| hostName |
        ^super.new.init(hostName);
    }

    init {| name |
        if(name.isNil,{
            hostName = Platform.userConfigDir.split[2];
        }, {
            hostName = name;
        });

        clients = Dictionary.new;
        clientBusses = Dictionary.new;
        receiverCallbacks = Dictionary.new;

        this.setupNetwork();
        this.broadcastAddress();

        ("NetHub: Initialized for " ++ hostName).postln;
    }

//////////// SETUP
    broadcastAddress {
        var broadcastAddress,
            hostIPAddress = NetAddr.myIP;

        var broadcastAddressSplit = hostIPAddress.split($\.);
        broadcastAddressSplit.put(3,"255");

        // Concatenate the results together
        broadcastAddressSplit.do({|item, i|
            if(i > 0, {
                broadcastAddress = broadcastAddress ++  "." ++ item;
            }, {
                // first member of IP address doesn't need to have a dot attached to it
                broadcastAddress = item;
            })
        });

        NetAddr.broadcastFlag = true;
        NetAddr(broadcastAddress, NetAddr.langPort).sendMsg('/hostip', hostIPAddress, NetAddr.langPort, hostName);
        NetAddr.broadcastFlag = false;

        ("NetHub: IP broadcast to network on " ++ broadcastAddress).postln;
    }

    // Set up the network based on a constructed broadcast address (based on Mark Trayle's code for "In My Room")
    setupNetwork {
        // respond to a message from a client on the network
        OSCdef(\clientIPResponder, {| msg,time,addr,recvPort|
            var clientName = msg[3];
            msg.postln;
            clients = clients.add(clientName.asSymbol -> NetAddr(msg[1].asString, msg[2].asInteger));
            ("NetHub: Received IP from " ++ clientName ++ " ").post;
            clients[clientName].postln;
        },'/clientip');

        OSCdef(\hostIPResponder, {| msg,time,addr,recvPort|
            var receivedIP = msg[1].asString,
                receivedPort = msg[2].asInteger,
                receivedName = msg[3].asString;

            msg.postln;
            clients = clients.add(receivedName -> NetAddr(receivedIP, receivedPort));

            // broadcast my ip back to the client
            NetAddr(receivedIP, receivedPort).sendMsg('/clientip', NetAddr.myIP, NetAddr.langPort, hostName);

            ("NetHub: IP received from " ++ receivedName ++ " = " ++ receivedIP ++":" ++ receivedPort).postln;
        },'/hostip');
    }

//////////// MESSAGING

    // alias for sendAll()
    toAll { | messageData |
        this.sendAll(messageData);
    }

    // Broadcast a message to all clients in your Dictionary
    sendAll { | messageData |
        clients.keysValuesDo({ | client, netAddress |
            netAddress.sendMsg(messageData);
        });
    }

    // alias for sendTo()
    to { | clientName, messageData |
        this.sendTo(clientName, messageData);
    }
    // Send to a specific client
    sendTo { | clientName, messageData |
        if(clients.size > 0, {
            clients[clientName.asSymbol].sendBundle(0, [messageData], messageData);
        }, {
            "NetHub: You don't have any clients to send to".postln;
        });
    }

    asBus { | name, mappingFunction, path |
        var listener;
        if(mappingFunction.isNil, {
            mappingFunction = {|msg|
                ^msg[1];
            };
        },{});

        if(clientBusses[name].isNil, {
            clientBusses[name.asSymbol] = Bus.control(Server.default, 1);
        });

        // make sure to add a way to clean these up later
        listener = OSCdef((name.asString ++ "-busReceiver").asSymbol, { | msg |
            var receivedValue = mappingFunction(msg);

            clientBusses[name.asSymbol].set(receivedValue);
        }, path );


        ^clientBusses[name.asSymbol];
    }

//////////// RECEIVERS and CALLBACKS
    setResponse { | clientName, responderFunc, path |
        var defName = this.getDefName(clientName, path);

        var listener = OSCdef(defName, responderFunc, path, clients[clientName.asSymbol]);
        receiverCallbacks.add(defName -> listener);
    }
    //alias for setResponse()
    from { | clientName, responderFunc, path |
        this.setResponse(clientName, responderFunc, path );
    }

    setAllResponses {| responderFunc, path |
        clients.keyValuesDo({ |clientName, netAddress|
            OSCdef(clientName.asSymbol, responderFunc, path);
        });
    }

    unSetResponse { | clientName, path |
        var defName = this.getDefName(clientName, path);

        receiverCallbacks.at(defName).free;
        receiverCallbacks.removeAt(defName);
    }

    unSetAllResponses {
        clients.keysValuesDo({ |clientName, netAddress|
            receiverCallbacks.do({|listener,i|
                listener.free;
            });
            receiverCallbacks.keysDo({|key|
                key.postln;
                receiverCallbacks.removeAt(key);
            });
        });
    }

//////////// Utils
    // gets an OSCdefname based on the client name and path
    getDefName {| clientName, path |
        var defName = (clientName.asString ++ "-" ++ path.asString).asSymbol;
        ^defName;
    }
}

