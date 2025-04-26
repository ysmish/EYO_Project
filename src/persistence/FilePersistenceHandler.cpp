#include "FilePersistenceHandler.h"
FilePersistenceHandler::FilePersistenceHandler(const std::string &filename) : filename(filename) {
}
void FilePersistenceHandler::save(const std::vector<bool> &bitArray) {}
std::vector<bool> FilePersistenceHandler::load() {
    return std::vector<bool>();
}