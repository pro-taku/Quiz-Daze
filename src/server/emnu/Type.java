package server.emnu;

public enum Type {
    GET("GET"),
    POST("POST"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    public final String value;

    Type(String s) {
        value = s;
    }
}
