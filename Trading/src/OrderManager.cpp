#include <string>
#include "AlpacaEndpoint.cpp"
#include "Audit.cpp"
#include <nlohmann/json.hpp>

using namespace std;
using json = nlohmann::json;

class OrderManager
{
	public: 
		void buy(string ticker, string qty)
		{
			AlpacaEndpoint alpaca;
			json order = {
				{"type", "market"},
				{"time_in_force", "day"},
				{"symbol", ticker},
				{"qty", qty},
				{"side", "buy"}
			};
			int x = alpaca.call("https://paper-api.alpaca.markets/v2/orders", order.dump(), "POST");
			Audit::audit("Bought " + qty +  " shares of " + ticker);
		}

		void sell(string ticker, string qty)
		{
			AlpacaEndpoint alpaca;
			json order = {
				{"type", "market"},
				{"time_in_force", "day"},
				{"symbol", ticker},
				{"qty", qty},
				{"side", "sell"}
			};
			int x = alpaca.call("https://paper-api.alpaca.markets/v2/orders", order.dump(), "POST");
			Audit::audit("Sold " + qty + " shares of " + ticker);
		}
};
