/**
 * Handles API errors by returning user-friendly error messages based on HTTP status codes.
 *
 * @param {number} status - The HTTP status code received from the server.
 * @returns {string} A descriptive error message corresponding to the status code.
 */
export const handleApiError = (status: number): string => {
  switch (status) {
    case 400:
      return "Bad Request: Invalid input data. Please check the form fields.";
    case 401:
      return "Unauthorized: Invalid credentials provided.";
    case 403:
      return "Forbidden: You do not have the required permissions.";
    case 404:
      return "Not Found: The requested endpoint does not exist.";
    case 408:
      return "Request Timeout: The server took too long to respond.";
    case 429:
      return "Too Many Requests: Slow down, you have exceeded the request limit.";
    case 500:
      return "Internal Server Error: Something went wrong on the backend.";
    case 502:
      return "Bad Gateway: The server received an invalid response from an upstream service.";
    case 503:
      return "Service Unavailable: The backend is overloaded or under maintenance.";
    case 504:
      return "Gateway Timeout: The backend took too long to respond.";
    default:
      // Handle any unrecognized status codes
      return `Unexpected Error: HTTP ${status}`;
  }
};
