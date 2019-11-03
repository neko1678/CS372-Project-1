default: ChatClient.cpp
	g++ -std=c++11 -pthread ChatClient.cpp
clean:
	rm ChatClient
