import React, { useEffect, useState } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import axios from "axios";
import "../css/CalCom.css";
import { getFormattedTodayYYMM } from "./DateUtils";
import PopUp from "./PopUp";
import CalendarMemoForm from "./CalendarMemoForm";

const CalCom = () => {
  const [calData, setCalData] = useState({});
  const [selectedDateKey, setSelectedDateKey] = useState(null);
  const [isPopUpOpen, setIsPopUpOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);

  const today = new Date();
  const currentYear = today.getFullYear();
  const currentMonth = today.getMonth(); // 0(Jan) ~ 11(Dec)

  const getMemo = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/planbee/calendar/memo/${getFormattedTodayYYMM()}`,
        { withCredentials: true }
      );
      console.log("캘린더 데이터", response.data);
      setCalData(response.data);
    } catch (error) {
      console.error("캘린더 메모 fetch 에러", error);
    }
  };

  useEffect(() => {
    getMemo();
  }, []);

  const handleDateClick = (info) => {
    const clickedDate = info.date;
    const dateKey = clickedDate.getDate().toString().padStart(2, "0");
    setSelectedDateKey(dateKey);
    setIsEditMode(calData[dateKey] ? true : false); // 해당 날짜에 메모가 있으면 수정 모드, 아니면 추가 모드
    setIsPopUpOpen(true);
  };

  const handleMemoUpdate = (newMemo) => {
    setCalData((prev) => ({
      ...prev,
      [selectedDateKey]: newMemo,
    }));
  };
  return (
    <>
      <div className="calendar_frame">
        <FullCalendar
          plugins={[dayGridPlugin, interactionPlugin]}
          initialView="dayGridMonth"
          height="800px"
          contentHeight="100%"
          validRange={{
            start: new Date(currentYear, currentMonth, 1), // 이번 달 1일부터
            end: new Date(currentYear, currentMonth + 1, 1), // 다음 달 1일 전까지 (즉, 이번 달의 마지막 날까지)
          }}
          dateClick={handleDateClick}
          dayCellContent={({ date }) => {
            // 날짜를 "01", "02", ... 형식으로 변환
            const dateKey = date.getDate().toString().padStart(2, "0"); // "01", "02" 형태
            const task = calData[dateKey]; // 해당 날짜에 해당하는 데이터
            const progress = task?.tdProgress || 0; // progress 값 가져오기

            let bgColor = "transparent"; // 기본값 (0.0 또는 값 없음)
            if (progress > 0.8) {
              bgColor = "rgb(94, 223, 94)";
            } else if (progress > 0.4) {
              bgColor = "rgb(245, 245, 92)";
            } else if (progress > 0.0) {
              bgColor = "rgb(236, 122, 122)";
            }

            return (
              <div className="custom_cell">
                <div className="date" style={{ backgroundColor: bgColor }}>
                  {date.getDate()}
                </div>
                <div className="cal_memo">
                  {task?.calDetail1 && (
                    <div className="memo_item">{task.calDetail1}</div>
                  )}
                  {task?.calDetail2 && (
                    <div className="memo_item">{task.calDetail2}</div>
                  )}
                  {task?.calDetail3 && (
                    <div className="memo_item">{task.calDetail3}</div>
                  )}
                </div>
              </div>
            );
          }}
        />
      </div>
      <PopUp
        isOpen={isPopUpOpen}
        onClose={() => setIsPopUpOpen(false)}
        style={{ width: "500px", height: "400px" }}
      >
        {selectedDateKey && (
          <CalendarMemoForm
            dateKey={selectedDateKey}
            memoData={calData[selectedDateKey]}
            onClose={() => setIsPopUpOpen(false)}
            onUpdate={handleMemoUpdate}
            getMemo={getMemo}
            isEditMode={isEditMode} // 수정 모드 여부 전달
          />
        )}
      </PopUp>
    </>
  );
};

export default CalCom;
