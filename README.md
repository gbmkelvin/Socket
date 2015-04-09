Program Requirements 
      CLIENT: The client should read input from the command line: 
      -x <number>, where number is a 32-bit unsigned integer 
      -t udp or tcp 
            for tcp,  the client opens a TCP connection to the server 
            for udp, the client sends the data to the server using UDP 
      -s host name of the server (ex: localhost  or 127.0.0.1) 
      -p the port being used by the server 

Requirement #1
  　　If any of the above command line arguments are missing, the program must print an error message and exit. 
    The command arguments may appear in any order. 
    You need to check if user specified the IP address for the '-s' option (in which case, you will use the IP address). 
    You must sanity each input to make sure it is valid. 
    For example, your program must reject a input number (-x number) that is negative, 
    a protocol other than tcp or upd, and a port number that is outside the port number range. 

Requirement #2
  　　After reading in the command line arguments, the client should create a message, 
    then open either a TCP or UDP socket, and send the message to the server. 
    After sending the message, the client should write a status message 
    (for example, "Sent number to server IP:port via TCP") and wait for an REPLY message from the server.
    If a REPLY message is received from the server, the client print "Success" and exits. 

Requirement #3
    If no REPLY message is received from the server in 3 seconds, the client should print an error message and exit. 
    If any of the above command line arguments are missing, the program must print an error message and exit. 
    The command arguments may appear in any order. As with the client, you must sanity check all arguments. 
    
Requirement #4
  　　Moreover, you have to build a GUI on the top of this network classes/package. 
    The GUI should be well designed so that you could change the server IP address, Port number, type a number in the text field, 
    and include a feature that you can select the protocol to send to the server.
  　　Regarding the Server program, upon receiving a message (see the message format section below), 
    the server should print out as below.
    The number is: X from client XXX.XXX.XXX.XXX on port XXXX