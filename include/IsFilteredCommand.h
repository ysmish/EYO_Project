#ifndef ISFILTERED_COMMAND_H
#define ISFILTERED_COMMAND_H

#include <vector>
#include <string>
#include <BloomFilter.h>
#include <command.h>

class IsFilteredCommand : public ICommand {
private:
    BloomFilter* bloomFilter; // Pointer to the Bloom filter instance
public:
    IsFilteredCommand(BloomFilter* bloomFilter);
    void execute(std::string url) override;
};
#endif // ISFILTERED_COMMAND_H