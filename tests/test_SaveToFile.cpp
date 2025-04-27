#include <BloomFilter.h>
#include <HashFunction.h>
#include <StdHashFunction.h>
#include <PersistenceHandler.h>
#include <FilePersistenceHandler.h>
#include <gtest/gtest.h>
#include <vector>
#include <memory>
#include <stdexcept>
#include <fstream>
#include <sstream>

TEST(SaveToFileTests, SaveEmptyBlacklistedURLs) {
    // Create persistence handler
    auto filename = "bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Save the empty blacklistedURLs vector in the file
    std::vector<std::string> blacklistedURLs = {};
    persistenceHandler.save(blacklistedURLs);

    // Get the blacklistedURLs vector from the file and check if the load succeeded
    std::ifstream file(filename);
    std::ostringstream contentStream;
    contentStream << file.rdbuf();

    EXPECT_EQ(contentStream.str(), "");
}

TEST(SaveToFileTests, SaveBlacklistedURLs) {
    // Create persistence handler
    auto filename = "bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Save the blacklistedURLs vector in the file
    std::vector<std::string> blacklistedURLs = {"http://example.com", "https://test.com"};
    persistenceHandler.save(blacklistedURLs);

    // Get the blacklistedURLs vector from the file and check if the load succeeded
    std::ifstream file(filename);
    std::ostringstream contentStream;
    contentStream << file.rdbuf();

    EXPECT_EQ(contentStream.str(), "http://example.com\nhttps://test.com\n");
}

TEST(SaveToFileTests, DoesOverwriteFileWithBlacklistedURLs) {
    // Create persistence handler
    auto filename = "bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Save the first blacklistedURLs vector in the file
    std::vector<std::string> blacklistedURLs1 = {"http://example.com"};
    persistenceHandler.save(blacklistedURLs1);

    // Save a new blacklistedURLs vector in the same file (should overwrite or not append)
    std::vector<std::string> blacklistedURLs2 = {"https://test.com"};
    persistenceHandler.save(blacklistedURLs2);

    // Get the blacklistedURLs vector from the file and check if the load succeeded
    std::ifstream file(filename);
    std::ostringstream contentStream;
    contentStream << file.rdbuf();

    // The file should contain the second vector, not combined
    EXPECT_EQ(contentStream.str(), "https://test.com\n");
}

TEST(SaveToFileTests, FileCreationWithBlacklistedURLs) {
    // Create persistence handler
    auto filename = "created_bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Save the blacklistedURLs vector in the file
    std::vector<std::string> blacklistedURLs = {"http://example.com"};
    persistenceHandler.save(blacklistedURLs);

    // Check if the file was created
    std::ifstream file(filename);
    EXPECT_TRUE(file.is_open());  // File should exist and be openable
}

TEST(SaveToFileTests, InvalidFilePathForBlacklistedURLs) {
    // Create persistence handler with a non-existent directory
    auto filename = "/nonexistent_directory/bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Attempt to save the vector to the non-existent path
    std::vector<std::string> blacklistedURLs = {"http://example.com"};

    // The save function should throw an error because the directory doesn't exist
    EXPECT_THROW(persistenceHandler.save(blacklistedURLs), std::system_error);
}