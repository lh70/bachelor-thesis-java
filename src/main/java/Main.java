import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String ip = "127.0.0.1";
        int port = 8090;

        String sensorKind = "dummy";

        Client python = new Client(ip, port);

        python.removeAssignment();
        python.distributeAssignment(sensorKind);

        Connection pipeline = python.requestPipeline();

        while(true) {
            System.out.println(pipeline.receive());
        }
    }

}
