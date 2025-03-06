# ReportWeaver

ReportWeaver is a full-stack application designed to automate the retrieval and processing of accessibility reports. The backend, built with Java Spring Boot, executes Selenium requests to extract data from PopeTech, a web accessibility and reporting tool. Once the data is collected, a Google Sheet is created on the backend using the Google Docs API, and the generated sheet URL is then provided to the frontend for user access.

The frontend is developed with React and styled using Tailwind CSS, providing a very simple and responsive UI for users to initiate and track report processing.
## Table of Contents

- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [Environment Variables](#environment-variables)
- [Running the Application](#running-the-application)
- [Demo Video](#demo-video)
- [Contributing](#contributing)
- [License](#license)

## Project Structure

```
ReportWeaver/
├── reportweaver-backend/  # Backend (Spring Boot, Selenium, Google Docs API)
└── reportweaver-frontend/ # Frontend (React, Tailwind CSS, Vite)
```

## Prerequisites

Ensure you have the following installed on your system:

- **Backend:**
  - [Java 17+](https://adoptopenjdk.net/)
  - [Maven](https://maven.apache.org/)
  - [Chromedriver](https://developer.chrome.com/docs/chromedriver/)(for Selenium operations)

- **Frontend:**
  - [Node.js](https://nodejs.org/) (version 16+ recommended)
  - [npm](https://www.npmjs.com/) (comes with Node.js)

## Backend Setup

1. **Navigate to the Backend Directory**:
   ```sh
   cd reportweaver-backend
   ```

2. **Set Up Environment Variables**:
   Create a `.env` file in the `reportweaver-backend` directory with the following content:
   ```env
   GOOGLE_CREDENTIALS_JSON="/path/to/your/google-credentials.json"
   POPE_TECH_URL="https://login.pope.tech/login?redirect=/"
   LOCAL_HOST_URL="http://localhost:5173"
   ```
   Adjust the values based on your local or production database settings.

3. **Build and Run the Backend**:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```
   The backend server should now be running at `http://localhost:8080`.

## Frontend Setup

1. **Navigate to the Frontend Directory**:
   ```sh
   cd reportweaver-frontend
   ```

2. **Install Dependencies**:
   ```sh
   npm install
   ```

3. **Configure Environment Variables**:
   Create a `.env` file in the `reportweaver-frontend` directory with the following content:
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   ```
   Adjust the API URL if your backend is hosted elsewhere.

4. **Run the Frontend Application**:
   ```sh
   npm run dev
   ```
   The frontend should now be accessible at `http://localhost:5173`.

## Running the Application

1. **Start the Backend**:
   ```sh
   cd reportweaver-backend
   mvn spring-boot:run
   ```

2. **Start the Frontend**:
   ```sh
   cd reportweaver-frontend
   npm run dev
   ```

Visit `http://localhost:5173` in your browser to access the application.

## Demo Video

Watch the demo video below to see ReportWeaver in action:

  <iframe width="560" height="315" src="https://www.youtube.com/embed/5SJRLgQF17M" frameborder="0" allowfullscreen></iframe>

## Contributing

We welcome contributions! Please fork the repository and create a pull request with your changes. Ensure your code adheres to project standards and includes appropriate documentation.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

