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

void cleanupTestFiles() {
    std::remove("test_bloom_filter.dat");
}

// Execute program with input and return output
std::string executeProgram(const std::vector<std::string>& inputLines) {
    std::ofstream inputFile("test_input.txt");
    for (const auto& line : inputLines) {
        inputFile << line << std::endl;
    }
    inputFile.close();

    std::string cmd = "timeout 2 ./main_app < test_input.txt 2>&1";//timeout because program is meant to be infinite
    std::array<char, 128> buffer;
    std::string result;
    
    std::unique_ptr<FILE, decltype(&pclose)> pipe(popen(cmd.c_str(), "r"), pclose);
    if (!pipe) {
        throw std::runtime_error("popen() failed!");
    }
    
    while (fgets(buffer.data(), buffer.size(), pipe.get()) != nullptr) {
        result += buffer.data();
    }
    std::remove("test_input.txt");
    
    return result;
}

void verifyOutput(const std::string& output, const std::vector<std::string>& expectedLines) {
    std::stringstream ss(output);
    std::string line;
    std::vector<std::string> actualLines;
    
    while (std::getline(ss, line)) {
        if (!line.empty()) {
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
     
    cleanupTestFiles();
    
    // Input from example 1
    std::vector<std::string> inputLines = {
        "a",             
        "8 1 2",         
        "2 www.example.com0",
        "x",             
        "1 www.example.com0", 
        "2 www.example.com0", 
        "2 www.example.com1", 
        "2 www.example.com11" 
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
    
     
    cleanupTestFiles();
}

// Test for example 2 from the instructions
TEST(BloomFilterSubprocessTest, Example2Flow) {
     
    cleanupTestFiles();
    
    // Input from example 2
    std::vector<std::string> inputLines = {
        "8 1",            
        "1 www.example.com0", 
        "2 www.example.com0", 
        "2 www.example.com1"  
    };

    // Expected output from example 2
    std::vector<std::string> expectedOutput = {
        "true true",
        "true false"
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);
    
     
    cleanupTestFiles();
}

// Test for example 3 from the instructions
TEST(BloomFilterSubprocessTest, Example3Flow) {
     
    cleanupTestFiles();
    
    // Input from example 3
    std::vector<std::string> inputLines = {
        "8 2",            
        "1 www.example.com0", 
        "2 www.example.com0", 
        "2 www.example.com4"  
    };

    // Expected output from example 3
    std::vector<std::string> expectedOutput = {
        "true true",
        "true false"
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);
    
     
    cleanupTestFiles();
}

// Test persistence between runs
TEST(BloomFilterSubprocessTest, PersistenceAcrossRuns) {
     
    cleanupTestFiles();
    
    // First run, add a URL
    {
        std::vector<std::string> inputLines = {
            "8 1 2",
            "1 www.example.com0"
        };

        executeProgram(inputLines); // No output expected
    }

    // Second run, check if the URL is still filtered
    {
        std::vector<std::string> inputLines = {
            "8 1 2",  
            "2 www.example.com0"
        };

        std::vector<std::string> expectedOutput = {
            "true true"
        };

        std::string output = executeProgram(inputLines);
        verifyOutput(output, expectedOutput);
    }
    
    cleanupTestFiles();
}

// Test for incorrect format handling
TEST(BloomFilterSubprocessTest, IncorrectFormatHandling) {
     
    cleanupTestFiles();
    
    std::vector<std::string> inputLines = {
        "8 1 2",
        "3 command", 
        "1",         
        "2",         
        "test",      
        "1 www.valid-url.com",
        "2 www.valid-url.com"
    };

    std::vector<std::string> expectedOutput = {
        "true true"  // Only the valid commands should produce output
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);
    
    cleanupTestFiles();
}