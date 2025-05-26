#ifndef ASP_PROJECT_INPUTHANDLER_H
#define ASP_PROJECT_INPUTHANDLER_H

#include <string>
#include <vector>
struct output {
    std::string command; // Command to be executed
    std::string url; // URL to be processed
};

struct fistLine {
    size_t size; // Size of the Bloom filter
    std::vector<size_t> rounds; // Number of rounds for each hash function
};


class InputHandler {
public:
    virtual output getInput() = 0; // Method to get input from the user
    virtual fistLine getFistLine() = 0; // Method to get the first line of input
};


#endif //ASP_PROJECT_INPUTHANDLER_H
