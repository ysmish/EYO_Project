#include <DeleteCommand.h>

#include <BloomFilter.h>
#include <iostream>
#include <string>
#include <stdexcept>

DeleteCommand::DeleteCommand(BloomFilter* bloomFilter) {
    this->bloomFilter = bloomFilter; // Initialize the Bloom filter pointer
}

std::string DeleteCommand::execute(std::string url) {
    try
    {
        bloomFilter->deleteURL(url);
        return "204 No Content";
    }
    catch(const std::exception& e)
    {
        if(url.empty())
        {
            return "400 Bad Request";
        }
        return "404 Not Found";
    }
    
}
