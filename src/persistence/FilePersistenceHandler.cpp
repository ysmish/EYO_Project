#include "FilePersistenceHandler.h"
#include "PersistenceHandler.h"
#include <fstream>
#include <system_error>

FilePersistenceHandler::FilePersistenceHandler(const std::string& filename) : filename(filename) {
}

void FilePersistenceHandler::save(const std::vector<bool>& bitArray) {
    std::ofstream file(filename, std::ios::out | std::ios::trunc);

    if (!file.is_open()) {
        std::error_code ec(errno, std::system_category());
        throw std::system_error(ec, "Failed to open file for writing");
    }

    for (bool bit : bitArray) {
        file << (bit ? '1' : '0');
    }

    if (!file.good()) {
        std::error_code ec(errno, std::system_category());
        throw std::system_error(ec, "Failed to write to file");
    }
}

std::vector<bool> FilePersistenceHandler::load() {
    std::ifstream file(filename);
    std::vector<bool> data;
    if (file.is_open()) {
        char bit;
        while (file >> bit) {
            data.push_back(bit == '1');
        }
        file.close();
    }
    return data;
}
