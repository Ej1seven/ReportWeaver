/**
 * GoogleDocButton component - Displays a button linking to a generated Google Doc.
 *
 * @param {string} documentUrl - The URL of the generated Google Document.
 * @returns {JSX.Element} A styled button with an icon linking to the document.
 */
const GoogleDocButton: React.FC<{ documentUrl: string }> = ({
  documentUrl,
}) => {
  return (
    <div className="text-center">
      {/* Informational text about the Google Doc */}
      <p className="text-gray-700 text-lg">
        The Google Doc was successfully created. Please click the icon below to
        view.
      </p>

      {/* Clickable link to open the Google Document in a new tab */}
      <a
        href={documentUrl}
        target="_blank"
        rel="noopener noreferrer"
        className="flex items-center justify-center space-x-2 text-blue-600 hover:text-blue-800 mt-4"
      >
        <img
          src="src/assets/icons/docs.png"
          alt="Google Docs Icon"
          width={75}
          height={75}
        />
      </a>
    </div>
  );
};

export default GoogleDocButton;
