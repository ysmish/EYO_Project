#ifndef INSERT_COMMAND_H
#define INSERT_COMMAND_H

#include <vector>
#include <string>
#include <command.h>
#include <BloomFilter.h>

class InsertCommand : public ICommand {
private:
    BloomFilter* bloomFilter; // Pointer to the Bloom filter instance
public:
    InsertCommand(BloomFilter* bloomFilter);
    std::string execute(std::string url) override;
};
#endif // INSERT_COMMAND_H