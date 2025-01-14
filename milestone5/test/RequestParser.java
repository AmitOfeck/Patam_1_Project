package test;
import java.io.BufferedReader;
import java.util.Map;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;

public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
    RequestInfo requestInfo = null;
    Scanner scanner = new Scanner(reader);
    
    String firstLine = scanner.nextLine();
    String method = firstLine.split(" ")[0];
    String url = firstLine.split(" ")[1];
    
    String[] urlParts = url.split("/");
    int totalParts = urlParts.length;
    String[] pathSegments = new String[totalParts - 1];
    
    for (int i = 1; i < totalParts; i++) {
        pathSegments[i - 1] = urlParts[i];
    }
    
    pathSegments[totalParts - 2] = pathSegments[totalParts - 2].split("\\?")[0];
    String[] queryParams = url.split("\\?")[1].split("&");
    int queryParamCount = queryParams.length;
    
    Map<String, String> queryParamsMap = new HashMap<>();
    for (int i = 0; i < queryParamCount; i++) {
        String[] keyValue = queryParams[i].split("=");
        queryParamsMap.put(keyValue[0], keyValue[1]);
    }
    
    String headerLine;
    while (!scanner.nextLine().isEmpty());
    
    while ((headerLine = scanner.nextLine()) != null && !headerLine.isEmpty()) {
        String[] keyValue = headerLine.split("=");
        queryParamsMap.put(keyValue[0], keyValue[1]);
    }
    
    byte[] bodyBytes = null;
    String bodyLine = scanner.nextLine() + "\n";
    bodyBytes = bodyLine.getBytes();

    requestInfo = new RequestInfo(method, url, pathSegments, queryParamsMap, bodyBytes);
    scanner.close();
    return requestInfo;
}


    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}