#include <string>
#include "AlpacaEndpoint.cpp"
#include "Audit.cpp"
#include <nlohmann/json.hpp>

using namespace std;
using json = nlohmann::json;

class OrderManager
{
	public: 
		bool buy(string ticker, string qty)
		{
			AlpacaEndpoint alpaca;
			json order = {
				{"type", "market"},
				{"time_in_force", "day"},
				{"symbol", ticker},
				{"qty", qty},
				{"side", "buy"}
			};
			//int x = alpaca.call("https://paper-api.alpaca.markets/v2/orders", "{\"type\":\"market\",\"time_in_force\":\"day\",\"symbol\":\"AAPL\",\"qty\":\"2\",\"side\":\"buy\"}", "POST");
			int x = alpaca.call("https://paper-api.alpaca.markets/v2/orders", order.dump(), "POST");
			return true;
		}
};
