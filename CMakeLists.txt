cmake_minimum_required(VERSION 3.16)
project(LauncherClient)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include_directories(
        ${CMAKE_SOURCE_DIR}/include
        ${CMAKE_SOURCE_DIR}/libs/win/gcc810_x64_dll/mswu
)

link_directories(
        ${CMAKE_SOURCE_DIR}/libs/win/gcc810_x64_dll
)


file(GLOB SOURCES sources/*.cpp)

add_executable(${PROJECT_NAME} ${SOURCES})

set(wxWidgets_STATIC_LIBRARIES
        ${CMAKE_SOURCE_DIR}/libs/win/gcc810_x64_dll/libwxmsw32u_core.a
        ${CMAKE_SOURCE_DIR}/libs/win/gcc810_x64_dll/libwxbase32u.a
        ${CMAKE_SOURCE_DIR}/libs/win/gcc810_x64_dll/libwxpng.a
        ${CMAKE_SOURCE_DIR}/libs/win/gcc810_x64_dll/libwxzlib.a
)

target_link_libraries(${PROJECT_NAME} ${wxWidgets_STATIC_LIBRARIES})