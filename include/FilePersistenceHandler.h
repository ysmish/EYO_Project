#ifndef FILE_PERSISTENCE_HANDLER_H
#define FILE_PERSISTENCE_HANDLER_H

#include "PersistenceHandler.h"
#include <fstream>
#include <vector>
#include <string>

class FilePersistenceHandler : public PersistenceHandler {
private:
    std::string filename;

public:
    explicit FilePersistenceHandler(const std::string& filename);

    void save(const std::vector<std::string>& blacklistedURLs) override;
    std::vector<std::string> load() override;
};

#endif 
