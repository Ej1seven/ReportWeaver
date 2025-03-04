import React from "react";
import UserInputForm from "../components/UserInputForm";
import Modal from "../components/Modal";
import GoogleDocButton from "../components/GoogleDocButton";
import { useUserForm } from "../hooks/useUserForm";
import DarkModeToggle from "../components/DarkModeToggle";

/**
 * Main page for handling the user form and displaying results.
 */
const UserFormPage: React.FC = () => {
  // ✅ Use custom hook for form state management
  const {
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
  } = useUserForm();

  return (
    <div className="relative flex flex-col items-center justify-center min-h-screen bg-white dark:bg-gray-900 dark:text-white">
      {/* Dark Mode Toggle Button in the Top Right Corner */}
      <div className="absolute top-4 right-4">
        <DarkModeToggle />
      </div>

      {/* User Input Form Card */}
      <div className="relative w-full max-w-md shadow-lg rounded-lg p-6 z-10 dark:bg-gray-800">
        <UserInputForm
          onSubmit={handleSubmit}
          onStatusUpdate={handleStatusUpdate}
          onButtonTextUpdate={handleButtonText}
          onDocumentUrlUpdate={handleDocumentUrl}
        />
      </div>

      {/* ✅ Background Dim Effect when Modal is Open */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-opacity-30 backdrop-blur-sm z-40"></div>
      )}

      {/* ✅ Modal Dialog for Status Updates and Actions */}
      {isModalOpen && (
        <div className="fixed inset-0 flex justify-center items-center z-50">
          <Modal onClose={() => setIsModalOpen(false)} statusText={status}>
            <div className="bg-white dark:bg-gray-900 p-6 rounded-lg flex flex-col items-center space-y-4">
              {/* Display Google Docs Button if Document is Available */}
              {documentUrl && documentUrl.trim() !== "" && (
                <GoogleDocButton documentUrl={documentUrl} />
              )}

              {/* Loading Spinner when Processing */}
              {buttonText !== "Done" && (
                <div className="w-12 h-12 border-4 dark:border-gray-600 border-gray-300 border-t-[var(--color-maroon)] dark:border-t-[var(--color-maroon)] rounded-full animate-spin"></div>
              )}

              {/* Action Button for Stopping Server or Closing Modal */}
              <button
                className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
                onClick={() => handleStopServer(true)}
              >
                {buttonText}
              </button>
            </div>
          </Modal>
        </div>
      )}
    </div>
  );
};

export default UserFormPage;
