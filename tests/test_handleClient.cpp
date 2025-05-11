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
#include <sys/socket.h>
#include <netinet/in.h>

void cleanupTestFiles() {
    // Remove any test files created during the tests
    std::remove("../data/bloom_filter_data.txt");
}

// Execute program with input and return output
std::string executeProgram(const std::vector<std::string>& inputLines) {

    std::string cmd = "timeout 2 ./main_app 12345";//timeout because program is meant to be infinite
    
    std::unique_ptr<FILE, decltype(&pclose)> pipe(popen(cmd.c_str(), "r"), pclose);
    if (!pipe) {
        throw std::runtime_error("popen() failed!");
    }

    int sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) {
        throw std::runtime_error("Socket creation failed");
    }
    // Connect to the server (localhost, port 12345)
    struct sockaddr_in serv_addr;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(12345);
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    if (connect(sockfd, (struct sockaddr*)&serv_addr, sizeof(serv_addr)) < 0) {
        throw std::runtime_error("Connection to server failed");
    }
    // Send input lines to the server
    std::array<char, 128> buffer;
    std::string result;
    for (const auto& line : inputLines) {
        std::string command = line + "\n";
        send(sockfd, command.c_str(), command.size(), 0);
        // Read the output from the server
        while (recv(sockfd, buffer.data(), buffer.size(), 0) > 0) {
            result += std::string(buffer.data());
            if (result.find('\n') != std::string::npos) {
                break; // Stop reading after the first line
            }
        }
    }
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


TEST(HandleClientTest, Example1) {
    // Cleanup any previous test files
    cleanupTestFiles();

    // Input from example 1
    std::vector<std::string> inputLines = {      
        "GET www.example.com0",
        "x",             
        "POST www.example.com0", 
        "GET www.example.com0", 
        "GET www.example.com1", 
        "GET www.example.com11" 
    };

    // Expected output from example 1
    std::vector<std::string> expectedOutput = {
        "200 OK\n\nfalse",
        "400 Bad Request",
        "201 Created",
        "200 OK\n\ntrue true",
        "200 OK\n\nfalse",
        "200 OK\n\nfalse"
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);

    // Cleanup any test files created during the test
    cleanupTestFiles();
}

// Test for example 2 from the instructions
TEST(HandleClientTestTest, Example2) {
    // Cleanup any previous test files
    cleanupTestFiles();
    
    // Input from example 2
    std::vector<std::string> inputLines = {      
        "POST www.example.com0", 
        "GET www.example.com0", 
        "GET www.example.com1"  
    };

    // Expected output from example 2
    std::vector<std::string> expectedOutput = {
        "201 Created",
        "200 OK\n\ntrue true",
        "200 OK\n\nfalse"
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);
    
     
    cleanupTestFiles();
}

// Test for example 3 from the instructions
TEST(HandleClientTest, Example3) {
     
    cleanupTestFiles();
    
    // Input from example 3
    std::vector<std::string> inputLines = {        
        "POST www.example.com0", 
        "GET www.example.com0", 
        "DELETE www.example.com0"
        "GET www.example.com4"  
    };

    // Expected output from example 3
    std::vector<std::string> expectedOutput = {
        "201 Created",
        "200 OK\n\ntrue true",
        "204 No Content",
        "200 OK\n\ntrue false"
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);
    
     
    cleanupTestFiles();
}

// Test persistence between runs
TEST(HandleClientTest, PersistenceBetweenRuns) {
     
    cleanupTestFiles();
    
    // First run, add a URL
    {
        std::vector<std::string> inputLines = {
            "POST www.example.com0"
        };
        executeProgram(inputLines); // No output expected
    }

    // Second run, check if the URL is still filtered
    {
        std::vector<std::string> inputLines = {
            "GET www.example.com0"
        };

        std::vector<std::string> expectedOutput = {
            "200 OK\n\ntrue true"
        };

        std::string output = executeProgram(inputLines);
        verifyOutput(output, expectedOutput);
    }
    
    cleanupTestFiles();
}

// Test for incorrect format handling
TEST(HandleClientTest, IncorrectFormatHandling) {
     
    cleanupTestFiles();
    
    std::vector<std::string> inputLines = {
        "3 command", 
        "1",         
        "2",         
        "test",      
        "1 www.valid-url.com",
        "2 www.valid-url.com"
    };

    std::vector<std::string> expectedOutput = {
        "400 Bad Request",
        "400 Bad Request",
        "400 Bad Request",
        "400 Bad Request",
        "201 Created",
        "200 OK\n\nfalse"
    };

    std::string output = executeProgram(inputLines);
    verifyOutput(output, expectedOutput);
    
    cleanupTestFiles();
}