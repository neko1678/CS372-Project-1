#include <iostream>
#include <iostream>
#include <sys/socket.h>
#include <netdb.h>
#include <cstring>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>


/*Taken directly from Beej Guide with some minor modifications
 * Gets a struct with the address information from the system
 *
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
 * Takes struct addrinfo
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

void chatWithHost(int socketDes, addrinfo* res){
    /*char* msg = "Test";
    while(1){
        send(socketDes,msg, strlen(msg), 0);
        usleep(1);
        }*/
}

int main(int argc, char *argv[]) {

    //Quit if required arguments aren't included
    if(argc != 3){
        fprintf(stderr,"Invalid arguments");
        exit(1);
    }

    //Set up user name
    char userName[10];
    printf("Enter 10 character username: ");
    //scanf("%s", userName);
    printf("Username is: %s\n", userName);

    //Set up and connect
    struct addrinfo* res = getAddressInfo(argv[1], argv[2]);
    int socketDes = createSocket(res);
    connectToHost(res, socketDes);

    chatWithHost(socketDes, res);

    return 0;
}
