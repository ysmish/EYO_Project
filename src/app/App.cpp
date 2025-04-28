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

App::App() {
}

void App::run() {
    InputHandler *inputHandler = new ConsoleInputHandler(); // Create a ConsoleInputHandler object

    // Main loop to run the application
    fistLine firstLine = inputHandler->getFistLine(); // Get the first line of input
    std::vector<std::unique_ptr<HashFunction>> hashFunctions; // Create a vector of StdHashFunction objects
    for (size_t i = 0; i < firstLine.rounds.size(); ++i) { // Loop through the number of rounds
        hashFunctions.push_back(std::make_unique<StdHashFunction>(firstLine.rounds[i])); // Add StdHashFunction objects to the vector
    }
    // Create a FilePersistenceHandler object
    std::unique_ptr<PersistenceHandler> persistenceHandler = std::make_unique<FilePersistenceHandler>("bloom_filter_data.txt");

    // Create the BloomFilter as a unique pointer
    std::unique_ptr<BloomFilter> bloomFilter = std::make_unique<BloomFilter>(firstLine.size, std::move(hashFunctions), std::move(persistenceHandler));

    std::map<std::string, ICommand*> commands; // Create a map to hold the commands
    commands["1"] = new InsertCommand(bloomFilter.get()); // Pass a pointer to InsertCommand
    commands["2"] = new IsFilteredCommand(bloomFilter.get()); // Pass a pointer to IsFilteredCommand
    while(true) { // Infinite loop to read input
        output input = inputHandler->getInput(); // Get the input from the user
        try {
            if (commands.find(input.command) == commands.end()) { // Check if the command exists in the map
                std::cerr << "Invalid command: " << input.command << std::endl; // Print an error message if the command is invalid
                continue; // Continue to the next iteration
            }
            commands[input.command]->execute(input.url); // Execute the command with the provided URL
        } catch (const std::exception& e) { // Catch other exceptions
            continue; // Ignore the exception and continue to the next iteration
        }
    }
}