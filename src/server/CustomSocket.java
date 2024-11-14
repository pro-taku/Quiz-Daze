package server;

import server.emnu.Sender;
import server.emnu.Status;
import server.emnu.Type;

import java.util.Map;

public class CustomSocket {
    static long socketCount = 0;
    final long uid;
    Sender sender;
    Type type;
    Status status;
    String url;
    Map<String, String> body;

    public CustomSocket(Sender sender, Type type, Status status, String url, Map<String, String> body) {
        this.uid = socketCount++;
        this.sender = sender;
        this.type = type;
        this.status = status;
        this.url = url;
        this.body = body;
    }

    static public CustomSocket decode(String rawMessage) throws Exception {
        Map<String, String> parts = Protocol.splitHeaderBody(rawMessage);
        Map<String, String> header = Protocol.decodeContent(parts.get("header"));
        Map<String, String> body = Protocol.decodeContent(parts.get("body"));

        return new CustomSocket(
                Sender.valueOf(header.get("sender")),
                Type.valueOf(header.get("type")),
                Status.valueOf(header.get("status")),
                header.get("url"),
                body
        );
    }

    public String encode() {
        return "{" +
                "sender:" + sender.value + "\n" +
                "type:" + type.value + "\n" +
                "url:" + url +
                "status:" + status.value + "\n" +
                "}&{" +
                body.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .reduce((acc, entry) -> acc + "\n" + entry)
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

    public CustomSocket copyWith(Sender sender, Type type, Status status, String url, Map<String, String> body) {
        return new CustomSocket(
                sender != null ? sender : this.sender,
                type != null ? type : this.type,
                status != null ? status : this.status,
                url != null ? url : this.url,
                body != null ? body : this.body
        );
    }

    public static void main(String[] args) throws Exception {
        String src = "{" +
                    "sender:CLIENT\n" +
                    "type:GET\n" +
                    "url:/example/load\n" +
                    "status:OK\n" +
                "}&{" +
                    "title:{" +
                        "\"k1\":\"v1\"," +
                        "\"k2\":\"v2\"," +
                        "\"k3\":\"v3\"" +
                    "}\n" +
                    "content:{" +
                        "\"k1\":\"v1\"," +
                        "\"k2\":\"v2\"," +
                        "\"k3\":\"v3\"" +
                    "}\n" +
                    "author:{" +
                        "\"k1\":\"v1\"," +
                        "\"k2\":\"v2\"," +
                        "\"k3\":\"v3\"" +
                    "}" +
                "}";

        CustomSocket socket = CustomSocket.decode(src);
        System.out.println("decoding success");
        String encoded = socket.encode();
        System.out.println("encoding success");
        System.out.println("<socket>\n" + socket.toString());

        CustomSocket copy = socket.copyWith(null, Type.DELETE, null, "/hello/world", null);
        System.out.println("<copy>\n" + copy.toString());
    }
}
