import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatServe {

    public static void main(String[] args) {
        int portNumber = getPortNumber(args);
        String handle = getHandle();

        chat(portNumber, handle);
    }

    /*
    From https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.htm
    Creates socket, waits for a client to connect, begins chat
     */
    private static void chat(int portNumber, String handle){
        while(true) {
            try (ServerSocket serverSocket = new ServerSocket(portNumber);
                 Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(clientSocket.getInputStream()))) {

                System.out.println("Connected to client");


                handle += "> ";

                String inputLine;
                String outputLine;
                Scanner scanner = new Scanner(System.in);

                while (true) {

                    System.out.println("Rerunnning loop");
                    if ((inputLine = in.readLine()) != null) {
                        System.out.println("In recieved");
                        //If quit message is received terminate program
                        if (inputLine.contains("\\quit")) {
                            System.out.println("Client has disconnected.");
                            break;
                        } else {
                            System.out.println(inputLine);
                        }
                    }

                    System.out.print(handle);
                    outputLine = scanner.next();
                    if (outputLine.equals("\\quit")) {
                        System.out.println("Closing program...");
                        out.println(outputLine);
                        System.exit(0);
                    } else {
                        System.out.println("Sending message: " + outputLine);
                        out.println(handle + outputLine);
                    }
                }


            } catch (IOException e) {
                System.err.println("IOException");
                //Terminates the program
                System.exit(1);
            }
        }
    }

    /*
    Retrieves port number from first arg
    If not there request input from terminal
     */
    private static int getPortNumber(String[] args){
        int portNumber;

        if(args == null || args.length < 1) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Please enter port number: ");
            portNumber = scanner.nextInt();
        }
        else {
            try {
                portNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                Scanner scanner = new Scanner(System.in);
                System.err.println("Invalid Port Number");
                System.out.print("Please enter port number: ");
                portNumber = scanner.nextInt();
            }
        }

        return portNumber;
    }


    private static String getHandle(){
        String handle;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter username: ");
        handle = scanner.next();

        while (handle.length()>10){
            System.out.println("Username is too long. Please enter a valid username: ");
            handle = scanner.next();
        }

        return handle;
    }
}


