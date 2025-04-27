#include <gtest/gtest.h>
#include <iostream>
#include <string>
#include <vector>
#include <cstdio>
#include <memory>
#include <stdexcept>
#include <array>
#include <sstream>
#include <fstream>

// Helper function to clean up test files
void cleanupTestFiles() {
    std::remove("test_bloom_filter.dat");
}

// Execute program with input and return output
std::string executeProgram(const std::vector<std::string>& inputLines) {
    // Create input file
    std::ofstream inputFile("test_input.txt");
    for (const auto& line : inputLines) {
        inputFile << line << std::endl;
    }
    inputFile.close();

    // Execute program with input redirection
    std::string cmd = "./url_filter < test_input.txt";
    std::array<char, 128> buffer;
    std::string result;
    
    // Open process
    std::unique_ptr<FILE, decltype(&pclose)> pipe(popen(cmd.c_str(), "r"), pclose);
    if (!pipe) {
        throw std::runtime_error("popen() failed!");
    }
    
    // Read output
    while (fgets(buffer.data(), buffer.size(), pipe.get()) != nullptr) {
        result += buffer.data();
    }
    
    // Clean up input file
    std::remove("test_input.txt");
    
    return result;
}

// Helper to compare output with expected
void verifyOutput(const std::string& output, const std::vector<std::string>& expectedLines) {
    std::stringstream ss(output);
    std::string line;
    std::vector<std::string> actualLines;
    
    while (std::getline(ss, line)) {
        if (!line.empty()) {
            // Remove carriage returns if any (for Windows compatibility)
            if (!line.empty() && line[line.size() - 1] == '\r') {
                line.erase(line.size() - 1);
            }
            actualLines.push_back(line);
        }
    }
    
    ASSERT_EQ(actualLines.size(), expectedLines.size()) << "Output line count mismatch";
    for (size_t i = 0; i < expectedLines.size(); i++) {
        EXPECT_EQ(actualLines[i], expectedLines[i]) << "Line " << i << " mismatch";
    }
}

// Test for example 1 from the instructions
TEST(BloomFilterSubprocessTest, Example1Flow) {
    // Clean up any existing test files
    cleanupTestFiles();
    
    // Input from example 1
    std::vector<std::string> inputLines = {
        "a",             // Invalid line, should be ignored
        "8 1 2",         // Bloom filter size 8, using 2 hash functions
        "2 www.example.com0", // Check if URL is filtered
        "x",             // Invalid line, should be ignored
        "1 www.example.com0", // Add URL to filter
        "2 www.example.com0", // Check if URL is filtered, should be true
        "2 www.example.com1", // Check if URL is filtered, should be false
        "2 www.example.com11" // Check if URL is filtered, should be false positive
    };

    // Expected output from example 1
    std::vector<std::string> expectedOutput = {
        "false",
        "true true",
        "false",
        "true false"
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);
    
    // Clean up test files
    cleanupTestFiles();
}

// Test for example 2 from the instructions
TEST(BloomFilterSubprocessTest, Example2Flow) {
    // Clean up any existing test files
    cleanupTestFiles();
    
    // Input from example 2
    std::vector<std::string> inputLines = {
        "8 1",            // Bloom filter size 8, using 1 hash function
        "1 www.example.com0", // Add URL to filter
        "2 www.example.com0", // Check if URL is filtered, should be true
        "2 www.example.com1"  // Check if URL is filtered, might be false positive
    };

    // Expected output from example 2
    std::vector<std::string> expectedOutput = {
        "true true",
        "true false"
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);
    
    // Clean up test files
    cleanupTestFiles();
}

// Test for example 3 from the instructions
TEST(BloomFilterSubprocessTest, Example3Flow) {
    // Clean up any existing test files
    cleanupTestFiles();
    
    // Input from example 3
    std::vector<std::string> inputLines = {
        "8 2",            // Bloom filter size 8, using 2 hash functions
        "1 www.example.com0", // Add URL to filter
        "2 www.example.com0", // Check if URL is filtered, should be true
        "2 www.example.com4"  // Check if URL is filtered, might be false positive
    };

    // Expected output from example 3
    std::vector<std::string> expectedOutput = {
        "true true",
        "true false"
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);
    
    // Clean up test files
    cleanupTestFiles();
}

// Test persistence between runs
TEST(BloomFilterSubprocessTest, PersistenceAcrossRuns) {
    // Clean up any existing test files
    cleanupTestFiles();
    
    // First run - add a URL
    {
        std::vector<std::string> inputLines = {
            "8 1 2",
            "1 www.example.com0"
        };

        executeProgram(inputLines);
    }

    // Second run - check if the URL is still filtered
    {
        std::vector<std::string> inputLines = {
            "8 1 2",  // Same configuration
            "2 www.example.com0"
        };

        std::vector<std::string> expectedOutput = {
            "true true"
        };

        std::string output = executeProgram(inputLines);
        verifyOutput(output, expectedOutput);
    }
    
    // Clean up test files
    cleanupTestFiles();
}

// Test for incorrect format handling
TEST(BloomFilterSubprocessTest, IncorrectFormatHandling) {
    // Clean up any existing test files
    cleanupTestFiles();
    
    std::vector<std::string> inputLines = {
        "8 1 2",
        "3 command", // Invalid command type (not 1 or 2)
        "1",         // Missing URL part
        "2",         // Missing URL part
        "test",      // Completely invalid format
        "1 www.valid-url.com",
        "2 www.valid-url.com"
    };

    std::vector<std::string> expectedOutput = {
        "true true"  // Only the valid commands should produce output
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);
    
    // Clean up test files
    cleanupTestFiles();
}