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
#include <pthread.h>
#include <vector>

// Structure to pass data to thread
struct ClientData {
    int socket;
    App* app;
};

class App {
private:
    std::map<std::string, ICommand*> commands; // Map to hold the commands
    std::unique_ptr<BloomFilter> bloomFilter; // Bloom filter to store the data
    size_t port; // Port number for the server
    std::vector<pthread_t> threads; // Vector to store thread IDs
    pthread_mutex_t bloomFilterMutex; // Mutex for thread-safe access to bloomFilter
    
    void handleClient(int socket); // Method to handle the client
    static void* threadHandler(void* arg); // Static method for thread handling

public:
    App(size_t size,
        std::vector<std::unique_ptr<HashFunction>> hashFunction,
        std::unique_ptr<PersistenceHandler> persistenceHandler,
        size_t port);
    ~App(); // Destructor to clean up threads and mutex
    void run();
};

#endif //ASP_APP_H