package protocol;

import java.util.HashMap;
import java.util.Map;

public class Protocol {
    // Raw String에서 Header와 Body를 분리
    static public Map<String, String> splitHeaderBody(String msg) throws Exception {
        int mid = msg.indexOf("}&{");
        int headerStart = 1;
        int headerEnd = mid;
        int bodyStart = mid + 3;
        int bodyEnd = msg.length() - 1;

        if (mid == -1) {
            throw new Exception("syntax error");
        }

        String header = msg.substring(headerStart, headerEnd);
        String body = msg.substring(bodyStart, bodyEnd);

        return Map.of("header", header, "body", body);
    }

    // {} 안에 있는 데이터 파싱
    static public Map<String, String> decodeContent(String rawHeader) {
        String[] parts = rawHeader.split("\\\\n");
        Map<String, String> result = new HashMap<>(Map.of());
        for (String part : parts) {
            if (part.isBlank() || !part.contains(":")) {
                continue;
            }
            String key = part.split(":")[0].trim();
            String value = part
                    .replaceFirst(key + ":", "")
                    .trim()
                    .replace("\\\\n", "");
            result.put(key, value);
        }
        return result;
    }
}
