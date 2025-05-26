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

TEST(HandleClientTest, Example1) {
    // Cleanup any previous test files
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
    
    // Wait for an idle period
    std::this_thread::sleep_for(std::chrono::seconds(3));
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
        "200 OK\n\nfalse\n",
        "400 Bad Request\n",
        "201 Created\n",
        "200 OK\n\ntrue true\n",
        "200 OK\n\nfalse\n",
        "200 OK\n\nfalse\n"
    };

    for (int i = 0; i < inputLines.size(); i++) {
        std::string command = inputLines[i] + "\n";
        send(sockfd, command.c_str(), command.size(), 0);

        std::this_thread::sleep_for(std::chrono::milliseconds(100));
        // Read the output from the server
        std::array<char, 4096> buffer;
        memset(buffer.data(), 0, buffer.size());
        int bytes_read = recv(sockfd, buffer.data(), buffer.size() - 1, 0);
        ASSERT_GT(bytes_read, 0) << "No response received";
        
        std::string response(buffer.data(), bytes_read);
        EXPECT_EQ(response, expectedOutput[i]);
    }
    
    close(sockfd);
    // Cleanup any test files created during the test
    cleanupTestFiles();
}

// Test for example 2 from the instructions
TEST(HandleClientTestTest, Example2) {
    // Cleanup any previous test files
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
    
    // Wait for an idle period
    std::this_thread::sleep_for(std::chrono::seconds(3));
    // Input from example 2
    std::vector<std::string> inputLines = {      
        "POST www.example.com0", 
        "GET www.example.com0", 
        "GET www.example.com1"  
    };

    // Expected output from example 2
    std::vector<std::string> expectedOutput = {
        "201 Created\n",
        "200 OK\n\ntrue true\n",
        "200 OK\n\nfalse\n"
    };

    for (int i = 0; i < inputLines.size(); i++) {
        std::string command = inputLines[i] + "\n";
        send(sockfd, command.c_str(), command.size(), 0);

        std::this_thread::sleep_for(std::chrono::milliseconds(100));
        // Read the output from the server
        std::array<char, 4096> buffer;
        memset(buffer.data(), 0, buffer.size());
        int bytes_read = recv(sockfd, buffer.data(), buffer.size() - 1, 0);
        ASSERT_GT(bytes_read, 0) << "No response received";
        
        std::string response(buffer.data(), bytes_read);
        EXPECT_EQ(response, expectedOutput[i]);
    }
    
    close(sockfd);
    // Cleanup any test files created during the test
    cleanupTestFiles();
}

// Test for example 3 from the instructions
TEST(HandleClientTest, Example3) {
    // Cleanup any previous test files
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
    
    // Wait for an idle period
    std::this_thread::sleep_for(std::chrono::seconds(3));
    // Input from example 3
    std::vector<std::string> inputLines = {        
        "POST www.example.com0", 
        "GET www.example.com0", 
        "DELETE www.example.com0",
        "GET www.example.com4"  
    };

    // Expected output from example 3
    std::vector<std::string> expectedOutput = {
        "201 Created\n",
        "200 OK\n\ntrue true\n",
        "204 No Content\n",
        "200 OK\n\nfalse\n"
    };

    for (int i = 0; i < inputLines.size(); i++) {
        std::string command = inputLines[i] + "\n";
        send(sockfd, command.c_str(), command.size(), 0);

        std::this_thread::sleep_for(std::chrono::milliseconds(100));
        // Read the output from the server
        std::array<char, 4096> buffer;
        memset(buffer.data(), 0, buffer.size());
        int bytes_read = recv(sockfd, buffer.data(), buffer.size() - 1, 0);
        ASSERT_GT(bytes_read, 0) << "No response received";
        
        std::string response(buffer.data(), bytes_read);
        EXPECT_EQ(response, expectedOutput[i]);
    }
    
    close(sockfd);
    // Cleanup any test files created during the test
    cleanupTestFiles();
}

// Test persistence between runs
TEST(HandleClientTest, PersistenceBetweenRuns) {
    // Cleanup any previous test files
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

    std::string command = "POST www.example.com0\n";
    send(sockfd, command.c_str(), command.size(), 0);

    std::this_thread::sleep_for(std::chrono::milliseconds(100));
    close(sockfd);

            // Start the server process
    std::system("./main_app 12345 > /dev/null 2>&1 &");
    
    // Wait for server to start
    std::this_thread::sleep_for(std::chrono::milliseconds(500));
    
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    ASSERT_GT(sockfd, 0) << "Socket creation failed";
    
    serv_addr;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(12345);
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    
    conn_result = connect(sockfd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
    ASSERT_GE(conn_result, 0) << "Connection to server failed";

    command = "GET www.example.com0\n";
    send(sockfd, command.c_str(), command.size(), 0);

    std::this_thread::sleep_for(std::chrono::milliseconds(100));

    // Read the output from the server
    std::array<char, 4096> buffer;
    memset(buffer.data(), 0, buffer.size());
    int bytes_read = recv(sockfd, buffer.data(), buffer.size() - 1, 0);
    ASSERT_GT(bytes_read, 0) << "No response received";
    
    std::string response(buffer.data(), bytes_read);
    EXPECT_EQ(response, "200 OK\n\ntrue true\n");
    
    close(sockfd);
    // Cleanup any test files created during the test
    cleanupTestFiles();
}

// Test for incorrect format handling
TEST(HandleClientTest, IncorrectFormatHandling) {
    // Cleanup any previous test files
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
    
    // Wait for an idle period
    std::this_thread::sleep_for(std::chrono::seconds(3));
    std::vector<std::string> inputLines = {
        "3 command", 
        "1",         
        "2",         
        "test",      
        "POST www.valid-url.com",
        "GET www.valid-url.com"
    };

    std::vector<std::string> expectedOutput = {
        "400 Bad Request\n",
        "400 Bad Request\n",
        "400 Bad Request\n",
        "400 Bad Request\n",
        "201 Created\n",
        "200 OK\n\ntrue true\n"
    };

    for (int i = 0; i < inputLines.size(); i++) {
        std::string command = inputLines[i] + "\n";
        send(sockfd, command.c_str(), command.size(), 0);

        std::this_thread::sleep_for(std::chrono::milliseconds(100));
        // Read the output from the server
        std::array<char, 4096> buffer;
        memset(buffer.data(), 0, buffer.size());
        int bytes_read = recv(sockfd, buffer.data(), buffer.size() - 1, 0);
        ASSERT_GT(bytes_read, 0) << "No response received";
        
        std::string response(buffer.data(), bytes_read);
        EXPECT_EQ(response, expectedOutput[i]);
    }
    
    close(sockfd);
    // Cleanup any test files created during the test
    cleanupTestFiles();
}