import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class ChatServe {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter port number:");
        //Waits for int to be entered from command line - !not input safe
        int portNumber = scanner.nextInt();
        System.out.println(portNumber);
        //TODO NO must be command line

        //From https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
        try(ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()))){

            System.out.println("The server is ready to receive");


            String inputLine;
            while (true) {
                //Check if is null if so check check again
                if((inputLine = in.readLine()) == null){
                    continue;
                }

                //Write message to receiving client
                out.println(inputLine);

                /*If quit message is received terminate program
                 */
                if (inputLine.equals("\\quit")) {
                    //TODO you sure to close the server?
                    System.exit(0);
                }
            }

        }
        catch (IOException e){
            System.err.println("IOException");
            //Terminates the program
            System.exit(1);
        }
    }
}
