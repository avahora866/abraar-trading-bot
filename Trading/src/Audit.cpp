using namespace std;
#include <string>
#include <iostream>
#include <ctime>
#include <iomanip>
#include <cctype>


class Audit
{
public:
	static void audit(string message)
	{
		time_t result = time(NULL);
		char currentTime[26];
		ctime_s(currentTime, sizeof currentTime, &result);
		cout << "Log [" << trim(currentTime) << "] " << trim(message) << endl;
	}
private:
    static string trim(const std::string& str)
    {
        size_t start = 0;
        while (start < str.length() && std::isspace(static_cast<unsigned char>(str[start]))) {
            ++start;
        }

        size_t end = str.length();
        while (end > start && std::isspace(static_cast<unsigned char>(str[end - 1]))) {
            --end;
        }

        return str.substr(start, end - start);
    }
};

