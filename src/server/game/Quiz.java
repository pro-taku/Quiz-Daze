package server.game;

import java.util.Arrays;
import java.util.List;

public class Quiz {
    final public int uid;
    final public String quiz;
    final public List<String> answer;
    final public int point;

    public Quiz(int uid, String quiz, String answer, int point) {
        this.uid = uid;
        this.quiz = quiz;
        this.answer = Arrays.asList(answer.split("/"));
        this.point = point;
    }

    static public Quiz fromString(String s) {
        String[] parts = s.split("/");
        return new Quiz(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                Integer.parseInt(parts[3])
        );
    }

    public boolean isAnswer(String answer) {
        return this.answer.contains(answer);
    }
}
