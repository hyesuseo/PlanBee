import Sidebar from "../components/SideBar";
import Banner from "../components/Banner";
import CalCom from "../components/CalCom";
import CurStreak from "../components/CurStreak";
import MaxStreak from "../components/MaxStreak";
import "../css/Main.css";
import "../css/Calendar.css";

const Calendar = () => {
  return (
    <div className="main_container">
      <Banner />
      <div className="sidebar_and_content">
        <Sidebar />
        <div className="main_content">
          <div className="calendar_container">
            <div className="calendar_boxL">
              <div className="calendar_box_test">
                <CalCom />
              </div>
            </div>
            <div className="calendar_boxR">
              <MaxStreak />
              <CurStreak />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Calendar;
