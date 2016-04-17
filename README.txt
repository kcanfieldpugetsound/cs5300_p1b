Kramer Canfield
Jerome Francis
Gus Donnelly

---------------------------------
ASSERT: Before running the project, you need to do the following:

- put your AWS access keys into the bootstrap.sh file
- create a SimpleDB domain called "Project1b.ktc36jf446gmd68"
You can do this with the command 

aws sdb create-domain --domain-name Project1b.ktc36jf446gmd68

from within an ec2 instance.

My s3 bucket has all the dependencies we need. See bootstrap.sh. Permissions have been configured and should be granted; if not, please contact me (Kramer) directly at ktc36@cornell.edu

---------------------------------
This file should include anything we need to know to grade your assignment. In particular, it should briefly describe the overall structure of your solution, including formats of your cookies and RPC messages; and specify what functionality is implemented in each source file. 
If you implemented extra credit options, describe the changes you needed to make.
Overall structure:
Cookie Format:
Cookie name is CS5300Project1BSession
Cookie value is a String made of IP addresses separated by "_" followed by ":" followed by the SessionID which is the serverID,sessionNumber,rebootID,versionNumber.
RPC Messages:
RPC Messages consist of the following information:
 - 1 byte: message type
 - 4 bytes: payload length
 - remainder: message payload (cookie data)


**** Source Files ****

NOTE: PLEASE SEE DETAILED JAVADOCS FOR MORE INFORMATION

** Constants.java
This is a class to organize all of our global variables that we create in bootstrap.sh as well as other places.


** IPAddress.java
This is a wrapper class to handle IP addresses and convert them between Strings and byte arrays.


** SimpleDBInterface.java
This file registers this server with the SimpleDB domain. 


** ServerFileInterface.java
Provides methods for interacting with the servers file called servers.txt.


** Initializer.java
The Initializer loads the values from the environmental variables as specified in bootstrap.sh. It also runs code for registering the server with SimpleDB, getting a list of other servers in the table and setting up a log system.


** RPC.java
Defines the Remote Procedure Call (RPC) Service, which is used for sending and receiving Session data to and from other servers. Particularly, the RPC service supports two methods:
  pushData(IPAddress, Session)
  requestData(IPAddress, SessionId)


** RPCDataRequestHandlerThread.java
 A thread for handling DATA_REQUEST messages received and stored in edu.cornell.cs5300.rpc.receive.RPCReceiver


** RPCPushRequestHandlerThread.java
A thread for handling PUSH_REQUEST messages received and stored in RPCReceiver.


** RPCMessage.java


** RPCMessageFactory.java


** RPCMessageInterpreter.java


** RPCReceiver.java


** RPCReceiveThread.java


** RPCReceiveReaperThread.java


** RPCSender.java


** RPCSenderThread.java


** DatagramService.java
 A class which provides {@link DatagramPacket} sending and receiving capabilities for the RPC service by abstracting away the methods of DatagramSocket. Particularly, DatagramService provides blocking sendDatagramPacket(DatagramPacket)and receiveDatagramPacket() methods.


** Project1bServlet.java
This class provides the bulk of the functionality and user interfacing from Project 1a including sending cookies and handling doGet() and doPost() and responding to user button clicks.


** State.java
Stores this server's volatile state.


** Session.java
An immutable class containing all information necessary to store a user's data for one session including user data and sessionID.


** SessionId.java
Representation of a globally unique {@code Session} identifier. The ID of the server, the number of times the server has rebooted, the local session number, and a version number are combined to produce such a globally unique ID.


** SessionManager.java
 A class for managing user sessions for the entire system, across multiple servers.


** UserData.java
 A class for holding a user's data. It has a String message and can convert the message to a byte[].


** Pair.java
A class for creating an immutable pair of objects. Pair components are accessed using .left and .right.


** SerializeUtils.java
A class containing methods useful for serializing data including byteArrayToInt(), stringToByteArray(), and byteArrayToString().