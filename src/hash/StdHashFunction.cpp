//
// Created by A on 18/4/2025.
//

#include "StdHashFunction.h"
#include <stdexcept>

std::hash<std::string> StdHashFunction::hashFunction;

StdHashFunction::StdHashFunction(size_t rounds) : rounds(rounds) {
    if (rounds == 0) {
        throw std::invalid_argument("Number of rounds must be greater than 0");
    }
}
size_t StdHashFunction::hash(const std::string &key) const {
    // Check if the key is empty
    if (key.empty()) {
        throw std::invalid_argument("Key cannot be empty");
    }
    // Perform the hashing operation for the specified number of rounds
    size_t hashValue = hashFunction(key);
    for (size_t i = 1; i < rounds; ++i) {
        hashValue = hashFunction(std::to_string(hashValue));
    }
    return hashValue;
}