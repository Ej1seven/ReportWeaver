import { useState, useEffect } from "react";

/**
 * Interface for the WebSocket hook return type.
 * @property {string[]} messages - Stores received WebSocket messages.
 * @property {(message: string) => void} sendMessage - Function to send a message through the WebSocket.
 */
interface WebSocketHook {
  messages: string[];
  sendMessage: (message: string) => void;
}

/**
 * Custom React hook for handling WebSocket connections.
 *
 * @param {string} url - The WebSocket server URL.
 * @returns {WebSocketHook} An object containing received messages and a function to send messages.
 */
export const useWebSocket = (url: string): WebSocketHook => {
  const [messages, setMessages] = useState<string[]>([]);
  let socket: WebSocket | null = null;

  useEffect(() => {
    // Initialize the WebSocket connection
    socket = new WebSocket(url);

    // Handle incoming WebSocket messages
    socket.onmessage = (event: MessageEvent) => {
      setMessages((prev) => [...prev, event.data]);
    };

    // Cleanup function to close the WebSocket connection on unmount
    return () => {
      socket?.close();
    };
  }, [url]);

  /**
   * Sends a message through the WebSocket connection.
   *
   * @param {string} message - The message to send.
   */
  const sendMessage = (message: string): void => {
    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(message);
    }
  };

  return { messages, sendMessage };
};
