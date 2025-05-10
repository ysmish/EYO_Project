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
#include <arpa/inet.h>
#include <unistd.h>
#include <thread>
#include <chrono>

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

// Test 1: check connection after idle period, as the server should not stop running
TEST(TCPConnectionTest, ConnectionAfterIdlePeriod) {
    cleanupTestFiles();
    
    // Start the server process
    std::system("./main_app 12345 > /dev/null 2>&1 &");
    
    // Wait for server to start
    std::this_thread::sleep_for(std::chrono::milliseconds(500));
    
    int sockfd = socket(AF_INET, SOCK_STREAM, 0);
    ASSERT_GT(sockfd, 0) << "Socket creation failed";
    
    struct sockaddr_in serv_addr;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(12345);
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    
    int conn_result = connect(sockfd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
    ASSERT_GE(conn_result, 0) << "Connection to server failed";
    
    // Send initial command
    std::string cmd1 = "GET www.example.com\n";
    send(sockfd, cmd1.c_str(), cmd1.length(), 0);
    
    // Wait for response
    std::array<char, 4096> buffer;
    memset(buffer.data(), 0, buffer.size());
    int bytes_read = recv(sockfd, buffer.data(), buffer.size() - 1, 0);
    ASSERT_GT(bytes_read, 0) << "No initial response received";
    
    // Wait for an idle period
    std::this_thread::sleep_for(std::chrono::seconds(3));
    
    // Try to send another command after idle period
    std::string cmd2 = "GET www.example2.com\n";
    send(sockfd, cmd2.c_str(), cmd2.length(), 0);
    
    memset(buffer.data(), 0, buffer.size());
    bytes_read = recv(sockfd, buffer.data(), buffer.size() - 1, 0);
    
    // Server should still respond since there's no timeout mechanism specified
    ASSERT_GT(bytes_read, 0) << "No response received after idle period";
    std::string response(buffer.data(), bytes_read);
    EXPECT_TRUE(response.find("200") != std::string::npos) 
               << "Invalid response after idle period";
    
    close(sockfd);
    std::system("pkill -f \"./main_app 12345\"");
    
    cleanupTestFiles();
}

// Test 2: make sure connection and clients are fine after abrupt termination of client
TEST(TCPConnectionTest, ConnectionTermination) {
    cleanupTestFiles();
    
    // First connection
    {
        std::vector<std::string> inputLines = {
            "POST www.example.com"
        };
        
        std::vector<std::string> expectedOutput = {
            "201 Created"
        };
        
        std::string output = executeProgram(inputLines);
        verifyOutput(output, expectedOutput);
    }
    
    // Second connection after termination
    {
        std::vector<std::string> inputLines = {
            "GET www.example.com"
        };
        
        std::vector<std::string> expectedOutput = {
            "200 OK\n\ntrue true"
        };
        
        std::string output = executeProgram(inputLines);
        verifyOutput(output, expectedOutput);
    }
    
    cleanupTestFiles();
}

// Test 3: make sure server supports large data transmission
TEST(TCPConnectionTest, LargeDataTransmission) {
    cleanupTestFiles();
    
    // Create a very long (that's what she said), URL, (but still valid)
    std::string longDomain = "www.";
    for (int i = 0; i < 200; i++) {
        longDomain += "a";
    }
    longDomain += ".com";
    
    std::vector<std::string> inputLines = {
        "POST " + longDomain
    };
    
    std::vector<std::string> expectedOutput = {
        "201 Created"
    };
    
    try {
        std::string output = executeProgram(inputLines);
        verifyOutput(output, expectedOutput);
    } catch (const std::exception& e) {
        FAIL() << "Exception during large data transmission: " << e.what();
    }
    
    cleanupTestFiles();
}

// Test 4: check behavior for partial message
TEST(TCPConnectionTest, PartialMessage) {
    cleanupTestFiles();
    
    // Start the server process
    std::system("./main_app 12345 > /dev/null 2>&1 &");
    
    // Wait for server to start
    std::this_thread::sleep_for(std::chrono::milliseconds(500));
    
    int sockfd = socket(AF_INET, SOCK_STREAM, 0);
    ASSERT_GT(sockfd, 0) << "Socket creation failed";
    
    struct sockaddr_in serv_addr;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(12345);
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    
    int conn_result = connect(sockfd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
    ASSERT_GE(conn_result, 0) << "Connection to server failed";
    
    // Send partial command (no newline)
    std::string cmd1 = "POST www.example.com";
    send(sockfd, cmd1.c_str(), cmd1.length(), 0);
    
    // Wait a bit before sending the rest
    std::this_thread::sleep_for(std::chrono::milliseconds(100));
    
    // Send the rest of the command
    std::string cmd2 = "\n";
    send(sockfd, cmd2.c_str(), cmd2.length(), 0);
    
    // Wait for response
    std::array<char, 4096> buffer;
    memset(buffer.data(), 0, buffer.size());
    int bytes_read = recv(sockfd, buffer.data(), buffer.size() - 1, 0);
    ASSERT_GT(bytes_read, 0) << "No response received";
    
    std::string response(buffer.data(), bytes_read);
    EXPECT_TRUE(response.find("201") != std::string::npos) << "Invalid response for partial message";
    
    close(sockfd);
    std::system("pkill -f \"./main_app 12345\"");
    
    cleanupTestFiles();
}
