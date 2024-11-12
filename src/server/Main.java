package server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            QuizDazeServer server = new QuizDazeServer(6789);
            server.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
