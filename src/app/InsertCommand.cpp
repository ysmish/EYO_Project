#include <InsertCommand.h>

#include <BloomFilter.h>
#include <iostream>
#include <string>
#include <stdexcept>

InsertCommand::InsertCommand(BloomFilter* bloomFilter) {
    this->bloomFilter = bloomFilter; // Initialize the Bloom filter pointer
}

void InsertCommand::execute(std::string url) {
    // Insert the URL into the Bloom filter
    bloomFilter->insert(url);
}