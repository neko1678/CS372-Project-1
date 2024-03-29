/*
Neal Kornreich
CS372 -Fall 2019
Client side of chat application
11/03/2019
 */

#include <iostream>
#include <sys/socket.h>
#include <netdb.h>
#include <cstring>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>


/*Taken directly from Beej Guide with some minor modifications
 * Takes character array of hostname and port number
 * Gets a struct with the address information from the system
*/
struct addrinfo * getAddressInfo(char* hostName, char* portNumber) {
    int status;
    struct addrinfo hints;
    struct addrinfo *servinfo;  // will point to the results

    memset(&hints, 0, sizeof hints); // make sure the struct is empty
    hints.ai_family = AF_UNSPEC;     // don't care IPv4 or IPv6
    hints.ai_socktype = SOCK_STREAM; // TCP stream sockets

    printf("Getting address info...\n");
    status = getaddrinfo(hostName, portNumber, &hints, &servinfo);

    if (status != 0) {
        fprintf(stderr, "getaddrinfo error: %s\n", gai_strerror(status));
        exit(1);
    }

    return servinfo;
}

/* Taken from Beej Guide
 * Takes the struct addrinfo as input
 * Returns an int with socket descriptor
 * Exits with error if invalid descriptor is returned
*/
int createSocket(struct addrinfo* addressInfo){
    printf("Creating socket...\n");
    int des = socket(addressInfo->ai_family, addressInfo->ai_socktype, addressInfo->ai_protocol);

    if(des == -1){
        fprintf(stderr,"Failed to create socket");
        exit(1);
    }

    printf("Socket created...\n");
    return des;
}

/* From Beej guide
 * Takes struct addrinfo and socket description
 * Exits with error if fails
*/
void connectToHost(struct addrinfo* addressInfo, int socketDes){
    printf("Connecting to host...\n");

    int status = connect(socketDes, addressInfo->ai_addr, addressInfo->ai_addrlen);

    if(status == -1){
        fprintf(stderr,"Failed to connect to host");
        exit(1);
    }

    printf("Connected to host\n");
}

/*
 * Takes username buffer and socket description
 * writes message to server
 */
void writeMessage(std::string username, char* outputBuffer, int socketDes){
    std::string output;

    getline(std::cin, output);

    /*
     * Checks for quit message
     * If is - notify server, close socket, exit
     */
    if(output.compare("\\quit") == 0){
        output.append("\n");
        strcpy(outputBuffer, output.c_str());
        send(socketDes, outputBuffer, strlen(outputBuffer), 0);
        printf("Closing program...");
        close(socketDes);
        exit(0);
    }
    else{
        output.append("\n");
        std::string outputWithHandle = username;
        outputWithHandle.append(output);
        strcpy(outputBuffer, outputWithHandle.c_str());
        send(socketDes, outputBuffer, strlen(outputBuffer), 0);
    }
}

/*
 * Primary function in which chat occurs
 * Takes socket description and username as inputs
 */
void chatWithHost(int socketDes, std::string username){
    std::string output;
    char outputBuffer[513];
    memset(outputBuffer, 0, sizeof(outputBuffer));
    char inputBuffer[513];
    memset(inputBuffer, 0, sizeof(inputBuffer));

    username.append("> ");

    while(1){
        printf("%s", username.c_str());

        writeMessage(username, outputBuffer, socketDes);

        /*
        * Waits to recieve messages
        * Checks for quit message - if so close and exit
        * Print message
        */
        recv(socketDes, inputBuffer, sizeof(inputBuffer), 0);
        if(strstr(inputBuffer, "\\quit") != 0){
            printf("Server quit. Closing program...");
            close(socketDes);
            exit(0);
        }
        else{
            printf("%s", inputBuffer);
        }

        memset(outputBuffer, 0 ,sizeof(outputBuffer));
        memset(inputBuffer, 0, sizeof(inputBuffer));
    }
}


/*
 * Gets handle from command line
 * Returns string of handle
 */
std::string getHandle(){
    printf("Enter username: ");

    std::string username;
    getline(std::cin, username);

    return username;
}

int main(int argc, char *argv[]) {

    //Quit if required arguments aren't included
    if(argc != 3){
        fprintf(stderr,"Invalid arguments");
        exit(1);
    }


    //Set up and connect
    struct addrinfo* res = getAddressInfo(argv[1], argv[2]);
    int socketDes = createSocket(res);
    connectToHost(res, socketDes);

    std::string handle = getHandle();

    chatWithHost(socketDes, handle);

    return 0;
}
