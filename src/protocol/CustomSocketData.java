package protocol;

import protocol.emnu.Sender;
import protocol.emnu.Status;
import protocol.emnu.Type;

import java.util.Map;

public class CustomSocketData {
    private static long socketCount = 0;
    public final long uid;
    public Sender sender;
    public Type type;
    public Status status;
    public String url;
    public Map<String, String> body;

    public CustomSocketData(Sender sender, Type type, Status status, String url, Map<String, String> body) {
        this.uid = socketCount++;
        this.sender = sender;
        this.type = type;
        this.status = status;
        this.url = url;
        this.body = body;
    }

    static public CustomSocketData decode(String rawMessage) throws Exception {
        Map<String, String> parts = Protocol.splitHeaderBody(rawMessage);
        Map<String, String> header = Protocol.decodeContent(parts.get("header"));
        Map<String, String> body = Protocol.decodeContent(parts.get("body"));

        return new CustomSocketData(
                Sender.valueOf(header.get("sender")),
                Type.valueOf(header.get("type")),
                Status.valueOf(header.get("status")),
                header.get("url"),
                body
        );
    }

    public String encode() {
        return "{" +
                "sender:" + sender.value + "\\n" +
                "type:" + type.value + "\\n" +
                "url:" + url + "\\n" +
                "status:" + status.value + "\\n" +
                "}&{" +
                body.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .reduce((acc, entry) -> acc + "\\n" + entry)
                .orElse("") +
                "}";
    }

    public String toString() {
        String result = "";
        result += "sender: " + sender.value + "\n";
        result += "type: " + type.value + "\n";
        result += "url: " + url + "\n";
        result += "status: " + status.value + "\n";
        result += "body: {\n";
        for (Map.Entry<String, String> entry : body.entrySet()) {
            result += "  " + entry.getKey() + ": " + entry.getValue() + "\n";
        }
        result += "}";
        return result;
    }

    public CustomSocketData copyWith(Sender sender, Type type, Status status, String url, Map<String, String> body) {
        return new CustomSocketData(
                sender != null ? sender : this.sender,
                type != null ? type : this.type,
                status != null ? status : this.status,
                url != null ? url : this.url,
                body != null ? body : this.body
        );
    }
}
