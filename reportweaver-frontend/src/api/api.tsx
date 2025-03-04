export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

/**
 * Sends a request to stop all active Selenium sessions on the server.
 *
 * @returns {Promise<string>} A message indicating success or failure.
 */
export const stopSeleniumSessions = async (): Promise<string> => {
  try {
    // Send a POST request to the backend to stop Selenium sessions
    const response = await fetch(`${API_BASE_URL}/api/server/stop-selenium`, {
      method: "POST", // HTTP method used for stopping the session
    });

    // Check if the response is unsuccessful, throw an error if so
    if (!response.ok) {
      throw new Error(`Error: ${response.statusText}`);
    }

    // Return the response text upon successful completion
    return await response.text();
  } catch (error) {
    // Return a failure message in case of an error
    return "Failed to stop Selenium sessions.";
  }
};
