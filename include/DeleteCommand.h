#ifndef DELETE_COMMAND_H
#define DELETE_COMMAND_H

#include <vector>
#include <string>
#include <command.h>
#include <BloomFilter.h>

class DeleteCommand : public ICommand {
private:
    BloomFilter* bloomFilter; // Pointer to the Bloom filter instance
public:
    DeleteCommand(BloomFilter* bloomFilter);
    std::string execute(std::string url) override;
};
#endif // DELETE_COMMAND_H
