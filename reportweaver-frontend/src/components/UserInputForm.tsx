import React, { FC, useState, useEffect } from "react";
import { handleApiError } from "../api/errorHandler";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Button } from "../components/ui/button";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../components/ui/card";

/**
 * Props definition for the UserInputForm component.
 */
interface UserInputFormProps {
  onSubmit: () => void;
  onStatusUpdate: (status: string) => void;
  onButtonTextUpdate: (status: string) => void;
  onDocumentUrlUpdate: (status: string | null) => void;
}

/**
 * UserInputForm component - Handles user input for generating a report.
 *
 * @param {UserInputFormProps} props - Contains callback functions for handling submission, status updates, and document URL updates.
 * @returns {JSX.Element} A form for user input with styled components.
 */
const UserInputForm: FC<UserInputFormProps> = ({
  onSubmit,
  onStatusUpdate,
  onButtonTextUpdate,
  onDocumentUrlUpdate,
}) => {
  // State to manage form inputs
  const [formData, setFormData] = useState({
    websiteName: "",
    ssoUsername: "",
    password: "",
    email: "",
  });

  // Dark mode styling variables
  const darkInputClasses =
    "dark:bg-gray-700 dark:text-white dark:border-gray-600";
  const darkTextClass = "dark:text-gray-300";
  const darkButtonClasses =
    "dark:bg-gray-900 dark:text-white dark:hover:bg-gray-700";

  /**
   * Establishes a WebSocket connection to receive Selenium status updates.
   * Updates the status in the parent component when messages are received.
   */
  useEffect(() => {
    const socket = new WebSocket("ws://localhost:8080/ws/selenium-status");

    socket.onmessage = (event) => {
      onStatusUpdate(event.data); // Updates status in parent component
    };

    socket.onclose = () => {
      console.log("WebSocket connection closed");
    };

    return () => {
      socket.close(); // Cleanup WebSocket connection when component unmounts
    };
  }, []); // Runs only once on component mount

  /**
   * Handles input field changes and updates state dynamically.
   *
   * @param {React.ChangeEvent<HTMLInputElement>} e - The event containing the updated input field values.
   */
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  /**
   * Handles form submission by sending user data to the backend API.
   *
   * @param {React.FormEvent} e - The form event to prevent default submission behavior.
   */
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit();

    try {
      const response = await fetch("http://localhost:8080/", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          website: formData.websiteName,
          username: formData.ssoUsername,
          password: formData.password,
          email: formData.email,
        }),
      });

      if (response.ok) {
        const documentId = await response.text();
        const documentUrl =
          documentId === "Processing"
            ? null
            : `https://docs.google.com/document/d/${documentId}/edit`;

        console.log("Document URL:", documentUrl);
        onDocumentUrlUpdate(documentUrl);
        onButtonTextUpdate("Done");
        resetForm();
      } else {
        onStatusUpdate(handleApiError(response.status));
        onButtonTextUpdate("Close");
        onDocumentUrlUpdate(null);
        console.error("Failed to submit report request");
      }
    } catch (error) {
      console.error("Error submitting report request", error);
    }
  };

  /**
   * Resets the form fields to their initial state.
   */
  const resetForm = () => {
    setFormData({ websiteName: "", ssoUsername: "", password: "", email: "" });
  };

  return (
    <div className="flex justify-center items-center">
      <Card className="w-full max-w-md p-6 bg-white dark:bg-gray-800 shadow-md rounded-lg">
        <CardHeader>
          <CardTitle>Generate Report</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit}>
            {/* Website Name Input */}
            <Label className={darkTextClass}>Website Name</Label>
            <Input
              type="text"
              name="websiteName"
              value={formData.websiteName}
              onChange={handleChange}
              required
              className={darkInputClasses}
            />

            {/* SSO Username Input */}
            <Label className={darkTextClass}>SSO Username</Label>
            <Input
              type="text"
              name="ssoUsername"
              value={formData.ssoUsername}
              onChange={handleChange}
              required
              className={darkInputClasses}
            />

            {/* Password Input */}
            <Label className={darkTextClass}>Password</Label>
            <Input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              className={darkInputClasses}
            />

            {/* Email Input */}
            <Label className={darkTextClass}>Email</Label>
            <Input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              className={darkInputClasses}
            />

            {/* Submit Button */}
            <Button
              type="submit"
              className={`mt-4 w-full cursor-pointer ${darkButtonClasses}`}
            >
              Submit
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default UserInputForm;
