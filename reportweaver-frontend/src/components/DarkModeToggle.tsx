import { useState, useEffect } from "react";
import lightModeIcon from "../assets/icons/light-mode.png";
import darkModeIcon from "../assets/icons/dark-mode.png";

/**
 * DarkModeToggle component - Handles toggling between light and dark mode.
 *
 * @returns {JSX.Element} A button that toggles dark mode and updates localStorage.
 */
export default function DarkModeToggle(): JSX.Element {
  // State to track the current theme (light/dark)
  const [darkMode, setDarkMode] = useState<boolean>(() => {
    return localStorage.getItem("theme") === "dark";
  });

  // Effect to apply or remove the dark mode class and store preference
  useEffect(() => {
    if (darkMode) {
      document.documentElement.classList.add("dark");
      localStorage.setItem("theme", "dark");
    } else {
      document.documentElement.classList.remove("dark");
      localStorage.setItem("theme", "light");
    }
  }, [darkMode]);

  return (
    <button
      onClick={() => setDarkMode((prev) => !prev)}
      className="p-2 rounded transition"
    >
      <img
        src={darkMode ? lightModeIcon : darkModeIcon}
        alt={darkMode ? "Light Mode" : "Dark Mode"}
        className="w-8 h-8 mr-2 cursor-pointer"
      />
    </button>
  );
}
