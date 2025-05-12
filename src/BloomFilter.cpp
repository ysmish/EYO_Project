#include "BloomFilter.h"
#include <string>
#include <vector>
#include <memory>
#include <stdexcept>
#include <algorithm>
#include <regex>

BloomFilter::BloomFilter(size_t size,
    std::vector<std::unique_ptr<HashFunction>> hashFunctions,
    std::unique_ptr<PersistenceHandler> persistenceHandler)
: persistenceHandler(std::move(persistenceHandler)), blacklistedURLs() {
    if (size <= 0 || hashFunctions.empty()) {
        throw std::invalid_argument("Bloom filter size must be greater than 0 and hash functions must be provided.");
    }

    this->bitArray = std::vector<bool>(size, false);
    this->hashFunctions = std::move(hashFunctions);
    // Load existing blacklisted URLs from the persistence handler
    try {
        for (const auto &url : this->persistenceHandler->load()) {
            if (!std::regex_match(url, std::regex(R"(^((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$)"))) {
                throw std::ios_base::failure("Invalid URL format in file: " + url);
            }
            insert(url); // Insert the URL into the filter
        }
    } catch (const std::exception &e) {
        throw std::runtime_error("Failed to load blacklisted URLs: " + std::string(e.what()));
    }
}

void BloomFilter::insert(const std::string &key) {
    if (key.empty()) {
        throw std::invalid_argument("Empty keys are not allowed in the filter");
    }

    // Regex URL validation using the provided pattern
    std::regex urlCheck(R"(^((https?:\/\/)?(www\.)?([a-zA-Z0-9-]+\.)+[a-zA-Z0-9]{2,})(\/\S*)?$)");
    if (!std::regex_match(key, urlCheck)) {
        throw std::invalid_argument("Invalid URL format");
    }

    // Check if the URL is already blacklisted
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
    persistenceHandler->save(blacklistedURLs); // Save the updated blacklisted URLs
}

void BloomFilter::deleteURL(const std::string &key) {
    // blank implementation stub
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