//
// Created by A on 27/4/2025.
//

#include "App.h"
#include <command.h>
#include <FilePersistenceHandler.h>
#include <StdHashFunction.h>
#include <BloomFilter.h>
#include <TCPInputHandler.h>
#include <InputHandler.h>
#include <InsertCommand.h>
#include <IsFilteredCommand.h>
#include <sys/socket.h>
#include <map>
#include <memory>
#include <vector>
#include <sys/socket.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <iostream>
#include <string>
#include <regex>

App::App(size_t size,
        std::vector<std::unique_ptr<HashFunction>> hashFunction,
        std::unique_ptr<PersistenceHandler> persistenceHandler, size_t port) : port(port) {
    if (!persistenceHandler) {
        throw std::invalid_argument("Persistence handler cannot be null"); // Check if persistence handler is null
    }
    if (size == 0) {
        throw std::invalid_argument("Size cannot be zero"); // Check if size is zero
    }
    if (hashFunction.empty()) {
        throw std::invalid_argument("Hash function cannot be empty"); // Check if hash function is empty
    }
    this->bloomFilter = std::make_unique<BloomFilter>(size, std::move(hashFunction), std::move(persistenceHandler)); // Create a unique pointer to BloomFilter
    commands["POST"] = new InsertCommand(bloomFilter.get()); // Pass a pointer to InsertCommand
    commands["GET"] = new IsFilteredCommand(bloomFilter.get()); // Pass a pointer to IsFilteredCommand
}

void App::run() {
    // Create a socket for the server
    int serverSock = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSock < 0) {
        perror("error creating socket");
    }
    // Set the socket options
    struct sockaddr_in sin;
    memset(&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_addr.s_addr = INADDR_ANY;
    sin.sin_port = htons(port);
    // Bind the socket to the address and port
    if (bind(serverSock, (struct sockaddr *) &sin, sizeof(sin)) < 0) {
        perror("error binding socket");
    }
    // Set the socket to listen for incoming connection
    if (listen(serverSock, 5) < 0) {
        perror("error listening to a socket");
    }
    // Accept incoming connection
    struct sockaddr_in client_sin;
    unsigned int addr_len = sizeof(client_sin);
    int client_sock = accept(serverSock,  (struct sockaddr *) &client_sin,  &addr_len);

    if (client_sock < 0) {
        perror("error accepting client");
    }
    // handle the client connection (buisness logic)
    handleClient(client_sock); // Handle the client connection
    close(serverSock); // Close the server socket
}

void App::handleClient(int socket) {
    TCPInputHandler inputHandler(socket);
    while(true) { // Infinite loop to read input
        output input = inputHandler.getInput(); // Get the input from the user
        if (input.command == "" && input.url == "") { // Check if the command and URL are empty
            break; // Break the loop if both are empty
        }
        try {
            if (commands.find(input.command) == commands.end()) { // Check if the command exists in the map
                std::string answer = "400 Bad Request\n"; // Set the answer to bad request
                send(socket, answer.c_str(), answer.length(), 0); // Send a bad request response
                continue; // Continue to the next iteration
            }
            std::string answer = commands[input.command]->execute(input.url); // Execute the command with the provided URL
            send(socket, answer.c_str(), answer.length(), 0); // Send the response to the client
            send(socket, "\n", 1, 0); // Send a newline character
        } catch (const std::exception& e) { // Catch other exceptions
            std::cerr << "Error: " << e.what() << std::endl; // Print the error message
        }
    }

    close(socket); // Close the client socket
}