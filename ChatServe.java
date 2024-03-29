/*
Neal Kornreich
CS372 -Fall 2019
Server side of chat application
11/03/2019
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatServe {

    public static void main(String[] args) {
        int portNumber = getPortNumber(args);
        String handle = getHandle();

        chat(portNumber, handle);
    }

    /*
    From https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.htm
    Creates socket, waits for a client to connect, begins chat
    Loops for new connection if client quits
     */
    private static void chat(int portNumber, String handle){
        handle += "> ";
        
        while(true) {
            /*
            Waits for client connection
             */
            try (ServerSocket serverSocket = new ServerSocket(portNumber);
                 Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(clientSocket.getInputStream()))) {

                System.out.println("Connected to client");

                /*
                Chat with client
                 */
                while (true) {

                    //Break if client sent \quit
                    if(!readMessage(in)){
                        break;
                    }

                    System.out.print(handle);

                    sendMessage(out, handle);
                }

            } catch (IOException e) {
                System.err.println("IOException");
                //Terminates the program
                System.exit(1);
            }
        }
    }

    /*
    Returns false if client quit
    Reads socket for clients message
    Prints message
     */
    private static boolean readMessage(BufferedReader in) throws IOException{
        String inputLine = in.readLine();
        if (inputLine != null) {
            //If quit message is received terminate program
            if (inputLine.contains("\\quit")) {
                System.out.println("Client has disconnected.");
                return false;
            } else {
                System.out.println(inputLine);
            }
        }
        return true;
    }

    /*
    Reads message from command line
    Sends to client
    Handles quit message
    */
    private static void sendMessage(PrintWriter out, String handle){
        Scanner scanner = new Scanner(System.in);
        String outputLine = scanner.nextLine();
        if (outputLine.equals("\\quit")) {
            System.out.println("Closing program...");
            out.println(outputLine);
            System.exit(0);
        } else {
            out.println(handle + outputLine);
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

    /*
    Gets username from commandline
    Checks length
     */
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


