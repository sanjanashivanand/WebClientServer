import javax.annotation.PostConstruct;
import java.net.Socket;
import java.io.*;

//Client opens a socket connection with the server and requests for a file. Once it gets the file it logs the contents
//of the file in console
public class Client implements Runnable{

    Socket client_Socket;    									//client socket
    DataOutputStream dataOutputStream;							//output stream
    DataInputStream dataInputStream;							//input stream
    String fileName; 
    long timeStart, timeEnd, timeUsed;//request file name

   /*
    Takes server name , port number and the file names as parameter,forms the request and sends it to the server
    @param args : command line arguments for this class
    @throws     Exception
    */
    public static void main(String[] args) throws Exception{
    	long timeStart, timeEnd, timeUsed;
		timeStart = System.nanoTime();

        String reqFileName;     //stores the request file name
        int port=0;        //default port the server is listening to
        if(args.length< 2){         // if no file name is given
            reqFileName = "";       //default page is requested
            port = 5555;
        }else if(args.length < 3){
            reqFileName = "";
            port = Integer.parseInt(args[1]);
        }else{
            reqFileName= args[2];   //file name
            port = Integer.parseInt(args[1]);
        }
      Client client = new  Client("localhost",port,reqFileName);  //instantiate the client object
      Thread thread = new Thread(client);           // Create a new thread to create an http request to the server
      thread.start();           //start the thread
    }

   /*
    @param  serverAddress : name of the host , port : port number the server is listening to,
    reqfilename : request file name
    @throws  Exception
    */
    Client(String serverAddress, int port,String reqFileName) throws Exception {
        fileName= reqFileName;
        client_Socket = new Socket(serverAddress, port);  //open a socket connection with server
        dataOutputStream = new DataOutputStream(client_Socket.getOutputStream());         // open output stream
        dataInputStream = new DataInputStream(client_Socket.getInputStream());            //open input stream
        System.out.println("Socket connection established between client and server!");
        System.out.println("\nServer Information :");
        System.out.println("\nServer Name:- " + client_Socket.getInetAddress().getHostName());   //log server name
        System.out.println("\nIP address of the server :"+client_Socket.getRemoteSocketAddress());   //ip address  and port number
        System.out.println("\nPort number the server is listening to : "+client_Socket.getPort());   //port number
        requestFile(fileName);              //sends the request to the server
    }

    /*
    Sends a GET request message to the server via the socket connection
    @param  filename : name of the requested file
    @throws Exception
    */
    public void requestFile(String fileName) throws Exception{
        fileName = "/"+fileName;
        System.out.println("\nRequest sent to server : "+ "GET "+fileName+" HTTP/1.1\n\n");
        dataOutputStream.writeBytes("GET "+fileName+" HTTP/1.1\n\n");
    }

    //Call when the thread is started
    public void run() {

            try {
                //buffer reader to read the socket input stream response from the server
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(client_Socket.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String temp;
                while ((temp = bufferReader.readLine()) != null) {
                    responseBuilder.append(temp + "\n");
                }
                timeEnd = System.nanoTime();
    			timeUsed = (timeEnd - timeStart) / 1000000;
    			System.out.println("RTT=" +  (double)Math.round(timeUsed * 100)/100+" ms");
                bufferReader.close();      //close the buffer reader
                client_Socket.close();   //close the socket connection with server
                System.out.println("\nResponse from server : " + responseBuilder.toString());   //log the response message from the server
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Something went wrong!");
            }
        }
}
