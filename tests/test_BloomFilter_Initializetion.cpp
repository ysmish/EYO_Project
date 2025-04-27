#include <BloomFilter.h>
#include <HashFunction.h>
#include <StdHashFunction.h>
#include <PersistenceHandler.h>
#include <FilePersistenceHandler.h>
#include <gtest/gtest.h>
#include <vector>
#include <memory>
#include <stdexcept>

TEST(BloomFilterInitializationTest, AllocatesCorrectSize) {
        size_t size = 1000;
        std::vector<std::unique_ptr<HashFunction>> hashFunctions;
        hashFunctions.push_back(std::make_unique<StdHashFunction>(1));
        hashFunctions.push_back(std::make_unique<StdHashFunction>(2));
        
        size_t expectedHashFunctionCount = hashFunctions.size();
        auto persistenceHandler = std::make_unique<FilePersistenceHandler>("bloomfilter.dat");
        
        // Create Bloom filter with size, hash functions, and persistence handler
        auto bloomFilter = std::make_unique<BloomFilter>(size, std::move(hashFunctions), std::move(persistenceHandler));
        
        EXPECT_EQ(bloomFilter->getBitArraySize(), size);
        EXPECT_EQ(bloomFilter->getHashFunctionCount(), expectedHashFunctionCount);
    }

TEST(BloomFilterInitializationTest, EmptyInitialization) {
    // Test empty initialization
    std::vector<std::unique_ptr<HashFunction>> hashFunctions;
    hashFunctions.push_back(std::make_unique<StdHashFunction>(1));
    hashFunctions.push_back(std::make_unique<StdHashFunction>(2));
    auto persistenceHandler = std::make_unique<FilePersistenceHandler>("bloomfilter.dat");

    // Create Bloom filter with no hash functions
    EXPECT_THROW(std::make_unique<BloomFilter>(
                    0,
                    std::move(hashFunctions),
                    std::move(persistenceHandler)
            ),
            std::invalid_argument
    );
    // Create Bloom filter with no hash functions
    EXPECT_THROW(std::make_unique<BloomFilter>(
                    1000,
                    std::vector<std::unique_ptr<HashFunction>>(),
                    std::move(persistenceHandler)
            ),
            std::invalid_argument
    );
}