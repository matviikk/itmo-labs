add_library(vtpc SHARED vtpc.c)
set_target_properties(vtpc PROPERTIES OUTPUT_NAME "vtpc")

add_library(vtpc_static STATIC vtpc.c)
set_target_properties(vtpc_static PROPERTIES OUTPUT_NAME "vtpc")

target_include_directories(vtpc PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
target_include_directories(vtpc_static PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

target_compile_options(vtpc PRIVATE -Wall -Wextra -Wpedantic)
target_compile_options(vtpc_static PRIVATE -Wall -Wextra -Wpedantic)
