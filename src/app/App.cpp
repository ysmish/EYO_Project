//
// Created by A on 27/4/2025.
//

#include "App.h"
#include <command.h>
#include <FilePersistenceHandler.h>
#include <StdHashFunction.h>
#include <BloomFilter.h>
#include <ConsoleInputHandler.h>
#include <InputHandler.h>
#include <map>
#include <vector>
#include <iostream>
#include <string>
#include <regex>

App::App(std::map<std::string, ICommand> commands) : commands(std::move(commands)) {
    // Constructor implementation
    // Initialize the App with the provided commands
    // The commands map can be used to execute different commands in the application
}

void App::run() {
    // Main loop to run the application
    InputHandler *inputHandler = new ConsoleInputHandler(); // Create a ConsoleInputHandler object
    fistLine firstLine = inputHandler->getFistLine(); // Get the first line of input
    std::vector<std::unique_ptr<HashFunction>> hashFunctions; // Create a vector of StdHashFunction objects
    for (size_t i = 0; i < firstLine.rounds.size(); ++i) { // Loop through the number of rounds
        hashFunctions.push_back(std::make_unique<StdHashFunction>(firstLine.rounds[i])); // Add StdHashFunction objects to the vector
    }
    std::unique_ptr<PersistenceHandler> persistenceHandler = std::make_unique<FilePersistenceHandler>("bloom_filter_data.txt"); // Create a FilePersistenceHandler object
    BloomFilter bloomFilter(firstLine.size, std::move(hashFunctions), std::move(persistenceHandler)); // Create a BloomFilter object

    while(true) { // Infinite loop to read input
        output input = inputHandler->getInput(); // Get the input from the user
        try {
            commands[input.command].execute(input.url); // Execute the command with the provided URL
        } catch (const std::exception& e) { // Catch other exceptions
            std::cerr << "No such command: " << input.command << std::endl; // Print an error message if the command is not found
        }
    }
}