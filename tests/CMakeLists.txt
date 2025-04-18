include(FetchContent)
FetchContent_Declare(
        googletest
        URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
)
FetchContent_MakeAvailable(googletest)



# Find all test files
file(GLOB TEST_SOURCES *.cpp)

# Create single test executable
add_executable(run_tests ${TEST_SOURCES})
target_include_directories(run_tests PRIVATE
        ${CMAKE_SOURCE_DIR}/include
)
target_link_libraries(run_tests PRIVATE
        project_lib
        gtest_main
)