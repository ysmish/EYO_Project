#include "FilePersistenceHandler.h"
#include <fstream>
#include <sstream>
#include <stdexcept>
#include <regex>
#include <filesystem> 

FilePersistenceHandler::FilePersistenceHandler(const std::string &filename) : filename(filename) {}

void FilePersistenceHandler::save(const std::vector<std::string>& blacklistedURLs) {
    // Attempt to open the file for writing
    std::ofstream file(filename); // Use std::ofstream directly

    // Check if the file stream opened successfully
    if (!file) {
        throw std::system_error(std::error_code(), "Could not open file for saving: " + filename);
    }

    // Save each URL on a new line
    for (const std::string& url : blacklistedURLs) {
        file << url << std::endl;
    }

    file.close();
}

std::vector<std::string> FilePersistenceHandler::load() {
    std::vector<std::string> blacklistedURLs;

    std::ifstream file(filename);

    // Create a new file if it does not exist
    if (!file) {
        std::ofstream newFile(filename);
        newFile.close(); // Close the file after creating it
    }

    std::string line;
    std::regex urlCheck(R"(^((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$)");

    while (std::getline(file, line)) {
        if (!std::regex_match(line, urlCheck)) {
            throw std::ios_base::failure("Invalid URL format " + line + " in file: " + filename);
        }
        blacklistedURLs.push_back(line);
    }

    file.close();
    return blacklistedURLs;
}
