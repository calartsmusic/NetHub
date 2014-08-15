NetHub
======

Network Music Communication Library for SuperCollider

Dependencies on the NetLib Quark - https://github.com/supercollider-quarks/NetLib
which can be install in SC via the Quarks GUI:
```
Quarks.gui
```

Example usage:

<pre><code>
// Setup a new NetHub object. This will automatically broadcast your IP to 
// anyone else running NetHub on the network and register your computer with them.
x = NetHub.new;


// Setup a listener for the \knock command coming from Joe. Can be run as many times 
// as you want and will replace previous listeners
x.from(\joe, { arg msg;
  "I just received a message from Joe".postln;
}, \knock);


// Sending to Joe with any number of messages (as an array)
x.to(\joe, [\path, "someString", 123, "etc"]);
</code></pre>
