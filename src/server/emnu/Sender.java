package server.emnu;

public enum Sender {
    CLIENT("CLIENT"),
    SERVER("SERVER");

    public final String value;

    Sender(String s) {
        value = s;
    }
}
