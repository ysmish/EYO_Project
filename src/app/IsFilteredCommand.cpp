#include <IsFilteredCommand.h>

#include <BloomFilter.h>
#include <iostream>
#include <string>
#include <stdexcept>

IsFilteredCommand::IsFilteredCommand(BloomFilter* bloomFilter) {
    this->bloomFilter = bloomFilter; // Initialize the Bloom filter pointer
}

std::string IsFilteredCommand::execute(std::string url) {
    return "200 OK";
}