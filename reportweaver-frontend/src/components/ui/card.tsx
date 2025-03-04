import { FC, ReactNode } from "react";

/**
 * Props for the Card component.
 * @property {ReactNode} children - The content inside the card.
 * @property {string} [className] - Optional additional styles.
 */
interface CardProps {
  children: ReactNode;
  className?: string;
}

/**
 * A reusable Card component that provides a styled container.
 *
 * @param {CardProps} props - Props containing children and optional className.
 * @returns {JSX.Element} A styled card container.
 */
export const Card: FC<CardProps> = ({ children, className = "" }) => {
  return (
    <div className={`bg-white shadow-md rounded-lg p-6 ${className}`}>
      {children}
    </div>
  );
};

/**
 * Props for Card section components (CardHeader, CardTitle, CardContent).
 * @property {ReactNode} children - The content inside the section.
 * @property {string} [className] - Optional additional styles.
 */
interface CardSectionProps {
  children: ReactNode;
  className?: string;
}

/**
 * A header section within a Card, typically used to display titles.
 *
 * @param {CardSectionProps} props - Props containing children and optional className.
 * @returns {JSX.Element} A styled card header.
 */
export const CardHeader: FC<CardSectionProps> = ({
  children,
  className = "",
}) => {
  return <div className={`border-b pb-2 mb-4 ${className}`}>{children}</div>;
};

/**
 * A title section within a Card, used for headings.
 *
 * @param {CardSectionProps} props - Props containing children and optional className.
 * @returns {JSX.Element} A styled card title.
 */
export const CardTitle: FC<CardSectionProps> = ({
  children,
  className = "",
}) => {
  return <h2 className={`text-lg font-semibold ${className}`}>{children}</h2>;
};

/**
 * A content section within a Card, used for main card content.
 *
 * @param {CardSectionProps} props - Props containing children and optional className.
 * @returns {JSX.Element} A styled card content area.
 */
export const CardContent: FC<CardSectionProps> = ({
  children,
  className = "",
}) => {
  return <div className={className}>{children}</div>;
};
