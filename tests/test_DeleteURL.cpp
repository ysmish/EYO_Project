// tests/test_DeleteURL.cpp

#include <gtest/gtest.h>
#include <vector>
#include <memory>
#include <stdexcept>
#include "BloomFilter.h"
#include "HashFunction.h"
#include "StdHashFunction.h"
#include "PersistenceHandler.h"
#include "FilePersistenceHandler.h"

static std::unique_ptr<BloomFilter> makeFilter(const std::string& dbFile) {
    size_t size = 1000;
    std::vector<std::unique_ptr<HashFunction>> hashFns;
    hashFns.push_back(std::make_unique<StdHashFunction>(1));
    hashFns.push_back(std::make_unique<StdHashFunction>(2));
    auto persistence = std::make_unique<FilePersistenceHandler>(dbFile);
    return std::make_unique<BloomFilter>(size, std::move(hashFns), std::move(persistence));
}

TEST(DeleteURLTest, RemoveExistingURL) {
    auto bloom = makeFilter("delete_exists.dat");
    std::string url = "http://example.com";

    bloom->insert(url);
    ASSERT_TRUE(bloom->contains(url));  // sanity check

    bloom->deleteURL(url);
    EXPECT_FALSE(bloom->contains(url));
}

TEST(DeleteURLTest, RemoveNonexistentURL) {
    auto bloom = makeFilter("delete_missing.dat");
    std::string missing = "http://not-in-filter.com";

    EXPECT_FALSE(bloom->contains(missing));
    EXPECT_THROW(bloom->deleteURL(missing), std::invalid_argument);
}

TEST(DeleteURLTest, RemoveInvalidURL) {
    auto bloom = makeFilter("delete_invalid.dat");
    std::string bad = "nota_url";

    EXPECT_THROW(bloom->deleteURL(bad), std::invalid_argument);
}

TEST(DeleteURLTest, RemoveTwiceThrows) {
    auto bloom = makeFilter("delete_twice.dat");
    std::string url = "https://double-remove.com";

    bloom->insert(url);
    ASSERT_TRUE(bloom->contains(url));

    bloom->deleteURL(url);
    EXPECT_FALSE(bloom->contains(url));

    // second removal should now fail
    EXPECT_THROW(bloom->deleteURL(url), std::invalid_argument);
}
