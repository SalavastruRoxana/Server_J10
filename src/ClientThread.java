import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ClientThread extends Thread {
    private Socket socket = null ;
    private boolean stopGame = false; //un thread va verifica in paralel daca
                                      //vreun client s-a decis sa opreasca serverul

    public ClientThread (Socket socket) throws SocketException {
        this.socket = socket ;
    }

    public void run () {
        try {
            boolean time = true;//trebuie sa ma asigur ca fiecare client este
                                //activ, sa trimita cereri din 2 in 2 min sa nu
                                //fie deconectat, voi avea nevoie de un thread in
                                //paralel sa contorizeze timpul
            while(time == true) {
                // Get the request from the input stream: client → server
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String request = in.readLine();
                if(request.compareTo("exit") == 0)
                    time = false;
                // Send the response to the oputput stream: server → client

                if(request.compareTo("close") == 0)
                {
                    time = false;
                    setStopGame(true);
                }

                PrintWriter out = new PrintWriter(socket.getOutputStream());
                String raspuns = "Hello " + request + "!";
                out.println(raspuns);
                out.flush();
            }
        } catch (IOException e) {
            if (e.getMessage() ==  "Connection reset")
                System.out.println("un client deconectat.. :(");
            else
                System.err.println("Communication error... " + e);
        } finally {
            try {
                socket.close(); // or use try-with-resources
                System.out.println("un client a transmis exit, am inchis conexiunea cu el");
            } catch (IOException e) { System.err.println (e); }
        }

    }

    private void setStopGame(boolean value) {
        stopGame = value;
    }
}