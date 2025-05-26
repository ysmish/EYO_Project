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

// Test 1: check connection after idle period, as the server should not stop running
TEST(TCPConnectionTest, ConnectionAfterIdlePeriod) {
    
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
}

// Test 2: make sure server supports large data transmission
TEST(TCPConnectionTest, LargeDataTransmission) {
    
    // Create a very long (that's what she said), URL, (but still valid)
    std::string longDomain = "www.";
    for (int i = 0; i < 200; i++) {
        longDomain += "aaaaaaaaaaa";
    }
    longDomain += ".com";
    
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
    std::string cmd1 = "POST " + longDomain + "\n";
    send(sockfd, cmd1.c_str(), cmd1.length(), 0);
    
    // Wait for response
    std::array<char, 4096> buffer;
    memset(buffer.data(), 0, buffer.size());
    int bytes_read = recv(sockfd, buffer.data(), buffer.size() - 1, 0);
    ASSERT_GT(bytes_read, 0) << "No initial response received";


    std::string response(buffer.data(), bytes_read);
    EXPECT_TRUE(response.find("201 Created") != std::string::npos) 
               << "Invalid response after idle period";
    
    close(sockfd);

}

// Test 3: check behavior for partial message
TEST(TCPConnectionTest, PartialMessage) {
    
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

}
