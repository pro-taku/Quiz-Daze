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

        while (true) {
            System.out.println("""
                    *** QuizDaze ***
                    (enter an option what you want to do)
                    1. start quiz
                    2. exit""");
            System.out.print("= ");
            int option = Integer.parseInt(inFromUser.readLine());

            if (option == 1) {
                int status = startQuiz(inFromUser, inFromServer, outToServer);
                if (status == -1) return;
            }
            else if (option == 2) {
                outToServer.writeBytes("-1");
                socket.close();
                break;
            }
            else {
                System.out.println("[!] invalid option\n");
            }
        }
    }

    int startQuiz(
            BufferedReader inFromUser,
            BufferedReader inFromServer,
            DataOutputStream outToServer
    ) throws IOException {
        System.out.println(1);
        outToServer.writeBytes("/quiz/start");
        System.out.println(2);

        while (true) {
            String quiz = inFromServer.readLine();
            System.out.println(3);
            System.out.print("Q. " + quiz + "\n= ");
            String answer = inFromUser.readLine();
            outToServer.writeBytes(answer);

            int response = Integer.parseInt(inFromServer.readLine());
            if (response == 1) {
                System.out.println("<< correct! >>\n");
            }
            else if (response == 0) {
                System.out.println("<< incorrect! >>\n");
                return 0;
            }
            else {
                System.out.println("[!] error occurred\n");
                return -1;
            }
        }
    }
}
