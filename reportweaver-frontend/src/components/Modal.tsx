import React, { ReactNode } from "react";

/**
 * Props for the Modal component.
 * @property {ReactNode} [children] - Optional content inside the modal.
 * @property {() => void} onClose - Function to handle modal closure.
 * @property {string} statusText - WebSocket status message from UserFormPage.
 */
interface ModalProps {
  children?: ReactNode;
  onClose: () => void;
  statusText: string; // ✅ Receives WebSocket status from UserFormPage
}

/**
 * Modal component - A reusable modal for displaying status messages and additional content.
 *
 * @param {ModalProps} props - Props containing children, statusText, and onClose handler.
 * @returns {JSX.Element} A styled modal overlay.
 */
const Modal: React.FC<ModalProps> = ({ children, statusText }) => {
  return (
    <div className="fixed inset-0 flex justify-center items-center z-50">
      <div className="bg-white dark:bg-gray-900 p-6 rounded-lg shadow-lg relative max-w-md w-full">
        {/* ✅ Display WebSocket status message */}
        <p className="text-gray-700 dark:text-white text-lg text-center pt-8">
          {statusText}
        </p>
        {children}
      </div>
    </div>
  );
};

export default Modal;
