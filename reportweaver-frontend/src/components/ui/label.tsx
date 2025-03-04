import { FC, LabelHTMLAttributes } from "react";

/**
 * Props for the Label component, extending default HTML label attributes.
 * @property {string} [className] - Optional additional styles.
 */
interface LabelProps extends LabelHTMLAttributes<HTMLLabelElement> {
  className?: string;
}

/**
 * A reusable Label component for form elements.
 *
 * @param {LabelProps} props - Props containing standard label attributes and optional className.
 * @returns {JSX.Element} A styled label element.
 */
export const Label: FC<LabelProps> = ({
  className = "",
  children,
  ...props
}) => {
  return (
    <label
      className={`text-sm font-medium text-gray-700 ${className}`}
      {...props}
    >
      {children}
    </label>
  );
};
