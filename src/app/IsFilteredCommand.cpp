#include <IsFilteredCommand.h>

#include <BloomFilter.h>
#include <iostream>
#include <string>
#include <stdexcept>

IsFilteredCommand::IsFilteredCommand(BloomFilter* bloomFilter) {
    if (bloomFilter == nullptr) {
        throw std::invalid_argument("Bloom filter cannot be null.");
    }

    this->bloomFilter = bloomFilter; // Initialize the Bloom filter pointer
}

void IsFilteredCommand::execute(std::string url) {
    // Check if the URL is already blacklisted
    bool contains = bloomFilter->contains(url);
    bool isBlacklisted = bloomFilter->isBlacklisted(url);
    std::cout << contains << " " << isBlacklisted << std::endl;
}