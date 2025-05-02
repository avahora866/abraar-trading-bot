cmake_minimum_required(VERSION 3.15)
project(TradingProject VERSION 1.0.0)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(CURL REQUIRED)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/Debug)

add_executable(Trading
    src/main.cpp
    src/AlpacaConfig.cpp
)

target_link_libraries(Trading PRIVATE CURL::libcurl)
target_include_directories(Trading PRIVATE ${CURL_INCLUDE_DIRS})

# cmake -DCMAKE_TOOLCHAIN_FILE=C:\Home\vcpkg\scripts\buildsystems\vcpkg.cmake ..
