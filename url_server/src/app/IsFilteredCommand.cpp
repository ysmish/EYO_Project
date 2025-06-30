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
        if(url.empty()) { // fix bug from last exercise
            throw std::invalid_argument("URL cannot be empty");
        }
        bool isFiltered = bloomFilter->contains(url);
        bool isBlacklisted = bloomFilter->isBlacklisted(url);
        std::string response = "200 OK\n\n";
        response += (isFiltered ? "true" : "false");
        if (isFiltered)
        {
            response += " ";
            response += (isBlacklisted ? "true" : "false");
        }
        return response;
    }
    catch(const std::exception& e)
    {
        return "400 Bad Request";
    }
}
