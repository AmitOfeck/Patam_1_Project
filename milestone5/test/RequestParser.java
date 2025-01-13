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
        String uriPath = firstLine.split(" ")[1]; 
        String method = firstLine.split(" ")[0]; 
        //System.out.println("Method: " + method);
        //System.out.println("URI Path: " + uriPath);

        String buff = uriPath;
        int length = buff.split("/").length;
        String[] pathSegments = new String[length - 1];

        for (int i = 1; i < length; i++) {
            pathSegments[i - 1] = buff.split("/")[i];
        }
        pathSegments[length - 2] = pathSegments[length - 2].split("\\?")[0];

        //System.out.println("Path Segments: ");
        for (String segment : pathSegments) {
            //System.out.println(segment);
        }

        String[] queryParams = uriPath.split("\\?").length > 1 ? uriPath.split("\\?")[1].split("&") : new String[0];
        int queryLength = queryParams.length;
        Map<String, String> parametersMap = new HashMap<>();

        //System.out.println("Query Parameters: ");
        for (int i = 0; i < queryLength; i++) {
            String key = queryParams[i].split("=")[0];
            String value = queryParams[i].split("=")[1];
            parametersMap.put(key, value);
            //System.out.println(key + "=" + value);
        }

        String line;
        while (!scanner.nextLine().isEmpty()); 
        while ((line = scanner.nextLine()) != null && !line.isEmpty()) {
            String key = line.split("=")[0];
            String value = line.split("=")[1];
            parametersMap.put(key, value);
            //System.out.println("Header: " + key + "=" + value);
        }

        byte[] contentBytes = null;
        String contentLine = scanner.nextLine() + "\n";
        contentBytes = contentLine.getBytes();

        requestInfo = new RequestInfo(method, uriPath, pathSegments, parametersMap, contentBytes);
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
