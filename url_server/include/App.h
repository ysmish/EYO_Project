//
// Created by A on 27/4/2025.
//

#ifndef ASP_APP_H
#define ASP_APP_H
#include <map>
#include <memory>
#include <string>
#include <InputHandler.h>
#include <PersistenceHandler.h>
#include <command.h>
#include <BloomFilter.h>

class App {
private:
    std::map<std::string, ICommand*> commands; // Map to hold the commands
    std::unique_ptr<BloomFilter> bloomFilter; // Bloom filter to store the data
    size_t port; // Port number for the server
    void handleClient(int socket); // Method to handle the client
public:
    App(size_t size,
        std::vector<std::unique_ptr<HashFunction>> hashFunction,
        std::unique_ptr<PersistenceHandler> persistenceHandler,
        size_t port);
    void run();
};


#endif //ASP_APP_H