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

    // Create persistence handler
    auto persistenceHandler = std::make_unique<FilePersistenceHandler>("bloomfilter.dat");

    // Create Bloom filter
    auto bloomFilter = std::make_unique<BloomFilter>(
            size,
            std::move(hashFunctions),
            std::move(persistenceHandler)
    );
    // Check if the bit array is of the correct size
    EXPECT_EQ(bloomFilter.getBitArraySize(), size);
    EXPECT_EQ(bloomFilter.getHashFunctionCount(), hashFunctions.size());
}

TEST(BloomFilterInitializationTest, EmptyInitialization) {
    // Test empty initialization
    std::vector<std::unique_ptr<HashFunction>> hashFunctions = {
            std::make_unique<StdHashFunction>(1),
            std::make_unique<StdHashFunction>(2)
    };
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

void RegisterBloomFilterTests() {
}