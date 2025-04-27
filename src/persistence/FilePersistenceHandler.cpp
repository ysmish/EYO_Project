#include "FilePersistenceHandler.h"
#include <fstream>
#include <sstream>
#include <stdexcept>

FilePersistenceHandler::FilePersistenceHandler(const std::string &filename) : filename(filename) {}

void FilePersistenceHandler::save(const std::vector<bool> &bitArray) {
    std::ofstream file(filename);
    if (!file.is_open()) {
        throw std::runtime_error("Could not open file for saving: " + filename);
    }

    for (bool bit : bitArray) {
        file << bit;
    }

    file.close();
}

std::vector<bool> FilePersistenceHandler::load() {
    std::vector<bool> bitArray;
    std::ifstream file(filename);

    if (!file.is_open()) {
        throw std::system_error(std::error_code(), "Could not open file for loading: " + filename);
    }

    char bit;
    while (file >> bit) {
        if (bit == '0') {
            bitArray.push_back(false);
        } else if (bit == '1') {
            bitArray.push_back(true);
        } else {
            throw std::ios_base::failure("Invalid character in file: " + filename);
        }
    }

    file.close();
    return bitArray;
}