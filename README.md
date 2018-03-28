# FTP_APP_FROM_SCRATCH
FTP_APP_FROM_SCRATCH Build using JAVA's SOCKETS, Multi-THREADS, CONCURRENT_PROGRAMMING.

One of the application where I received the title of "Best Implementation" 
for a course CSE 6324 Project Submission at University of Texas Arlington, under Yu Lei(ylei@cse.uta.edu).


###Project ReadMe file###

Working Environment
The code works on AWS/windows/Linux machine installed with java. 
***There are seperate version of code for AWS and local Windows machines.***

The only change comes in the way the folders are structured in these 
environments that needs to be taken into consideration and 
accordingly the variables “path” and “root” needs to be changes in Server.java file.
For ex: In windows folder depth is shown using “\” forward slash where as in AWS it is shown using a “/”. 
Also windows has drives such as C:, D: or any other where as Linux has folders usually starting from /root/username/home....

Program Requirement
The program doesn’t require any additional library or tool except Java JRE and SDK must be installed. 
It is self-sufficient. Running it from Eclipse IDE would give the best results. However, it can be run from command prompt as well.


Compiling and Executing

The following should be done from a client machine could be a seperate terminal 
or a seperate machine/s which actually is preferable to highlight the concurrent programming.
Compiling Client code: javac Client.java
Running Client code: java Client

The Server can be started on local Windows or AWS EC2 instance or Google App Engine.
Compiling Server code: javac Server.java
Running server code: java Server


Running the App for testing
1.	Run the server code. 
2.	Run the client code and enter the IP Address of the system on which server is running. 
    (Can find out IP Address using IPCONFIG command in Command Prompt if it is a Windows machine OR checking the AWS instance IP on AWS)
3.	Enter the control connection "port" number as present in the server code.
4.	Next, use USER <space><username> followed by PASS <space><password> to log into the server. 
    Valid user name and passwords are presented below.

Currently only these 4 usernames are hard coded which can be changed to support an SQL based storage.
Username	Password
micheal	micheal
arshad	arshad
pramodh	pramodh
mohan	mohan

5.	Once logged in, you can enter the following commands.
I.	STOR <local filepath> to store file on server
II.	RETR <file name on server>to retrieve file from server machine.
III.	LIST to see the list of files and folders in current directory.
IV.	STAT to see status of transactions.
V.	PORT <port number>to change the port number.
VI.	PWDto see present working directory’s path.
VII.	MKDIR <folder name> to create a directory.
VIII.	CWD<folder>to change working directory.
IX.	CDUP to change to parent directory.
X.	NOOP to ping the server.
XI.	CANCEL to cancel all ongoing transactions.
XII.	DEL <file name>to delete a file on server.
XIII.	QUIT to log out.

6.	At the end, use QUIT command to log out of the current session.

Sample Scenario
1.	Start the server.
2.	Start the client, enter sever IP, PORT and connect it with server. Followed by rovind USER and PASS.
3.	Now, use STOR command to upload a big file (say 350MB).
4.	Immediately, use STOR command to upload a small file(say 5MB or less).
5.	Immediately use RETR command to retrieve a small file (say 5MB or less).
6.	Now use STAT to see status of threads.
The STOR and RETR of small files should have a status “complete” and STOR of big file will be “In progress”.
7.	Now use CANCEL command to stop all the transactions. Now use STAT again to see the “terminated” status for STOR of big file.
8.	This can be done with numerous clients simultaneously as well.
9.	Next, try using all the other commands mentioned in the previous section.
10.	At the end, use QUIT command to log out.

