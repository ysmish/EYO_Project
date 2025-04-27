//
// Created by A on 27/4/2025.
//

#ifndef ASP_APP_H
#define ASP_APP_H
#include <map>

class App{
private:
    std::map<std::string, ICommand> commands;
public:
    App(std::map<std::string, ICommand> commands);
    void run();
};


#endif //ASP_APP_H