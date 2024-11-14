package server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Protocol {
    static public Map<String, String> splitHeaderBody(String msg) throws Exception {
        int mid = msg.indexOf("}&{");
        int headerStart = 1;
        int headerEnd = mid - 1;
        int bodyStart = mid + 3;
        int bodyEnd = msg.length() - 1;

        if (mid == -1) {
            throw new Exception("syntax error");
        }

        String header = msg.substring(headerStart, headerEnd);
        String body = msg.substring(bodyStart, bodyEnd);

        return Map.of("header", header, "body", body);
    }

    static public Map<String, String> decodeContent(String rawHeader) {
        String[] parts = rawHeader.split("\n");
        Map<String, String> result = new HashMap<>(Map.of());
        for (String part : parts) {
            if (part.isBlank() || !part.contains(":")) {
                continue;
            }
            String key = part.split(":")[0].trim();
            String value = part
                    .replaceFirst(key + ":", "")
                    .trim()
                    .replace("\n", "");
            result.put(key, value);
        }
        return result;
    }
}
