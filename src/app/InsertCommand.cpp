#include <InsertCommand.h>

#include <BloomFilter.h>
#include <iostream>
#include <string>
#include <stdexcept>

InsertCommand::InsertCommand(BloomFilter* bloomFilter) {
    if (bloomFilter == nullptr) {
        throw std::invalid_argument("Bloom filter cannot be null.");
    }
    this->bloomFilter = bloomFilter; // Initialize the Bloom filter pointer
}

void InsertCommand::execute(std::string url) {
    // Insert the URL into the Bloom filter
    bloomFilter->insert(url);
}