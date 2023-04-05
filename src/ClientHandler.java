import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    //keep track of all of the Clients
    //whenever clients sends a message, we loop through arraylist and send message to all clients
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    //socket passed from Server class to establish connection between Client and Server
    private Socket socket;

    //used to read and send data from and to clients respectivly
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private String clientUsername;

    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            //Outputstream for sending data
            this.bufferedWriter =  new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //Inputstream for reading data
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            //add
            clientHandlers.add(this);
            System.out.println("SERVER: " + clientUsername + " has entered the chat!");
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
        } catch(IOException e) {
            //closes socket and streams
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

    //Seperate thread for listening to messages
    @Override
    public void run(){
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler : clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername))
                {
                    //possible decryption
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    //for catch, whenever this is called = user is leaving
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
