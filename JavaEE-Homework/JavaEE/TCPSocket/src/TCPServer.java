import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(3333);
            Socket socket = null;
            //记录客户端的数量
            int count = 0;
            System.out.println("the server is waiting the clients now");
            while (true){
                socket = serverSocket.accept();
                ServerThread serverThread = new ServerThread(socket);
                serverThread.start();
                count++; 
                System.out.println("the number of the clients is: " + count);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
