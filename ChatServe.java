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
        try(ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()))){

            System.out.println("Connected to client");


            handle += "> ";

            String inputLine;
            String outputLine;
            Scanner scanner = new Scanner(System.in);
            
            while(true){
                
                if ((inputLine = in.readLine()) != null) {
                    //If quit message is received terminate program
                    if (inputLine.equals("\\quit")) {
                        System.out.println("Client has disconnected. Closing program...");
                        System.exit(0);
                    }
                    else {
                        System.out.println(inputLine);
                        System.out.println(handle);
                    }
                }
                
                outputLine = scanner.next();
                if (outputLine.equals("\\quit")) {
                    System.out.println("Closing program...");
                    out.write("\\quit");
                    System.exit(0);
                }
                else{
                    out.write(outputLine);
                    System.out.print(handle);
                }
                
            }



        }
        catch (IOException e){
            System.err.println("IOException");
            //Terminates the program
            System.exit(1);
        }

    }

    private static void readMessages(BufferedReader in, String handle, Lock lock) throws IOException{
        String inputLine;

        //Used to clear user's handle from screen when new message arrives from client
        String clearMessage = "";
        for(int i = 0; i<handle.length(); i++){
            clearMessage += "\\b";
        }

        while (true) {
            if ((inputLine = in.readLine()) != null) {
                lock.lock();

                System.out.print(clearMessage);

                //If quit message is received terminate program
                if (inputLine.equals("\\quit")) {
                    System.out.println("Client has disconnected. Closing program...");
                    System.exit(0);
                }
                else {
                    System.out.println(inputLine);
                    System.out.println(handle);
                }

                lock.unlock();
            }
        }
    }

    private static void writeMessages(PrintWriter out, String handle, Lock lock) throws IOException{
        Scanner scanner = new Scanner(System.in);
        String outputLine;

        while(true){
            outputLine = scanner.next();

            lock.lock();
            if (outputLine.equals("\\quit")) {
                System.out.println("Closing program...");
                out.write("\\quit");
                System.exit(0);
            }
            else{
                out.write(outputLine);
                System.out.print(handle);
            }
            lock.unlock();
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
            System.out.println("Please enter port number: ");
            portNumber = scanner.nextInt();
        }
        else {
            try {
                portNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                Scanner scanner = new Scanner(System.in);
                System.err.println("Invalid Port Number");
                System.out.println("Please enter port number: ");
                portNumber = scanner.nextInt();
            }
        }

        return portNumber;
    }


    private static String getHandle(){
        String handle;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter username: ");
        handle = scanner.next();

        while (handle.length()>10){
            System.out.println("Username is too long. Please enter a valid username: ");
            handle = scanner.next();
        }

        return handle;
    }
}


