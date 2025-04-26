//
// Created by A on 18/4/2025.
//

#include "BloomFilter.h"
#include <string>
BloomFilter::BloomFilter(size_t size, std::vector<std::unique_ptr<HashFunction>> hashFunctions, std::unique_ptr<PersistenceHandler> persistenceHandler)
    : bitArray(size), hashFunctions(std::move(hashFunctions)), persistenceHandler(std::move(persistenceHandler)) {
}
void BloomFilter::insert(const std::string &key) {
    // Implementation of insert
}

bool BloomFilter::contains(const std::string &key) const {
    return false; // Placeholder implementation
}

size_t BloomFilter::getBitArraySize() const {
    return 0;
}
size_t BloomFilter::getHashFunctionCount() const {
    return 0;
}