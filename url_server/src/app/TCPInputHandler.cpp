#include <TCPInputHandler.h>
#include <InputHandler.h>
#include <string>
#include <regex>
#include <iostream>
#include <vector>
#include <sys/socket.h>
#include <fcntl.h>

TCPInputHandler::TCPInputHandler(int socket) : socket(socket) {
    // check if socket is connected
    if (fcntl(socket, F_GETFD) == -1) {
        std::cerr << "Socket is not connected" << std::endl;
        return;
    }
}

std::string TCPInputHandler::receive() {
    std::string result;
    char c;
    // Receive data from the socket until a newline character is found
    while (true) {
        int bytesReceived = recv(socket, &c, 1, 0);
        if (bytesReceived <= 0) {
            std::cerr << "Error receiving data" << std::endl;
            return "";
        }
        if (c == '\n') {
            break;
        }
        result += c;
    }
    return result;
}

output TCPInputHandler::getInput() {
    std::string input = receive();
    if (input.empty()) {
        return {};
    }
    size_t delimiterPos = input.find(' ');
    if (delimiterPos == std::string::npos) return {input, ""};
    std::string command = input.substr(0, delimiterPos);
    std::string url = input.substr(delimiterPos + 1);
    return {command, url};
}

// because the first line is not received from the socket, we need to implement it differently
fistLine TCPInputHandler::getFistLine() {
    return {100, {1, 2, 3}}; // Default values for size and rounds
}

