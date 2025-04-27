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
};


#endif //ASP_PROJECT_PERSISTENCEHANDLER_H
