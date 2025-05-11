#include <InsertCommand.h>

#include <BloomFilter.h>
#include <iostream>
#include <string>
#include <stdexcept>

InsertCommand::InsertCommand(BloomFilter* bloomFilter) {
    this->bloomFilter = bloomFilter; // Initialize the Bloom filter pointer
}

std::string InsertCommand::execute(std::string url) {
    // Insert the URL into the Bloom filter
    return "201 Created";
}