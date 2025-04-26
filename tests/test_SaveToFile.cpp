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

TEST(SaveToFileTests, SaveEmptyVector) {
    // Create persistence handler
    auto filename = "bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Save the empty bitArray vector in the file
    std::vector<bool> bitArray = {};
    persistenceHandler.save(bitArray);

     // Get the bitArray vector from the file and check if the load succseeded
     std::ifstream file(filename);
     std::ostringstream contentStream;
     // Read entire file content into the string stream
     contentStream << file.rdbuf();  
 
     EXPECT_EQ(contentStream.str(), "");
}

TEST(SaveToFileTests, SaveVectorWithTrueAndFalse) {
    // Create persistence handler
    auto filename = "bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Save the bitArray vector in the file
    std::vector<bool> bitArray = {true, false, false, true, true, true, false, true, false};
    persistenceHandler.save(bitArray);

    // Get the bitArray vector from the file and check if the load succseeded
    std::ifstream file(filename);
    std::ostringstream contentStream;
    // Read entire file content into the string stream
    contentStream << file.rdbuf();  

    EXPECT_EQ(contentStream.str(), "100111010");
}

TEST(SaveToFileTests, DoesNotOverwriteFile) {
    // Create persistence handler
    auto filename = "bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Save the first bitArray vector in the file
    std::vector<bool> bitArray1 = {true, false};
    persistenceHandler.save(bitArray1);

    // Save a new bitArray vector in the same file (should append or not overwrite)
    std::vector<bool> bitArray2 = {false, true};
    persistenceHandler.save(bitArray2);

    // Get the bitArray vector from the file and check if the load succeeded
    std::ifstream file(filename);
    std::ostringstream contentStream;
    contentStream << file.rdbuf();  

    // The file should contain the first and second vectors combined, so we expect "1001"
    EXPECT_EQ(contentStream.str(), "1001");
}

TEST(SaveToFileTests, FileCreation) {
    // Create persistence handler
    auto filename = "created_bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Save the bitArray vector in the file
    std::vector<bool> bitArray = {true, false, true};
    persistenceHandler.save(bitArray);

    // Check if the file was created
    std::ifstream file(filename);
    EXPECT_TRUE(file.is_open());  // File should exist and be openable
}

TEST(SaveToFileTests, InvalidFilePath) {
    // Create persistence handler with a non-existent directory
    auto filename = "/nonexistent_directory/bloomfilter.dat";
    auto persistenceHandler = FilePersistenceHandler(filename);

    // Attempt to save the vector to the non-existent path
    std::vector<bool> bitArray = {true, false, true};
    
    // The save function should throw an error because the directory doesn't exist
    EXPECT_THROW(persistenceHandler.save(bitArray), std::system_error);
}

