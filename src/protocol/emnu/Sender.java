package protocol.emnu;

// 통신의 주체
public enum Sender {
    CLIENT("CLIENT"),
    SERVER("SERVER");

    public final String value;

    Sender(String s) {
        value = s;
    }
}
