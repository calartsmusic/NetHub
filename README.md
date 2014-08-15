NetHub
======

A peer-to-peer network music communication library for SuperCollider
meant to eliminate setup woes by handling asynchronous connections and abstracting common features such as broadcasting message, maintaing a simpler syntax for clarity as well as quickness, and allowing one to easily connect between different platforms.

Depends on the NetLib Quark (https://github.com/supercollider-quarks/NetLib) 
which can be installed in SuperCollider via the Quarks GUI:
```
Quarks.gui
```

Example usage:

<pre><code>
// Setup a new NetHub object. This will automatically broadcast your IP to 
// anyone else running NetHub on the network and register your computer with them.
// This only needs to be run once. Anyone who connects later will automatically be connected to you, and you to them
x = NetHub.new;


// Setup a listener for the \knock command coming from Joe. Can be run as many times 
// as you want and will replace previous listeners
x.from(\joe, { arg msg;
  "I just received a message from Joe".postln;
}, \knock);


// Sending to Joe with any number of messages (as an array)
x.to(\joe, [\path, "someString", 123, "etc"]);
</code></pre>
