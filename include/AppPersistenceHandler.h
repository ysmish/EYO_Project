#ifndef ASP_PROJECT_APPPERSISTENCEHANDLER_H
#define ASP_PROJECT_APPPERSISTENCEHANDLER_H
#include <PersistenceHandler.h>
#include <memory>
#include <iostream>

class AppPersistenceHandler {
private:
    std::unique_ptr<PersistenceHandler> persistenceHandler; // Pointer to a persistence handler for data storage
public:
    AppPersistenceHandler(std::unique_ptr<PersistenceHandler> persistenceHandler)
        : persistenceHandler(std::move(persistenceHandler)) {}
    void init() {
        if (!persistenceHandler->isSaved()) { // Check if data is already saved
            std::getline(std::cin);
            // Initialize new data storage here
        }
    }
};


#endif //ASP_PROJECT_APPPERSISTENCEHANDLER_H