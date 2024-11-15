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
            // 서버 소켓 생성
            ServerSocket listener = new ServerSocket(port);
            System.out.println("[Server] run server " + listener.getInetAddress().getHostAddress() + ":" + port);

            while (!listener.isClosed()) {
                // 클라이언트의 연결을 기다림
                Socket socket = listener.accept();

                // 접속한 클라이언트의 정보 출력
                System.out.println("[Server] new connection from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                // 클라이언트와 통신
                int status = process(socket);

                // process가 정상적으로 종료
                if (status == 0) {
                    System.out.println("[Server] disconnected from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                    socket.close();
                }
                // process에서 에러 발생
                else if (status == -1) {
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
                // Socket 통신을 위한 I/O
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

                // 클라이언트로부터 데이터를 받아서 처리
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
