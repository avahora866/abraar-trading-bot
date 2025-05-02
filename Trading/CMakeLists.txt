cmake_minimum_required(VERSION 3.15)
project(HttpClientProject VERSION 1.0.0)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(CURL REQUIRED)

add_executable(HttpClient
    src/main.cpp
    src/AlpacaConfig.cpp
)

target_link_libraries(HttpClient PRIVATE CURL::libcurl)
target_include_directories(HttpClient PRIVATE ${CURL_INCLUDE_DIRS})

# cmake -DCMAKE_TOOLCHAIN_FILE=C:\Home\vcpkg\scripts\buildsystems\vcpkg.cmake ..
