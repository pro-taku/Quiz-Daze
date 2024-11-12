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
            System.out.println("hello");
            while (true) {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

                System.out.println("listening");
                String s = inFromClient.readLine();
                if (s.equals("/quiz/start")) {
                    System.out.println("[Server] start quiz");
                    sendQuiz(inFromClient, outToClient);
                }
                else if (s.equals("-1")) {
                    return 0;
                }

                outToClient.writeBytes(s.toUpperCase() + '\n');
            }
        }
        catch (Exception e) {
            return -1;
        }
    }

    void sendQuiz(BufferedReader inFromClient, DataOutputStream outToClient) {
        try {
            while (true) {
                outToClient.writeBytes("Can you answer this question?\n");
                String answer = inFromClient.readLine();

                if (answer.equalsIgnoreCase("yes")) {
                    outToClient.writeBytes("1");
                } else {
                    outToClient.writeBytes("0");
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
