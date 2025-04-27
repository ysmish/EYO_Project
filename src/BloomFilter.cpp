//
// Created by A on 18/4/2025.
//

#include "BloomFilter.h"
#include <string>
#include <vector>
#include <memory>
#include <stdexcept>
#include <algorithm>
BloomFilter::BloomFilter(size_t size, std::vector<std::unique_ptr<HashFunction>> hashFunctions, std::unique_ptr<PersistenceHandler> persistenceHandler)
    : persistenceHandler(std::move(persistenceHandler)), blacklistedURLs() {
    // Throw exception if size is 0 or hash functions are empty
    if (size <= 0 || hashFunctions.empty()) {
        throw std::invalid_argument("Bloom filter size must be greater than 0 and hash functions must be provided.");
    }

    this->bitArray = std::vector<bool>(size, false);
    this->hashFunctions = std::move(hashFunctions);
}
void BloomFilter::insert(const std::string &key) {
    // Check if the key is already blacklisted
    if (isBlacklisted(key)) {
        return; // No need to insert if it's already blacklisted
    }
    if (key.empty()) {
        throw std::invalid_argument("Empty keys are not allowed in the filter");
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
    if (key.empty()) {
        return false; // Empty keys are not considered in the filter
    }
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
    return bitArray.size();
}
size_t BloomFilter::getHashFunctionCount() const {
    return hashFunctions.size();
}