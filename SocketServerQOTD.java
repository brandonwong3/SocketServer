import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class SocketServer {
    private static final int TCP_PORT = 17;
    private static final int UDP_PORT = 17;
    private static final int BUFFER_SIZE = 512;
    private static final String[] QUOTES = {
        "At last we will reveal ourselves to the Jedi. At last we will have revenge",
        "Have you ever heard the tragedy of Darth Plagueis the Wise?",
        "I do not fear the dark side as you do! I have brought peace, freedom, justice, and security to my new empire!",
        "Save the rebellion! Save the dream!",
        "I am McLovin.",
        "How are your classes going, DOUG?!"
    };
    private static final Random RANDOM = new Random();
    private static final List<String> USED_QUOTES = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket tcpSocket = new ServerSocket(TCP_PORT);
            DatagramSocket udpSocket = new DatagramSocket(UDP_PORT);
            byte[] buffer = new byte[BUFFER_SIZE];
            System.out.println("Server: "); // test

            while(true) {
                Socket clientSocket = tcpSocket.accept();
                Thread tcpThread = new Thread(() -> {
                    try {
                        String quote = getRandomQuote();
                        USED_QUOTES.add(quote);
                        clientSocket.getOutputStream().write(quote.getBytes());
                        clientSocket.close();
                    } 
                    catch (IOException i) {
                        i.printStackTrace();
                    }
                });

                tcpThread.start();
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(requestPacket);
                Thread udpThread = new Thread(() -> {
                    try {
                        String quote = getRandomQuote();
                        USED_QUOTES.add(quote);
                        DatagramPacket responsePacket = new DatagramPacket(quote.getBytes(), quote.getBytes().length, requestPacket.getAddress(), requestPacket.getPort());
                        udpSocket.send(responsePacket);
                    } 
                    catch (IOException i) {
                        i.printStackTrace();
                    }
                });
                udpThread.start();
            }
        } 
        catch (IOException i) {
            i.printStackTrace();
        }
    }

    private static String getRandomQuote() {
        Random random = new Random();
        int generate = random.nextInt(QUOTES.length);
        return "Quote: " + QUOTES[generate];
    }

}


