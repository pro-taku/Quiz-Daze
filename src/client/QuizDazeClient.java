package client;

import java.io.*;
import java.net.Socket;

public class QuizDazeClient {
    private String host;
    private int port;

    public QuizDazeClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    void run() throws IOException {
        System.out.println("[Client] run client");
        System.out.println("[Client] connect to " + host + ":" + port);
        Socket socket = new Socket(host, port);
        if (!socket.isConnected()) {
            System.out.println("[Client] (Error) failed to connect");
            return;
        }

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)),
                inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
        System.out.println("[Client] successfully connected!\n" +
                "[Client] input '-1' to exit, and 'exit' to close server");
        while (!socket.isClosed()) {
            System.out.print("[Client] input: ");
            String s = inFromUser.readLine();
            outToServer.writeBytes(s + '\n');

            String response = inFromServer.readLine();
            System.out.println("[Client] received: " + response);
            if (response.equals("-1")) {
                System.out.println("[Client] close client");
                socket.close();
            }
        }
    }
}
