NetHub
======

Network Music Communication Library for SuperCollider


Example usage:
x = NetHub.new;

// Setup a listener for the \knock command coming from Joe 
x.from(\joe, { arg msg;
  "I just received a message from Joe".postln;
}, \knock);

// Sending to Joe with any number of messages (as an array)
x.to(\joe, [\path, "someString", 123, "etc"]);
