#ifndef PERSISTENCE_HANDLER_H
#define PERSISTENCE_HANDLER_H

#include <vector>
#include <string>

class PersistenceHandler {
public:
    virtual void save(const std::vector<std::string>& blacklistedURLs) = 0;
    virtual std::vector<std::string> load() = 0;
    virtual ~PersistenceHandler() = default;
};

#endif 
