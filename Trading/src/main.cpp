#include <curl/curl.h>
#include <iostream>

// Callback function for receiving response data
size_t WriteCallback(void* contents, size_t size, size_t nmemb, void* userp) {
    // We're not storing the response data, so just return the size
    return size * nmemb;
}

int main() {
    // Initialize curl
    CURL* curl = curl_easy_init();

    if (curl) {
        // Set the URL
        curl_easy_setopt(curl, CURLOPT_URL, "https://jsonplaceholder.typicode.com/posts/1");

        // Set callback function
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);

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
        }

        // Clean up
        curl_easy_cleanup(curl);
    }
    else {
        std::cerr << "Failed to initialize curl" << std::endl;
        return 1;
    }

    return 0;
}
