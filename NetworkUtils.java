import java.io.*;
import java.net.*;

public class NetworkUtils {

    // Utility method to send an object over a socket
    public static void sendObject(Socket socket, Object object) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.writeObject(object);
        outputStream.flush();
    }

    // Utility method to receive an object from a socket
    public static Object receiveObject(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        return inputStream.readObject();
    }

    // Utility method to start a server
    public static ServerSocket startServer(int port) throws IOException {
        return new ServerSocket(port);
    }

    // Utility method to connect to a server
    public static Socket connectToServer(String host, int port) throws IOException {
        return new Socket(host, port);
    }
}