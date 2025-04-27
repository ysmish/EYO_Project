#include <ConsoleInputHandler.h>
#include <InputHandler.h>
#include <string>
#include <regex>
#include <iostream>
#include <vector>

fistLine ConsoleInputHandler::getFistLine() {
    std::regex pattern(R"((\d+)\s(\d+)(?:\s\d+)*$)"); // Regular expression to match alphanumeric characters
    std::smatch matches;
    std::string fistLine;
    std::cin >> fistLine; // Read the first line of input
    while (!std::regex_match(fistLine, matches, pattern)) { // Check if the input matches the pattern
        std::cin >> fistLine; // Read the first line of input again
    }
    size_t size = std::stoi(matches[0].str()); // Extract the size from the input
    std::vector<size_t> rounds; // Create a vector to store the number of rounds
    for (size_t i = 1; i < sizeof(matches); ++i) {
        rounds.push_back(std::stoi(matches[i].str())); // Add the number of rounds to the vector
    }
    return {size, rounds}; // Return the size and rounds as a fistLine object
}

output ConsoleInputHandler::getInput() {
    std::regex pattern(R"((\d+)\s+(\S+)$)"); // Regular expression to match alphanumeric characters
    std::smatch matches;
    std::string input;
    std::cin >> input; // Read the input
    while (!std::regex_match(input, matches, pattern)) { // Check if the input matches the pattern
        std::cin >> input; // Read the input again
    }
    std::string command = matches[0].str(); // Extract the command from the input
    std::string url = matches[1].str(); // Extract the URL from the input
    return {command, url}; // Return the command and URL as an output object
}

