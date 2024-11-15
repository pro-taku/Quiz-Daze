package client;

import protocol.CustomSocketData;
import protocol.emnu.Sender;
import protocol.emnu.Status;
import protocol.emnu.Type;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class QuizDazeClient {
    private final String host;
    private final int port;
    private CustomSocketData data;

    private BufferedReader inFromUser;
    private BufferedReader inFromServer;
    private DataOutputStream outToServer;

    public QuizDazeClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception {
        // 클라이언트 소켓 생성
        System.out.println("[Client] run client");
        System.out.println("[Client] connect to " + host + ":" + port);
        Socket socket = new Socket(host, port);

        // 소켓이 연결되지 않았을 경우
        if (!socket.isConnected()) {
            System.out.println("[Client] (Error) failed to connect");
            return;
        }

        // Socket 통신을 위한 I/O
        inFromUser = new BufferedReader(new InputStreamReader(System.in));
        inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outToServer = new DataOutputStream(socket.getOutputStream());

        while (true) {
            // 퀴즈 시작
            System.out.println("""
                    
                    *** QuizDaze ***
                    (enter an option what you want to do)
                    1. start quiz
                    2. exit""");
            System.out.print("= ");
            int option;
            try {
                option = Integer.parseInt(inFromUser.readLine());
                System.out.println();
            }
            catch (NumberFormatException e) {
                System.out.println("[!] invalid option\n");
                continue;
            }

            // 옵션에 따라 퀴즈를 시작/종료
            if (option == 1) {
                int status = startQuiz();
                if (status == -1) return;
            }
            else if (option == 2) {
                data = new CustomSocketData(
                        Sender.CLIENT,
                        Type.POST,
                        Status.ERROR,
                        "/exit",
                        Map.of("message", "Unexpected exit from client")
                );
                outToServer.writeBytes(data.encode() + '\n');
                socket.close();
                break;
            }
        }
    }

    int startQuiz() throws Exception {
        // 서버에 퀴즈 게임을 시작함을 알림
        data = new CustomSocketData(
                Sender.CLIENT,
                Type.POST,
                Status.OK,
                "/quiz/start",
                Map.of()
        );
        outToServer.writeBytes(data.encode() + '\n');
        System.out.println("===================================\n");

        while (true) {
            // 데이터 수신
            data = CustomSocketData.decode(inFromServer.readLine());
            switch (data.url) {
                // 만약 서버가 종료된 상황이면
                case "/exit" -> {
                    System.out.println("[!] server closed the connection\n");
                    return -1;
                }
                // 만약 서버에서 에러가 발생한 상황이면
                case "/error" -> {
                    System.out.println("[!] error occurred\n");
                    return -1;
                }
                // 만약 퀴즈 게임이 종료된 상황이면
                case "/quiz/end" -> {
                    System.out.println("===================================\n");
                    System.out.println(
                            "Game Over!\n" +
                            "score: " + data.body.get("score") + "\n" +
                            "corrects: " + data.body.get("corrects") + "\n"
                    );
                    return 0;
                }
            }

            // 퀴즈 게임 진행
            String quizId = data.url.split("/")[2];
            System.out.print(
                    "< Q" + quizId + " >\n" +
                    data.body.get("quiz") + "\n" +
                    "> "
            );

            // 답안 제출
            String answer = inFromUser.readLine();
            System.out.println();
            data = new CustomSocketData(
                    Sender.CLIENT,
                    Type.POST,
                    Status.OK,
                    "/quiz/" + quizId,
                    Map.of("answer", answer)
            );
            outToServer.writeBytes(data.encode() + '\n');

            // 답안에 대한 결과 수신
            data = CustomSocketData.decode(inFromServer.readLine());
            int response = Integer.parseInt(data.body.get("correct"));
            if (response == 1) {
                System.out.println("[!] correct! (+ " + data.body.get("score") + ")\n");
            }
            else if (response == 0) {
                System.out.println("<< incorrect! >>\n");
            }
            else {
                System.out.println("[!] error occurred\n");
                return -1;
            }
        }
    }
}
