package server;

import protocol.CustomSocketData;
import server.game.Game;

import java.io.*;
import java.net.*;

public class QuizDazeServer {
    public final int port;

    public QuizDazeServer(int port) {
        this.port = port;
    }

    public void run() {
        try {
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
                } else if (status == -1) {
                    System.out.println("[Server] error occurred. close connection from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                    socket.close();
                    listener.close();
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to run server: " + e.getMessage());
        }
        finally {
            System.out.println("[Server] close server");
        }
    }

    int process(Socket socket) {
        try {
            while (true) {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

                CustomSocketData data = CustomSocketData.decode(inFromClient.readLine());
                if (data.url.equals("/quiz/start")) {
                    System.out.println("[Server] start quiz");
                    new Game(inFromClient, outToClient).start();
                }
                else if (data.url.equals("/exit")) {
                    return 0;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
