#include <BloomFilter.h>
#include <HashFunction.h>
#include <StdHashFunction.h>
#include <PersistenceHandler.h>
#include <FilePersistenceHandler.h>
#include <gtest/gtest.h>
#include <vector>
#include <memory>

// Test case to verify URL filtering works correctly
TEST(BloomFilterIsFilteredTest, DetectsFilteredUrl) {
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
    // Add a URL to the filter
    std::string testUrl = "www.example.com";
    bloomFilter->insert(testUrl);
    
    bool isFiltered = bloomFilter->contains(testUrl);// Check that the URL is correctly identified as filtered
    bool isBlacklisted = bloomFilter->isBlacklisted(testUrl); // Verify it is actually blacklisted
    
    EXPECT_TRUE(isFiltered) << "Bloom filter should detect filtered URL";
    EXPECT_TRUE(isBlacklisted) << "URL should be in the actual filter list";

    // Check empty URL behavior
    std::string emptyUrl = "";
    isFiltered = bloomFilter->contains(emptyUrl);
    isBlacklisted = bloomFilter->isBlacklisted(emptyUrl);
    
    // Bloom filter should consistently handle empty URLs
    EXPECT_FALSE(isBlacklisted) << "Empty URL should not be in actual filter list by default";
    
    // Now add an empty URL and check again
    bloomFilter->insert(emptyUrl);
    isFiltered = bloomFilter->contains(emptyUrl);
    isBlacklisted = bloomFilter->isBlacklisted(emptyUrl);
    
    EXPECT_TRUE(isFiltered) << "Bloom filter should detect empty URL after adding it";
    EXPECT_TRUE(isBlacklisted) << "Empty URL should be in the actual filter list after adding it";

    // Check that a different URL is correctly identified as not filtered
    testUrl = "www.different-example.com";
    isFiltered = bloomFilter->contains(testUrl);
    isBlacklisted = bloomFilter->isBlacklisted(testUrl);
    
    // Either the URL is correctly identified as not filtered, or it's a false positive
    if (!isFiltered) {
        EXPECT_FALSE(isBlacklisted) << "URL should not be in the actual filter list";
    } else {
        // If Bloom filter says it's filtered, check if it's a false positive
        EXPECT_FALSE(isBlacklisted) << "Should be a false positive detection";
    }
}

// Test case for detecting false positives
TEST(BloomFilterIsFilteredTest, DetectsFalsePositives) {
    // We'll create a small Bloom filter with high probability of false positives
    size_t size = 8; // Very small bit array
    std::vector<std::unique_ptr<HashFunction>> hashFunctions;
    hashFunctions.push_back(std::make_unique<StdHashFunction>(1));
    
    auto persistenceHandler = std::make_unique<FilePersistenceHandler>("small_test_bloom_filter.dat");
    auto smallBloomFilter = std::make_unique<BloomFilter>(
        size,
        std::move(hashFunctions),
        std::move(persistenceHandler)
    );
    
    // Add several URLs to increase chance of hash collisions
    smallBloomFilter->insert("www.example.com0");
    smallBloomFilter->insert("www.example.com1");
    smallBloomFilter->insert("www.example.com2");
    
    // Now check a URL that we didn't add, but might trigger a false positive
    std::string testUrl = "www.example.com7";
    bool isFiltered = smallBloomFilter->contains(testUrl);
    bool isBlacklisted = smallBloomFilter->isBlacklisted(testUrl);
    
    // If the Bloom filter says it's filtered but it's not in the actual list, it's a false positive
    if (isFiltered && !isBlacklisted) {
        SUCCEED() << "Successfully detected a false positive";
    } else if (!isFiltered) {
        SUCCEED() << "URL correctly identified as not filtered";
    } else {
        FAIL() << "URL should not be in the actual filter list";
    }
    
    // Clean up the test file
    std::remove("small_test_bloom_filter.dat");
}


