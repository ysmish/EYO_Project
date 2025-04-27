#ifndef FILE_PERSISTENCE_HANDLER_H
#define FILE_PERSISTENCE_HANDLER_H

#include "PersistenceHandler.h" 
#include <string>
#include <vector>

class FilePersistenceHandler : public PersistenceHandler {
private:
    std::string filename;
public:
    explicit FilePersistenceHandler(const std::string& filename);
    void save(const std::vector<bool>& bitArray) override;
    std::vector<bool> load() override;
};

#endif 
