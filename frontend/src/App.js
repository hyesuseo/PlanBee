import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import SignIn from "./pages/SignIn";
import ToDoList from "./pages/ToDoList";
import Archive from "./pages/Archive";
import Calendar from "./pages/Calendar";
import Social from "./pages/Social";
import Board from "./pages/Board";
import BoardDetail from "./pages/BoardDetail";
import BoardWrite from "./pages/BoardWrite";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<SignIn />} />
        <Route path="/todolist" element={<ToDoList />} />
        <Route path="/archive" element={<Archive />} />
        <Route path="/calendar" element={<Calendar />} />
        <Route path="/social" element={<Social />} />
        <Route path="/boardList/:group" element={<Board />} />
        <Route path="/boardOne/:id" element={<BoardDetail />} />
        <Route path="/boardWrite" element={<BoardWrite />} />
        <Route path="/login" element={<SignIn />} />
      </Routes>
    </Router>
  );
}

export default App;
