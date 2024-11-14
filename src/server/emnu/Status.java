package server.emnu;

public enum Status {
    OK("OK"),
    ERROR("ERROR");

    public final String value;

    Status(String s) {
        value = s;
    }
}
