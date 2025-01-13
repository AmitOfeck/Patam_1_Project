package test;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.*;
//import test.Servlets.Servlet;

public class MyHTTPServer extends Thread implements HTTPServer {

    private ConcurrentHashMap<String, Servlet> getRequestHandlers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> deleteRequestHandlers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Servlet> postRequestHandlers = new ConcurrentHashMap<>();
    private ExecutorService requestProcessingPool;
    private volatile boolean serverRunningFlag = false;
    private ServerSocket mainServerSocket;
    private final int maxThreadPoolSize;
    private final int serverPort;


    public MyHTTPServer(int serverPort, int maxThreadPoolSize) {
        this.requestProcessingPool = Executors.newFixedThreadPool(maxThreadPoolSize);
        this.serverPort = serverPort;
        this.maxThreadPoolSize = maxThreadPoolSize;
        // System.out.println("HTTP Server initialized with port: " + serverPort + " and thread pool size: " + maxThreadPoolSize);
    }

    public void addServlet(String httpMethod, String endpointUri, Servlet servletHandler) {
        if (endpointUri == null || servletHandler == null) {
            return;
        }
        httpMethod = httpMethod.toUpperCase();

        // System.out.println("Adding servlet for HTTP method: " + httpMethod + ", URI: " + endpointUri);
        switch (httpMethod) {
            case "POST":
                postRequestHandlers.put(endpointUri, servletHandler);
                break;
            case "DELETE":
                deleteRequestHandlers.put(endpointUri, servletHandler);
                break;
            case "GET":
                getRequestHandlers.put(endpointUri, servletHandler);
                break;
        }
    }

    public void removeServlet(String httpMethod, String endpointUri) {
        if (endpointUri == null) {
            return;
        }
        httpMethod = httpMethod.toUpperCase();
        // System.out.println("Removing servlet for HTTP method: " + httpMethod + ", URI: " + endpointUri);
        switch (httpMethod) {
            case "POST":
                postRequestHandlers.remove(endpointUri);
                break;
            case "DELETE":
                deleteRequestHandlers.remove(endpointUri);
                break;
            case "GET":
                getRequestHandlers.remove(endpointUri);
                break;
        }
    }

    public void run() {
        try (ServerSocket temporaryServerSocket = new ServerSocket(serverPort)) {
            this.mainServerSocket = temporaryServerSocket;
            mainServerSocket.setSoTimeout(1000);

            // System.out.println("Server is running on port: " + serverPort);
            while (!serverRunningFlag) {
                try {
                    Socket incomingClientSocket = mainServerSocket.accept();

                    // System.out.println("Accepted connection from client: " + incomingClientSocket.getRemoteSocketAddress());
                    requestProcessingPool.submit(() -> {
                        try {
                            Thread.sleep(125); 
                            BufferedReader clientRequestReader = createRequestBufferedReader(incomingClientSocket);
                            RequestParser.RequestInfo parsedRequestInfo = RequestParser.parseRequest(clientRequestReader);
                            ConcurrentHashMap<String, Servlet> relevantServletMap;

                            if (parsedRequestInfo != null) {
                                // System.out.println("Processing request: " + parsedRequestInfo);
                                switch (parsedRequestInfo.getHttpCommand()) {
                                    case "POST":
                                        relevantServletMap = postRequestHandlers;
                                        break;
                                    case "DELETE":
                                        relevantServletMap = deleteRequestHandlers;
                                        break;
                                    case "GET":
                                        relevantServletMap = getRequestHandlers;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unsupported HTTP method: " + parsedRequestInfo.getHttpCommand());
                                }

                                Servlet selectedServlet = null;
                                 String bestMatchingUri = "";
                                for (Map.Entry<String, Servlet> servletEntry : relevantServletMap.entrySet()) {
                                    if (parsedRequestInfo.getUri().startsWith(servletEntry.getKey()) && servletEntry.getKey().length() > bestMatchingUri.length()) {
                                        bestMatchingUri = servletEntry.getKey();
                                        selectedServlet = servletEntry.getValue();
                                    }
                                }

                                if (selectedServlet != null) {
                                    // System.out.println("Handling request with servlet for URI: " + bestMatchingUri);
                                    selectedServlet.handle(parsedRequestInfo, incomingClientSocket.getOutputStream());
                                }
                            }
                            clientRequestReader.close();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                incomingClientSocket.close();
                                // System.out.println("Closed connection with client.");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    if (serverRunningFlag) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedReader createRequestBufferedReader(Socket clientSocket) throws IOException {
        InputStream rawInputStream = clientSocket.getInputStream();
        int availableDataBytes = rawInputStream.available();
        byte[] temporaryBuffer = new byte[availableDataBytes];
        int actualBytesRead = rawInputStream.read(temporaryBuffer, 0, availableDataBytes);

        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(temporaryBuffer, 0, actualBytesRead)
                )
        );
    }

    public void start() {
        serverRunningFlag = false;
        new Thread(() -> run()).start();
        // System.out.println("Server thread started.");
    }

    public void close() {
        serverRunningFlag = true;
        requestProcessingPool.shutdownNow();
        // System.out.println("Server is shutting down...");
    }

    public Object getThreadPool() {
        return requestProcessingPool;
    }
}
