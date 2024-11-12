package client;

public class Main {
    public static void main(String[] args) {
        try {
            QuizDazeClient client = new QuizDazeClient("127.0.0.1", 6789);
            client.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
