import React, { useEffect, useState } from "react";
import axios from "axios";
import "../css/Streak.css";

const CurStreak = () => {
  const [curStreak, setCurStreak] = useState(0);
  const [maxStreak, setMaxStreak] = useState(1);
  const [message, setMessage] = useState("");
  useEffect(() => {
    const fetchCurStreak = async () => {
      try {
        const response = await axios.get(
          `https://wherethereis.site/planbee/calendar/curStreak`,
          {
            withCredentials: true,
          }
        );
        const data = response.data;
        console.log(data);
        setCurStreak(data.curStreak);
        console.log("progressColor:", progressColor);
        console.log("progressHeight:", progressHeight);
        setMaxStreak(data.maxStreak);
        setMessage(data.message);
      } catch (error) {
        console.error("현재 진척도 fetch 에러", error);
      }
    };

    fetchCurStreak();
  }, []);

  const progressRatio = Math.min(curStreak / maxStreak, 1);
  const progressHeight = `${(progressRatio * 100).toFixed(0)}%`;

  let progressColor = "red";
  if (progressRatio > 0.7) {
    progressColor = "#4caf50";
  } else if (progressRatio > 0.3) {
    progressColor = "#ffdb3a";
  }

  return (
    <div className="current_streak">
      <h3>현재 연속 달성일</h3>
      <div className="current_streak_days">
        <div
          className="progress_bar"
          style={{
            height: progressHeight,
            backgroundColor: progressColor,
          }}
        ></div>
        <div className="notion">
          <span className="curStreak_data">
            {curStreak !== null ? curStreak : "로딩 중..."}
          </span>
          <span className="curStreak_days">days</span>
        </div>
      </div>
      <div
        className="current_streak_comment"
        style={{
          backgroundColor: progressColor,
        }}
      >
        {message}
      </div>
    </div>
  );
};

export default CurStreak;
