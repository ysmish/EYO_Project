#ifndef ASP_PROJECT_CONSOLEINPUTHANDLER_H
#define ASP_PROJECT_CONSOLEINPUTHANDLER_H

#include <InputHandler.h>
#include <string>
#include <vector>


class ConsoleInputHandler : public InputHandler {
public:
    ConsoleInputHandler() = default; // Default constructor
    output getInput() override; // Method to get input from the user
    fistLine getFistLine() override; // Method to get the first line of input
};


#endif //ASP_PROJECT_CONSOLEINPUTHANDLER_H
