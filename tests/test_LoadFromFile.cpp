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

TEST(LoadFromFileTests, LoadEmptyFile) {
    // Create a file with empty content
    auto filename = "empty_bloomfilter.dat";
    std::ofstream(filename).close();  // Create an empty file

    auto persistenceHandler = FilePersistenceHandler(filename);
    std::vector<std::string> loaded = persistenceHandler.load();

    EXPECT_TRUE(loaded.empty());
}

TEST(LoadFromFileTests, LoadVectorWithURLs) {
    // Prepare a file with URLs
    auto filename = "vector_bloomfilter.dat";
    std::ofstream file(filename);
    file << "http://example.com" << std::endl;
    file << "https://test.com" << std::endl;
    file.close();

    auto persistenceHandler = FilePersistenceHandler(filename);
    std::vector<std::string> loaded = persistenceHandler.load();

    std::vector<std::string> expected = {"http://example.com", "https://test.com"};
    EXPECT_EQ(loaded, expected);
}

TEST(LoadFromFileTests, FileDoesNotExist) {
    // Use a file path that does not exist
    auto filename = "nonexistent_bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Attempt to load from the nonexistent file
    persistenceHandler.load();

    // Ensure that the file was created (it would be empty, but the file should exist)
    std::ifstream file(filename);
    EXPECT_TRUE(file.is_open());
}

TEST(LoadFromFileTests, LoadFileWithInvalidCharacters) {
    // Write an invalid string into the file
    auto filename = "invalidchar_bloomfilter.dat";
    std::ofstream file(filename);
    file << "http://example.com" << std::endl;
    file << "https://test.com" << std::endl;
    file << "10x10" << std::endl;  // Invalid URL
    file.close();

    auto persistenceHandler = FilePersistenceHandler(filename);

    EXPECT_THROW(persistenceHandler.load(), std::ios_base::failure);
}
