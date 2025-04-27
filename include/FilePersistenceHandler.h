// FilePersistenceHandler.h
#ifndef FILE_PERSISTENCE_HANDLER_H
#define FILE_PERSISTENCE_HANDLER_H

#include "PersistenceHandler.h" // Crucial: Include the base class header
#include <string>
#include <vector>

class FilePersistenceHandler : public PersistenceHandler {
public:
    explicit FilePersistenceHandler(const std::string& filename);
    void save(const std::vector<bool>& bitArray) override;
    std::vector<bool> load() override;

private:
    std::string filename;
};

#endif // FILE_PERSISTENCE_HANDLER_H
