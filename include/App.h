//
// Created by A on 27/4/2025.
//

#ifndef ASP_APP_H
#define ASP_APP_H
#include <map>
#include <memory>
#include <string>
#include <InputHandler.h>
#include <command.h>

class App{
private:
    std::map<std::string, std::unique_ptr<ICommand>> commands; // Map of commands to be executed
    std::unique_ptr<InputHandler> inputHandler; // Input handler to get user input
public:
    App(); // Constructor to initialize the App with commands and input handler
    void run();
};


#endif //ASP_APP_H