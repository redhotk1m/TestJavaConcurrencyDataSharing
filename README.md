# TestJavaConcurrencyDataSharing
This is a project where i tried to create UDP with 0% packetloss, ignoring packet order. I didn't want to use TCP, since i needed UDPs speed.

I tried to do this with several different methods, searching for the best one.
I stresstested it, to see how many clients could send x amount of packets per second.

# How it works
1. Client sends packet to server, while also saving that packet in a queue.
   1. Server gets the packet, and resends it.
   2. Client receives the packet, resulting in 0% loss, and removing the packet from the queue.
2. Packet is lost on it's way to server, or on it's way from server to client.
   1. No packet has been received within X ms, resend packet and update it's position in the queue.


This uses several threads, to make sure i don't have packet loss due to any of the ports being full.
There's always one thread for each send/receive, per client/server.

Check figurs below on the two ways i tried to implement reliable UDP.
![Image of RUDP ex 1](https://github.com/redhotk1m/TestJavaConcurrencyDataSharing/blob/master/0001.jpg)
![Image of RUDP ex 2](https://github.com/redhotk1m/TestJavaConcurrencyDataSharing/blob/master/0002.jpg)

This project remains unfinished.
