#ifndef PERSISTENCE_HANDLER_H
#define PERSISTENCE_HANDLER_H

#include <vector>
#include <string>

class PersistenceHandler {
public:
    virtual ~PersistenceHandler() = default;
    virtual void save(const std::vector<bool>& bitArray) = 0;
    virtual std::vector<bool> load() = 0;
};

#endif 
