//
// Created by A on 18/4/2025.
//

#include "BloomFilter.h"
#include <string>
#include <vector>
#include <memory>
#include <algorithm>
BloomFilter::BloomFilter(size_t size, std::vector<std::unique_ptr<HashFunction>> hashFunctions, std::unique_ptr<PersistenceHandler> persistenceHandler)
    : bitArray(size, false), hashFunctions(std::move(hashFunctions)), persistenceHandler(std::move(persistenceHandler)), blacklistedURLs() {
}
void BloomFilter::insert(const std::string &key) {
    // Check if the key is already blacklisted
    if (isBlacklisted(key)) {
        return; // No need to insert if it's already blacklisted
    }
    // Add the key to the blacklisted URLs
    blacklistedURLs.push_back(key);
    // Set the bits in the bit array based on the hash functions
    for (const auto &hashFunction : hashFunctions) {
        size_t hashValue = hashFunction->hash(key) % bitArray.size();
        bitArray[hashValue] = true;
    }
}

bool BloomFilter::contains(const std::string &key) const {
    // if one of the hash functions returns false then the key is not in the filter
    for (const auto &hashFunction : hashFunctions) {
        size_t hashValue = hashFunction->hash(key) % bitArray.size();
        if (!bitArray[hashValue]) {
            return false;
        }
    }
    return true;
}

bool BloomFilter::isBlacklisted(const std::string &key) const {
    return std::find(blacklistedURLs.begin(), blacklistedURLs.end(), key) != blacklistedURLs.end();
}

size_t BloomFilter::getBitArraySize() const {
    return 0;
}
size_t BloomFilter::getHashFunctionCount() const {
    return 0;
}