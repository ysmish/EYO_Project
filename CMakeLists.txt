cmake_minimum_required(VERSION 3.14)
project(MyCppProject VERSION 1.0)

set(CMAKE_CXX_STANDARD 17)
enable_testing()

file(GLOB_RECURSE SOURCES ${CMAKE_SOURCE_DIR}/src/*.cpp)

# Library target
add_library(project_lib ${SOURCES})
target_include_directories(project_lib PUBLIC
        ${CMAKE_SOURCE_DIR}/include
)

# Include subdirectories
add_subdirectory(tests)

add_executable(main_app src/main.cpp)
target_link_libraries(main_app PRIVATE project_lib)