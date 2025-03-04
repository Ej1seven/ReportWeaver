import { FC, ButtonHTMLAttributes } from "react";

/**
 * ButtonProps extends the default HTML button attributes
 * and allows an optional `className` for custom styling.
 */
interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  className?: string; // Allows passing custom Tailwind classes
}

/**
 * A reusable Button component with default styles.
 *
 * @param {string} className - Optional additional styles.
 * @param {React.ReactNode} children - Content inside the button.
 * @param {ButtonHTMLAttributes<HTMLButtonElement>} props - Additional button props.
 *
 * @returns {JSX.Element} A styled button element.
 */
export const Button: FC<ButtonProps> = ({
  className = "",
  children,
  ...props
}) => {
  return (
    <button
      className={`w-full px-4 py-2 bg-[var(--color-maroon)] text-white rounded-md hover:bg-[var(--color-darkMaroon)] transition ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};
