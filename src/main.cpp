#include <iostream>
#include <App.h>
#include <map>
#include <string>
#include <memory>
#include <InsertCommand.h>
#include <IsFilteredCommand.h>
#include <FilePersistenceHandler.h>
#include <ConsoleInputHandler.h>


int main() {
    // Create a map to hold the commands
    std::map<std::string, std::unique_ptr<ICommand>> commands;
    commands["1"] = std::make_unique<InsertCommand>(nullptr); // InsertCommand will be initialized later

    ConsoleInputHandler inputHandler; // Create an instance of ConsoleInputHandler
    // Create the application instance
    App app(
        std::make_unique<ConsoleInputHandler>(), // Create a unique pointer to ConsoleInputHandler
        std::make_unique<FilePersistenceHandler>("../data/bloom_filter_data.txt") // Create a unique pointer to FilePersistenceHandler with the file name
    );
    
    // Run the application
    app.run();

    return 0;
}