import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    //represents a connection between the server/clientHandler and the client

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){

        //error handling
        try{

            while(!serverSocket.isClosed()){

                //blocking method, program will be halted here until a client connects
                Socket socket = serverSocket.accept();
                System.out.println("A NEW CLIENT HAS CONNECTED!");
                //responsible for communicating with Client, implements Runnable
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e){

        }
    }

    //for shutting down server socket when error occurs
    public void closeServerSocket(){
        try{
            if (serverSocket != null)
                serverSocket.close();
        }
        catch(IOException e){
            e.printStackTrace();

        }
    }

    //to instantiate and run objects
    public static void main(String[] args) throws IOException{

        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();


    }
}

