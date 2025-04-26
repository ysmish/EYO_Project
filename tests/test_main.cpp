#include "gtest/gtest.h"

// Forward declarations of test functions
extern void RegisterInsertURLTests();
extern void RegisterBloomFilterInitializationTests();

int main(int argc, char **argv) {
    testing::InitGoogleTest(&argc, argv);
    RegisterBloomFilterInitializationTests();
    return RUN_ALL_TESTS();
}