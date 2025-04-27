//
// Created by A on 18/4/2025.
//

#ifndef ASP_PROJECT_COMMAND_H
#define ASP_PROJECT_COMMAND_H
#include <string>

class ICommand {
public:
    virtual void execute(std::string) = 0;
};

#endif //ASP_PROJECT_COMMAND_H
