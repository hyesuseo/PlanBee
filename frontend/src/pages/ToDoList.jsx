import React, { useEffect, useState } from "react";
import Sidebar from "../components/SideBar";
import Banner from "../components/Banner";
import TodayCom from "../components/TodayCom";
import TomorrowCom from "../components/TomorrowCom";
import { getFormattedTodayYYMMDD } from "../components/DateUtils";
import "../css/Main.css";
import "../css/ToDoList.css";
import axios from "axios";

const ToDoList = () => {
  const [progress, setProgress] = useState(null);

  useEffect(() => {
    const fetchPercent = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/planbee/todolist/progress/${getFormattedTodayYYMMDD()}`,
          {
            withCredentials: true,
          }
        );
        setProgress(response.data);
        console.log("진척도", response.data);
      } catch (error) {
        console.error("진척도 관련 에러 발생", error);
      }
    };
    fetchPercent();
  }, []);

  // progress 값을 기준으로 height와 background-color를 동적으로 설정하는 함수
  const getBarStyle = () => {
    if (progress === null) return { height: "0%", backgroundColor: "#ff3b3b" }; // progress가 null일 때는 스타일 적용하지 않음

    const progressPercentage = progress * 100; // progress를 백분율로 변환

    // height는 progress에 맞춰 비례적으로 설정
    const barHeight = `${progressPercentage}%`;

    // background-color를 progress 값에 따라 동적으로 설정
    let barColor = "#f4cc3a"; // 기본값: 노란색
    if (progressPercentage <= 30) {
      barColor = "#ff3b3b"; // 0% - 30%는 빨간색
    } else if (progressPercentage > 30 && progressPercentage <= 70) {
      barColor = "#ffdb3a"; // 30% - 70%는 노란색
    } else {
      barColor = "#4caf50"; // 70% 이상은 녹색
    }

    console.log("적용될 색상:", barColor); // 여기에서 출력되는 색상이 정상적인지 확인

    return {
      height: barHeight,
      backgroundColor: barColor,
    };
  };

  return (
    <div className="main_container">
      <Banner />
      <div className="sidebar_and_content">
        <Sidebar />
        <div className="main_content">
          <div className="todolist_container">
            <TodayCom />
            <div className="todo_progress">
              <div className="todo_percent">
                {progress !== null
                  ? `${Math.round(progress * 100)}%`
                  : "로딩 중..."}
              </div>
              <div className="todo_bar">
                <div className="todo_bar_ex" style={getBarStyle()}></div>
              </div>
            </div>
            <TomorrowCom />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ToDoList;
