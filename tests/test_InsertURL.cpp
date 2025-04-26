#include <gtest/gtest.h>
#include <vector>
#include <memory>
#include <stdexcept>
#include <BloomFilter.h>
#include <HashFunction.h>
#include <StdHashFunction.h>
#include <PersistenceHandler.h>
#include <FilePersistenceHandler.h>

TEST(InsertURLTest, InsertURL) {
    // Create a Bloom filter with a size of 1000 and 2 hash functions
    size_t size = 1000;
    std::vector<std::unique_ptr<HashFunction>> hashFunctions;
    hashFunctions.push_back(std::make_unique<StdHashFunction>(1));
    hashFunctions.push_back(std::make_unique<StdHashFunction>(2));
    
    // Create persistence handler
    auto persistenceHandler = std::make_unique<FilePersistenceHandler>("bloomfilter.dat");
    
    // Create Bloom filter
    auto bloomFilter = std::make_unique<BloomFilter>(
            size,
            std::move(hashFunctions),
            std::move(persistenceHandler)
    );
    
    // Insert a URL into the Bloom filter
    std::string url = "http://example.com";
    bloomFilter->insert(url);
    
    // Check if the URL is in the Bloom filter
    EXPECT_TRUE(bloomFilter->contains(url));
}
TEST(InsertURLTest, InsertInvalidURL) {
    // Create a Bloom filter with a size of 1000 and 2 hash functions
    size_t size = 1000;
    std::vector<std::unique_ptr<HashFunction>> hashFunctions;
    hashFunctions.push_back(std::make_unique<StdHashFunction>(1));
    hashFunctions.push_back(std::make_unique<StdHashFunction>(2));
    
    // Create persistence handler
    auto persistenceHandler = std::make_unique<FilePersistenceHandler>("bloomfilter.dat");
    
    // Create Bloom filter
    auto bloomFilter = std::make_unique<BloomFilter>(
            size,
            std::move(hashFunctions),
            std::move(persistenceHandler)
    );
    
    // Insert an invalid URL into the Bloom filter
    std::string invalidUrl = "invalid_url";
    EXPECT_THROW(bloomFilter->insert(invalidUrl), std::invalid_argument);
}

void RegisterInsertURLTests() {
}