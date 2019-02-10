import java.io.*;
import java.net.*;
import java.util.*;

// Server class that accepts requests from clients and gives the request to thread process for each incoming request so that it can handle multiple clients
public final class Server {

   /* Entry point of the program
    @parameters: Command line arguments , Takes port number as input
    @throws Exception*/
    public static void main(String[] args) throws Exception {
        // Set the port number from the argument passed from command line.
        int port ;
        if(args.length < 1){
            port = 5555;
        }else{
            port = Integer.parseInt(args[0]);
        }
        // Establish the listen socket connection.
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started and listening to port : "+port);
        // Accepts http requests from the client all the time and creates a thread for each request to be handled
        while (true) {
            // Listen for a TCP connection request and accept it when ever the clients requests for a file
            Socket clientSocket = serverSocket.accept();
            // Construct an object of HttpRequestResponse to process the HTTP request message and respond accordingly.
            HttpRequestResponse request = new HttpRequestResponse(clientSocket);
            // Create a new thread to process the http request.
            Thread thread = new Thread(request);
            // Start the thread to process and respond to the client (this calls the run() method of the thread)
            thread.start();
        }
    }
}


//This class process the http request and gives back the response accordingly
final class HttpRequestResponse implements Runnable {

    final static String CRLF = "\r\n";         //Constant to give line break and carriage return for formatting the http header fields
    Socket socket;                              //Local socket object for the thread

    // Constructor to initialize the socket object
    public HttpRequestResponse(Socket socket) throws Exception {
        this.socket = socket;
    }

    //Call when the thread is started
    public void run() {
        try {
            processRequest();       //call the method for processing the http request
        } catch (Exception e) {
            System.out.println();     //catches the exception if at all any
        }
    }

    /*
    Method is called to process the http header and search for the requested file.
    If file not found then return 404 error else just returns the file
    @param
    @throws Exception
    */
    private void processRequest() throws Exception {


        InputStream is = socket.getInputStream();               //open socket's input stream
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());  //open socket's output stream

        BufferedReader br = new BufferedReader(new InputStreamReader(is));     //create a buffer reader to read the contents of the inputstream


        // Get the request line of the HTTP request message.
        String requestLine = br.readLine();
        // Get and display the header lines.
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println("Client Request : \n"+headerLine);   //displays the header line of the request
        }

        // Extract the filename from the request line.
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken(); // skip over the method, which should be "GET"
        String fileName = tokens.nextToken();
        // Prepend a "." so that file request is within the current directory.
        fileName = "." + fileName;
        System.out.println("File name from the server(request file name) :" + fileName);
        //if no file requested it fetches and displays the default page
        if (fileName.equalsIgnoreCase("./")) {
            fileName = "index.html";          //default home page if not file name is mentioned
        }
        File file = null;
        // Open the requested file.
        FileInputStream fis = null;

        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);            //opening file input stream for reading the file
        } catch (FileNotFoundException e) {
            fileExists = false;                 //setting the flag to file does not exists
        }

        // Construct the response message.
        String statusLine = null;               //first like with method type and status code
        String contentTypeLine = null;          //mime type info is present in this line
        String entityBody = null;               //content of the message thats to be sent
        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK" + CRLF;
            contentTypeLine = "Content-type: " +
                    contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.1 404 NOT FOUND" + CRLF;
            contentTypeLine = "Content-Type: text" + CRLF;
            entityBody = "<HTML>" +
                    "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                    "<BODY>File not found! Please try with some other file name!!</BODY></HTML>";
        }

        os.writeBytes(statusLine);              // Send the status line.writing it to output stream of the socket
        os.writeBytes(contentTypeLine);         // Send the content type line.
        os.writeBytes(CRLF);                    // Send a blank line to indicate the end of the header lines.

        if (fileExists) {                       // Send the entity body if the file exists
            sendBytes(fis, os);                 //sending the message body to the client
            fis.close();                        //closing file inputstream
        } else {
            os.writeBytes(entityBody);          //If file does not exists send a not found message html page
        }
        System.out.println("\nResponse to client : "+statusLine+"\n"+contentTypeLine+"\n");
        // Close streams and socket.
        os.close();
        br.close();
        socket.close();
    }

   /*
    This method copies the requested file and put it on the output stream of the socket and sends it to client
    @param  fis is input stream to read the file, os is the output stream to write to the datastream of the socket
    @throws Exception
    */
    private static void sendBytes(FileInputStream fis, OutputStream os)
            throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;
        // Copy requested file into the socket's output stream.
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

   /*
    This method is used to set the mime type of the response. It gets the type of the file that is requested
    @param fileName is the name of the requested file
    @throws
    */
    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }else if(fileName.endsWith("txt") || fileName.endsWith("")){
        	return "text/plain";
        }else if(fileName.endsWith("jpg") || fileName.endsWith(".jpeg")){
        	return "image/jpeg";
        }else if(fileName.endsWith(".xml")){
        	return "application/xml";
        }else if(fileName.endsWith(".csv")){
        	return "text/csv";
        }
        return "application/octet-stream";
    }
}
