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
    std::vector<bool> loaded = persistenceHandler.load();

    EXPECT_TRUE(loaded.empty());
}

TEST(LoadFromFileTests, LoadVectorWithTrueAndFalse) {
    // Prepare a file with bitArray "100111010"
    auto filename = "vector_bloomfilter.dat";
    std::ofstream file(filename);
    file << "100111010";
    file.close();

    auto persistenceHandler = FilePersistenceHandler(filename);
    std::vector<bool> loaded = persistenceHandler.load();

    std::vector<bool> expected = {true, false, false, true, true, true, false, true, false};
    EXPECT_EQ(loaded, expected);
}

TEST(LoadFromFileTests, FileDoesNotExist) {
    // Use a file path that does not exist
    auto filename = "nonexistent_bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    EXPECT_THROW(persistenceHandler.load(), std::system_error);
}

TEST(LoadFromFileTests, LoadFileWithInvalidCharacters) {
    // Write an invalid string into file (e.g., "10x10")
    auto filename = "invalidchar_bloomfilter.dat";
    std::ofstream file(filename);
    file << "10x10";
    file.close();

    auto persistenceHandler = FilePersistenceHandler(filename);

    EXPECT_THROW(persistenceHandler.load(), std::ios_base::failure);
}
