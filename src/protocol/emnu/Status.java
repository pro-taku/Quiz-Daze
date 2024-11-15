package protocol.emnu;

// 요청을 정상적으로 처리했는지 판독하기 위함
public enum Status {
    OK("OK"),
    ERROR("ERROR");

    public final String value;

    Status(String s) {
        value = s;
    }
}
