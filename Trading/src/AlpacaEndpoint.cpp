#include <curl/curl.h>
#include <iostream>
#include "AlpacaConfig.h"

using namespace std;


class AlpacaEndpoint {
    // Callback function for receiving response data
    static size_t WriteCallback(void* contents, size_t size, size_t nmemb, void* userp) {
        size_t totalSize = size * nmemb;
        std::string* response = static_cast<std::string*>(userp);
        response->append(static_cast<char*>(contents), totalSize);
        return totalSize;
    }

public:
    int call(string endpoint, string jsonData, string method)
    {
        // Initialize curl
        CURL* curl = curl_easy_init();

        if (curl) {
            // Set the URL
            curl_easy_setopt(curl, CURLOPT_URL, endpoint.c_str());

            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, method.c_str());

            // Headers
            struct curl_slist* headers = NULL;
            headers = curl_slist_append(headers, "content-type: application/json");
            headers = curl_slist_append(headers, "accept: application/json");

            std::string apiKeyHeader = "APCA-API-KEY-ID: " + AlpacaConfig::API_KEY;
            headers = curl_slist_append(headers, apiKeyHeader.c_str());
            std::string apiSecretKeyHeader = "APCA-API-SECRET-KEY: " + AlpacaConfig::API_SECRET;
            headers = curl_slist_append(headers, apiSecretKeyHeader.c_str());

            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

            curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonData.c_str());

            // Set callback function
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);

            std::string response_data;
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response_data);


            // Perform the request
            CURLcode res = curl_easy_perform(curl);

            // Check for errors
            if (res != CURLE_OK) {
                std::cerr << "curl_easy_perform() failed: " << curl_easy_strerror(res) << std::endl;
            }
            else {
                // Get HTTP response code
                long http_code = 0;
                curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &http_code);
                std::cout << "HTTP response code: " << http_code << std::endl;
                std::cout << response_data;
            }

            // Clean up
            curl_slist_free_all(headers);
            curl_easy_cleanup(curl);
        }
        else {
            std::cerr << "Failed to initialize curl" << std::endl;
            return 1;
        }

        return 0;
    }
};
