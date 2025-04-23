//
// Created by A on 18/4/2025.
//

#ifndef ASP_PROJECT_FILEPERSISTENCEHANDLER_H
#define ASP_PROJECT_FILEPERSISTENCEHANDLER_H
#include <string>
#include <vector>
#include "PersistenceHandler.h"

class FilePersistenceHandler : public PersistenceHandler {
private:
    std::string filename;
public:
    FilePersistenceHandler(const std::string& filename);

    void save(const std::vector<bool>& bitArray) override;

    std::vector<bool> load() override;
};


#endif //ASP_PROJECT_FILEPERSISTENCEHANDLER_H
