Computer Network Assignment 1
Name:	Sanjana Shivanand Shankarikoppa

Steps to executing via command prompt:
1.	Start the server by running the server.java file with port number as argument i.e. [port number] as follows
	java server.java 5555. Passing port number as argument is optional, if not passed then default port 5555 is taken into consideration.
3.	To check the tcp request reply process open browser and hit http://localhost:<portnumber> and hit enter to load the response from the server.
        Example url : http://localhost:5555 , this loads the default page.
4.	We can also request for a file by giving a file name in the end of the url i.e. http://localhost:5555/filename. 
5.	The same process can be repeated by running the client program. 
        Run the client.java with arguments <server_IPaddress/name>  [<port_number>] [<requested_file_name>] .  
        Port number here is optional but if while starting the server we provide port number then it is mandate to provide port number while starting the client as well.
        Example : java client.java localhost 5555 filename
6.	On doing step 5 client sends the request to the server and displays the corresponding result in client program console.
7.	All the processing in the server is logged in the server program console.
8.	If a file name that is not present in the folder is requested for then 404 error is thrown back at the client with a “File not found error message”.
        This message is framed by the server and sent as response to the client.
9.	To check file not found process give a file that does not exist in the folder. i.e.
         http://localhost:5555/filenotfound.html . This will return 404 not found response.



References:
1.	Stackoverflow.com
2.	Java TutorialPoint.com
3.	Programming Assignment 1_reference_Java pdf given by TA.
4.      java2s.com
5.      Codeproject.com
