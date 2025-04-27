//
// Created by A on 18/4/2025.
//

#ifndef ASP_PROJECT_PERSISTENCEHANDLER_H
#define ASP_PROJECT_PERSISTENCEHANDLER_H
#include <vector>

class PersistenceHandler {
public:
    virtual ~PersistenceHandler() = default;
    virtual void save(const std::vector<bool>& bitArray) = 0;
    virtual std::vector<bool> load() = 0;
    bool isSaved() const; // Check if data is saved
private:
    bool saved; // Flag to check if data is saved
};


#endif //ASP_PROJECT_PERSISTENCEHANDLER_H
