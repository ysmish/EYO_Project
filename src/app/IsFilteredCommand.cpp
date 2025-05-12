#include <IsFilteredCommand.h>

#include <BloomFilter.h>
#include <iostream>
#include <string>
#include <stdexcept>

IsFilteredCommand::IsFilteredCommand(BloomFilter* bloomFilter) {
    this->bloomFilter = bloomFilter; // Initialize the Bloom filter pointer
}

std::string IsFilteredCommand::execute(std::string url) {
    try
    {
        bool isFiltered = bloomFilter->contains(url);
        bool isBlacklisted = bloomFilter->isBlacklisted(url);
        return "200 OK\n\n" + std::to_string(isFiltered) + " " + std::to_string(isBlacklisted);
    }
    catch(const std::exception& e)
    {
        return "400 Bad Request";
    }
}
