package server.game;

import protocol.CustomSocketData;
import protocol.emnu.Sender;
import protocol.emnu.Status;
import protocol.emnu.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Game {
    static private int count = 0;
    static private List<Quiz> quiz;
    static private final String fpath = "./quiz_set.csv";

    public final int id;
    public int score = 0;
    private final int[] order;
    private final int[] corrects;

    private final BufferedReader inFromClient;
    private final DataOutputStream outToClient;

    public Game(BufferedReader inFromClient, DataOutputStream outToClient) throws IOException {
        if (quiz == null) {
            try {
                loadQuizSet();
            } catch (IOException e) {
                CustomSocketData data = new CustomSocketData(
                        Sender.SERVER,
                        Type.POST,
                        Status.ERROR,
                        "/quiz",
                        Map.of("error", "Failed to load quiz set")
                );
                outToClient.writeBytes(data.encode() + '\n');

                e.printStackTrace();
                throw new RuntimeException("Failed to load quiz set");
            }
        }

        id = count++;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;

        // 1부터 quiz.size()까지의 숫자를 랜덤하게 섞어서 order에 저장
        order = new int[quiz.size()];
        corrects = new int[quiz.size()];
        for (int i = 0; i < quiz.size(); i++) {
            order[i] = i;
            corrects[i] = 0;
        }
        for (int i = 0; i < order.length; i++) {
            int j = (int) (Math.random() * order.length);
            int temp = order[i];
            order[i] = order[j];
            order[j] = temp;
        }
    }

    private void loadQuizSet() throws IOException {
        String path = System.getProperty("user.dir") + fpath;
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        quiz = new ArrayList<Quiz>();

        br.readLine(); // skip header
        while ((line = br.readLine()) != null) {
            quiz.add(Quiz.fromString(line));
        }
    }

    public void start() throws Exception {
        CustomSocketData data;

        for (int i = 0; i < quiz.size(); i++) {
            data = new CustomSocketData(
                    Sender.SERVER,
                    Type.POST,
                    Status.OK,
                    "/quiz/" + i,
                    Map.of("quiz", quiz.get(order[i]).quiz)
            );
            outToClient.writeBytes(data.encode() + '\n');

            data = CustomSocketData.decode(inFromClient.readLine());
            System.out.println("[Game] (id=" + id + ") user's answer: " + data.body.get("answer"));

            if (quiz.get(order[i]).isAnswer(data.body.get("answer"))) {
                System.out.println("[Game] (id=" + id + ") correct!");
                score += quiz.get(order[i]).point;
                corrects[i] = 1;

                data = new CustomSocketData(
                        Sender.SERVER,
                        Type.POST,
                        Status.OK,
                        "/quiz/" + i + "/answer",
                        Map.of("correct", "1", "score", Integer.toString(score))
                );
                outToClient.writeBytes(data.encode() + '\n');
            }
            else {
                System.out.println("[Game] (id=" + id + ") wrong!");
                data = new CustomSocketData(
                        Sender.SERVER,
                        Type.POST,
                        Status.OK,
                        "/quiz/"+ i + "/answer",
                        Map.of("correct", "0")
                );
                outToClient.writeBytes(data.encode() + '\n');
            }
        }

        System.out.println("[Game] (id=" + id + ") gmae over!");
        System.out.println("[Game] (id=" + id + ") score: " + score);
        data = new CustomSocketData(
                Sender.SERVER,
                Type.POST,
                Status.OK,
                "/quiz/end",
                Map.of(
                        "score", Integer.toString(score),
                        "corrects", String.join("/", Arrays.stream(corrects).mapToObj(Integer::toString).toArray(String[]::new))
                )
        );
        outToClient.writeBytes(data.encode() + '\n');
    }
}
