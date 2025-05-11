#ifndef ASP_PROJECT_TCPINPUHANDLER_H
#define ASP_PROJECT_TCPINPUHANDLER_H
#include "HashFunction.h"

#include <InputHandler.h>
#include <string>
#include <vector>


class TCPInputHandler : public InputHandler {
private:
    int socket; // Socket for TCP connection
    std::string receive(); // receive data from the socket

    void parseInput(); // Method to parse input from the buffer
    void handleCommand(); // Method to handle the command received
public:
    TCPInputHandler(int socket); // Constructor that takes a socket as an argument
    output getInput() override; // Method to get input from the user
    fistLine getFistLine() override; // Method to get the first line of input
};



#endif //ASP_PROJECT_TCPINPUHANDLER_H
