import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import UserFormPage from "./pages/UserFormPage";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/user-form" element={<UserFormPage />} />
      </Routes>
    </Router>
  );
}

export default App;
