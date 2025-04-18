#include "gtest/gtest.h"

// Forward declarations of test functions
extern void RegisterInsertURLTests();

int main(int argc, char **argv) {
    testing::InitGoogleTest(&argc, argv);
    RegisterInsertURLTests();
    return RUN_ALL_TESTS();
}