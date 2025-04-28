#include <IsFilteredCommand.h>

#include <BloomFilter.h>
#include <iostream>
#include <string>
#include <stdexcept>

IsFilteredCommand::IsFilteredCommand(BloomFilter* bloomFilter) {
    this->bloomFilter = bloomFilter; // Initialize the Bloom filter pointer
}

void IsFilteredCommand::execute(std::string url) {
    // Check if the URL is already blacklisted
    bool contains = bloomFilter->contains(url);
    bool isBlacklisted = bloomFilter->isBlacklisted(url);
    std::cout << std::boolalpha; // Print boolean values as true/false
    std::cout << contains;
    if (contains) {
        std::cout << " " << isBlacklisted; // Print the blacklisted status if the URL is in the filter
    }
    std::cout << std::endl; // Print a newline character
}