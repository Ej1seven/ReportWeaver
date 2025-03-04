import { FC, InputHTMLAttributes } from "react";

/**
 * Props for the Input component, extending default HTML input attributes.
 * @property {string} [className] - Optional additional styles.
 */
interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  className?: string;
}

/**
 * A reusable Input component with default styles and Tailwind utility classes.
 *
 * @param {InputProps} props - Props containing standard input attributes and optional className.
 * @returns {JSX.Element} A styled input field.
 */
export const Input: FC<InputProps> = ({ className = "", ...props }) => {
  return (
    <input
      className={`w-full px-3 py-2 border rounded-md focus:ring focus:ring-indigo-200 focus:border-indigo-500 ${className}`}
      {...props}
    />
  );
};
