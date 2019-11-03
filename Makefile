default: ChatClient.cpp
	g++ -std=c++0x -pthread ChatClient.cpp
clean:
	rm ChatClient
