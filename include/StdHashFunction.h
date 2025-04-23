//
// Created by A on 18/4/2025.
//

#ifndef ASP_PROJECT_STDHASHFUNCTION_H
#define ASP_PROJECT_STDHASHFUNCTION_H
#include "HashFunction.h"

class StdHashFunction : public HashFunction {
private:
    size_t rounds;
public:
    StdHashFunction(size_t rounds = 1);
    size_t hash(const std::string& input) const override;
};


#endif //ASP_PROJECT_STDHASHFUNCTION_H
