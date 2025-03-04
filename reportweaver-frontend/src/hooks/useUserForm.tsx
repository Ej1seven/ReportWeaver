import { useState, Dispatch, SetStateAction } from "react";
import { stopSeleniumSessions } from "../api/api";

/**
 * Defines the shape of the useUserForm hook return value.
 */
interface UseUserFormReturnType {
  isModalOpen: boolean;
  status: string;
  buttonText: string;
  documentUrl: string | null;
  handleSubmit: () => void;
  handleStatusUpdate: (newStatus: string) => void;
  handleButtonText: (newButtonText: string) => void;
  handleDocumentUrl: (documentUrlText: string | null) => void;
  handleStopServer: (shouldCloseModal: boolean) => Promise<void>;
  setIsModalOpen: Dispatch<SetStateAction<boolean>>;
}

/**
 * Custom hook for managing user form state and interactions.
 * @returns {UseUserFormReturnType} Hook return values and methods
 */
export const useUserForm = (): UseUserFormReturnType => {
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [status, setStatus] = useState<string>("Waiting for updates...");
  const [buttonText, setButtonText] = useState<string>("Cancel");
  const [documentUrl, setDocumentUrl] = useState<string | null>(null);

  /**
   * Handles form submission by resetting the document URL and updating status.
   */
  const handleSubmit = (): void => {
    setDocumentUrl(null);
    setButtonText("Cancel");
    setStatus("Processing...");
    setIsModalOpen(true);
  };

  /**
   * Updates the status message.
   * @param newStatus - The new status message.
   */
  const handleStatusUpdate = (newStatus: string): void => {
    setStatus(newStatus);
  };

  /**
   * Updates the button text.
   * @param newButtonText - The new button label.
   */
  const handleButtonText = (newButtonText: string): void => {
    setButtonText(newButtonText);
  };

  /**
   * Updates the document URL or stops the server if the URL is null.
   * @param documentUrlText - The new document URL.
   */
  const handleDocumentUrl = (documentUrlText: string | null): void => {
    documentUrlText ? setDocumentUrl(documentUrlText) : handleStopServer(false);
  };

  /**
   * Stops the Selenium server and updates UI accordingly.
   * @param shouldCloseModal - Determines if the modal should be closed.
   */
  const handleStopServer = async (shouldCloseModal: boolean): Promise<void> => {
    setStatus("Stopping Selenium WebDrivers...");
    const response: string = await stopSeleniumSessions();
    setStatus(response);
    setIsModalOpen(!shouldCloseModal); // Close modal based on flag
    setDocumentUrl(null);
  };

  return {
    isModalOpen,
    status,
    buttonText,
    documentUrl,
    handleSubmit,
    handleStatusUpdate,
    handleButtonText,
    handleDocumentUrl,
    handleStopServer,
    setIsModalOpen,
  };
};
