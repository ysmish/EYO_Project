#ifndef ASP_PROJECT_BLOOMFILTER_H
#define ASP_PROJECT_BLOOMFILTER_H

#include <vector>
#include <memory>
#include <string>
#include <mutex>
#include "HashFunction.h"
#include "PersistenceHandler.h"
#include <mutex>

class BloomFilter {
private:
    std::vector<bool> bitArray;
    std::vector<std::string> blacklistedURLs;
    std::vector<std::unique_ptr<HashFunction>> hashFunctions;
    std::unique_ptr<PersistenceHandler> persistenceHandler;
    mutable std::mutex mutex; // Mutex for thread safety

public:
    BloomFilter(size_t size,
                std::vector<std::unique_ptr<HashFunction>> hashFunctions,
                std::unique_ptr<PersistenceHandler> persistenceHandler);

    /**
     * Insert a key into the Bloom filter and persist it.
     */
    void insert(const std::string &key);

    /**
     * Remove a key from the Bloom filter. Throws std::invalid_argument
     * if the key is not present or the format is invalid.
     */
    void deleteURL(const std::string &key);

    /**
     * Check if a key is possibly in the Bloom filter.
     */
    bool contains(const std::string &key) const;

    /**
     * Check if a key is definitely blacklisted (persisted removal).
     */
    bool isBlacklisted(const std::string &key) const;

    /**
     * Get the size of the underlying bit array.
     */
    size_t getBitArraySize() const;

    /**
     * Get the number of hash functions used.
     */
    size_t getHashFunctionCount() const;
};

#endif // ASP_PROJECT_BLOOMFILTER_H