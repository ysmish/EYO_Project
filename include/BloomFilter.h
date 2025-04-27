//
// Created by A on 18/4/2025.
//

#ifndef ASP_PROJECT_BLOOMFILTER_H
#define ASP_PROJECT_BLOOMFILTER_H
#include <vector>
#include <memory>
#include <string>
#include "HashFunction.h"
#include "PersistenceHandler.h"

class BloomFilter {
private:
    std::vector<bool> bitArray;
    std::vector<std::string> blacklistedURLs;
    std::vector<std::unique_ptr<HashFunction>> hashFunctions;
    std::unique_ptr<PersistenceHandler> persistenceHandler;
public:
    BloomFilter(size_t size,
                std::vector<std::unique_ptr<HashFunction>> hashFunctions,
                std::unique_ptr<PersistenceHandler> persistenceHandler);
    void insert(const std::string &key);
    bool contains(const std::string &key) const;
    bool isBlacklisted(const std::string &key) const;
    size_t getBitArraySize() const;
    size_t getHashFunctionCount() const;
};


#endif //ASP_PROJECT_BLOOMFILTER_H
