add_executable(src lab1.c)

target_include_directories(src PUBLIC
	"${PROJECT_BINARY_DIR}"
	"${PROJECT_SOURCE_DIR}/include"
)

target_link_libraries(src PUBLIC lab1)
target_link_libraries(src PUBLIC util)
