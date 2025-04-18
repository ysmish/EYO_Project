#include "gtest/gtest.h"

// Forward declarations of test functions
extern void RegisterInsertURLTests();
extern void RegisterBloomFilterTests();

int main(int argc, char **argv) {
    testing::InitGoogleTest(&argc, argv);
    RegisterBloomFilterTests();
    return RUN_ALL_TESTS();
}