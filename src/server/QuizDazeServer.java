package server;

import java.io.*;
import java.net.*;

public class QuizDazeServer {
    public final int port;

    public QuizDazeServer(int port) {
        this.port = port;
    }

    void run() throws IOException {
        // current address
        ServerSocket listener = new ServerSocket(port);
        System.out.println("[Server] run server " + listener.getInetAddress().getHostAddress() + ":" + port);

        while (!listener.isClosed()) {
            Socket socket = listener.accept();
            System.out.println("[Server] new connection from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            int status = process(socket);
            if (status == 0) {
                System.out.println("[Server] disconnected from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                socket.close();
            }
            else if (status == -1) {
                socket.close();
                listener.close();
            }
        }
        System.out.println("[Server] close server");
    }

    int process(Socket socket) {
        try {
            while (true) {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

                // Todo. 변경할 것
                String s = inFromClient.readLine();
                System.out.println("[Server] received: " + s);
                if (s.equals("exit")) {
                    outToClient.writeBytes("-1");
                    throw new Exception("exit");
                }
                if (s.equals("-1")) {
                    outToClient.writeBytes("-1");
                    return 0;
                }

                outToClient.writeBytes(s.toUpperCase() + '\n');
            }
        }
        catch (Exception e) {
            return -1;
        }
    }
}
