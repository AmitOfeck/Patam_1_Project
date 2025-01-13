package test;

//import test.Servlets.Servlet;


public interface HTTPServer extends Runnable{
    public void addServlet(String httpCommanmd, String uri, Servlet s);
    public void removeServlet(String httpCommanmd, String uri);
    public void start();
    public void close();
}
