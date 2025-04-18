#ifndef ASP_PROJECT_HASHFUNCTION_H
#define ASP_PROJECT_HASHFUNCTION_H

#include <string>

class HashFunction {
public:
    virtual ~HashFunction() = default;
    virtual size_t hash(const std::string& input) const = 0;
};


#endif //ASP_PROJECT_HASHFUNCTION_H
