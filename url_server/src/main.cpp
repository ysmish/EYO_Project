#include <iostream>
#include <App.h>
#include <map>
#include <string>
#include <memory>
#include <InsertCommand.h>
#include <IsFilteredCommand.h>
#include <FilePersistenceHandler.h>
#include <ConsoleInputHandler.h>
#include <StdHashFunction.h>


int main() {

    std::vector<std::unique_ptr<HashFunction>> hashFunctions;
    hashFunctions.push_back(std::make_unique<StdHashFunction>(1));
    hashFunctions.push_back(std::make_unique<StdHashFunction>(2));
    hashFunctions.push_back(std::make_unique<StdHashFunction>(3));
    App app(
        1000, // Size of the Bloom filter
        std::move(hashFunctions), // Move the vector of hash functions
        std::make_unique<FilePersistenceHandler>("../data/bloom_filter_data.txt"),
        12345 // Port number for the server
    );
    
    // Run the application
    app.run();

    return 0;
}