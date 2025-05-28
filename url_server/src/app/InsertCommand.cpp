#include <InsertCommand.h>

#include <BloomFilter.h>
#include <iostream>
#include <string>
#include <stdexcept>

InsertCommand::InsertCommand(BloomFilter* bloomFilter) {
    this->bloomFilter = bloomFilter; // Initialize the Bloom filter pointer
}

std::string InsertCommand::execute(std::string url) {
    try
    {
        bloomFilter->insert(url);
        return "201 Created";
    }
    catch(const std::exception& e)
    {
        return "400 Bad Request";
    }
    
}
