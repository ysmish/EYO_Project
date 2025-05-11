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

class App {
private:
    std::unique_ptr<InputHandler> inputHandler; // Input handler to get user input
    std::unique_ptr<PersistenceHandler> persistenceHandler; // Bloom filter to store the data
    std::map<std::string, ICommand*> commands; // Map to hold the commands
public:
    App(std::unique_ptr<InputHandler> inputHandler, std::unique_ptr<PersistenceHandler> persistenceHandler);
    void run();
};


#endif //ASP_APP_H