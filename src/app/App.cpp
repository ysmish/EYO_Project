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
#include <InsertCommand.h>
#include <IsFilteredCommand.h>
#include <map>
#include <vector>
#include <iostream>
#include <string>
#include <regex>

App::App(std::unique_ptr<InputHandler> inputHandler, std::unique_ptr<PersistenceHandler> persistenceHandler) : inputHandler(std::move(inputHandler)), persistenceHandler(std::move(persistenceHandler)) {
    // Constructor to initialize the App with input and persistence handlers
    if (!this->inputHandler) {
        throw std::invalid_argument("Input handler cannot be null"); // Check if input handler is null
    }
    if (!this->persistenceHandler) {
        throw std::invalid_argument("Persistence handler cannot be null"); // Check if persistence handler is null
    }
}

void App::run() {

    // Main loop to run the application
    fistLine firstLine = inputHandler->getFistLine(); // Get the first line of input
    std::vector<std::unique_ptr<HashFunction>> hashFunctions; // Create a vector of StdHashFunction objects
    for (size_t i = 0; i < firstLine.rounds.size(); ++i) { // Loop through the number of rounds
        hashFunctions.push_back(std::make_unique<StdHashFunction>(firstLine.rounds[i])); // Add StdHashFunction objects to the vector
    }

    // Create the BloomFilter as a unique pointer
    std::unique_ptr<BloomFilter> bloomFilter = std::make_unique<BloomFilter>(firstLine.size, std::move(hashFunctions), std::move(persistenceHandler));

    commands["1"] = new InsertCommand(bloomFilter.get()); // Pass a pointer to InsertCommand
    commands["2"] = new IsFilteredCommand(bloomFilter.get()); // Pass a pointer to IsFilteredCommand
    while(true) { // Infinite loop to read input
        output input = inputHandler->getInput(); // Get the input from the user
        try {
            if (commands.find(input.command) == commands.end()) { // Check if the command exists in the map
                continue; // Continue to the next iteration
            }
            commands[input.command]->execute(input.url); // Execute the command with the provided URL
        } catch (const std::exception& e) { // Catch other exceptions
            std::cerr << "Error: " << e.what() << std::endl; // Print the error message
        }
    }
}