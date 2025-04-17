import React, { useEffect, useState } from "react";
import {
  getFormattedTomorrowYYYYMMDD,
  getFormattedTomorrowYYMMDD,
} from "./DateUtils";
import axios from "axios";
import "../css/TodayCom.css";

const TomorrowCom = () => {
  const [todoDetailsTomorrow, setTodoDetailsTomorrow] = useState([]); //내일 todolist 목록 불러오기기
  const [memo, setMemo] = useState(""); //메모 fetch
  const [isEditingMemo, setIsEditingMemo] = useState(false);
  const [newMemo, setNewMemo] = useState("");
  const [isAdding, setIsAdding] = useState(false); //todolist 추가
  const [newTask, setNewTask] = useState({ tdDetail: "", tdDetailTime: "" }); //todolist 추가가
  const [dropdownOpen, setDropdownOpen] = useState(null);
  const [tomorrowTdId, setTomorrowTdId] = useState(null);
  const [editItem, setEditItem] = useState(null);
  const formatTime = (time) => {
    if (typeof time === "number") {
      time = time.toString().padStart(4, "0"); // 900 → "0900"
    } else if (typeof time === "string") {
      time = time.padStart(4, "0"); // "900" → "0900"
    } else {
      return ""; // 예외 처리 (undefined 등)
    }
  
    const hour = time.slice(0, 2);
    const minute = time.slice(2);
  
    return `${hour}:${minute}`;
  };
  

  useEffect(() => {
    //checklist 불러오는 함수 -> 세션연결 성공, 테스트완료
    const fetchDashBoard = async () => {
      try {
        const response = await axios.get(
          `https://wherethereis.site/planbee/todolist/dashBoard/${getFormattedTomorrowYYMMDD()}`,
          {
            withCredentials: true,
          }
        );
        console.log("내일Dashboard",response.data);
        if (Array.isArray(response.data.todoList)) {
          setTodoDetailsTomorrow(response.data.todoList);
          setTomorrowTdId(response.data.todoId);
          setMemo(response.data.memo);
        } else {
          console.error("오늘의 데이터 에러", response.data);
        }
      } catch (error) {
        console.error("오늘의 데이터 fetch 에러", error);
      }
    };

    //memo 불러오는 함수 -> 세션연결 성공, 테스트완료
    fetchDashBoard();
  }, []);

  //todolist 체크박스 상태 변경 함수
  const handleCheckboxChange = async (id) => {
    const updatedTodoDetails = todoDetailsTomorrow.map((item) =>
      item.tdDetailId === id
        ? { ...item, tdDetailState: !item.tdDetailState } //false인 경우 true로 바꿈
        : item
    );

    setTodoDetailsTomorrow(updatedTodoDetails);

    //변경된 상태를 저장한 후 api 요청 보내기
    const changedItem = updatedTodoDetails.find(
      (item) => item.tdDetailId === id
    );

    try {
      await axios.put("https://wherethereis.site/planbee/todolist/state", {
        tdDetailId: changedItem.tdDetailId,
        tdId: changedItem.tdId,
        tdDetail: changedItem.tdDetail,
        tdDetailTime: changedItem.tdDetailTime,
        tdDetailState: changedItem.tdDetailState, // 반전된 상태값을 저장시켜서 전송송
      });
    } catch (error) {
      console.error("체크박스 처리 오류:", error);
    }
  };

  const handleEditClick = (item) => {
    setEditItem(item);
  };

  // 수정된 내용 서버에 저장
  const handleSaveEdit = async (item) => {
    const requestData = {
      tdDetailId: editItem.tdDetailId,
      tdDetail: editItem.tdDetail,
      tdDetailState: editItem.tdDetailState,
      tdDetailTime: editItem.tdDetailTime,
      tdId: editItem.tdId,
    };
    try {
      const response = await axios.put(
        `https://wherethereis.site/planbee/todolist/modify`,
        requestData,
        { withCredentials: true }
      );
      console.log("수정 요청 데이터: ", requestData);
      setTodoDetailsTomorrow((prev) =>
        prev.map((todo) =>
          todo.tdDetailId === response.data.tdDetailId
            ? { ...todo, ...response.data }
            : todo
        )
      );
      setEditItem(null);
    } catch (error) {
      console.error("TD 수정 실패", error);
    }
  };

  // 수정 취소
  const handleCancelEdit = () => {
    setEditItem(null); // 수정 모드 종료
  };

  //todolist 삭제 함수 -> 세션연결 성공, 테스트 완료
  const handleDeleteClick = (id) => {
    axios
      .delete(`https://wherethereis.site/planbee/todolist/del`, {
        data: { tdDetailId: id },
        withCredentials: true,
      })
      .then(() => {
        setTodoDetailsTomorrow((prev) =>
          prev.filter((item) => item.tdDetailId !== id)
        );
      })
      .catch((error) => {
        console.error("삭제 실패:", error);
      });
  };

  const handleAddTask = async () => {
    if (!newTask.tdDetail.trim() || !newTask.tdDetailTime.trim()) {
      console.error("할 일과 목표 시간을 입력해야 합니다.");
      return;
    }

    const newTaskData = {
      tdDetail: newTask.tdDetail,
      tdDetailTime: newTask.tdDetailTime,
    };

    try {
      const response = await axios.post(
        `https://wherethereis.site/planbee/todolist/write/${getFormattedTomorrowYYMMDD()}`,
        newTaskData,
        { withCredentials: true }
      );

      console.log("서버에서 받은 응답:", response.data);
      if (response.data && response.data.tdDetailId) {
        // 서버 응답을 기반으로 상태 업데이트
        setTodoDetailsTomorrow((prev) => [...prev, 
          {...response.data,
            tdDetail: newTask.tdDetail,
            tdDetailTime: newTask.tdDetailTime,
      },
    ]);
      } else {
        console.error("서버 응답에 tdDetailId가 없습니다:", response.data);
      }

      setNewTask({ tdDetail: "", tdDetailTime: "" });
      setIsAdding(false);
    } catch (error) {
      console.error("추가 실패:", error);
    }
  };

  const toggleDropdown = (id) => {
    setDropdownOpen(dropdownOpen === id ? null : id);
  };

  //memo 수정 함수
  const handleSaveMemo = async () => {
    if (tomorrowTdId === null) {
      console.error("tdId를 가져올 수 없습니다");
      return;
    }

    const requestData = {
      tdId: tomorrowTdId,
      tdMemo: newMemo,
    };

    console.log("전송하는 데이터:", requestData);

    try {
      await axios.put(
        "https://wherethereis.site/planbee/todolist/memoWrite",
        {
          tdId: tomorrowTdId,
          tdMemo: newMemo,
        }
      );
      setMemo(newMemo);
      setIsEditingMemo(false);
    } catch (error) {
      console.error("메모 수정 실패: ", error);
    }
  };

  return (
    <div className="todolist">
      <div className="todolist_index">Tomorrow</div>
      <div className="todolist_content">
        <h2 className="todolist_date">{getFormattedTomorrowYYYYMMDD()}</h2>
        <table className="todolist_checkbox">
          <tbody>
            {todoDetailsTomorrow.map((item) => (
              <tr key={item.tdDetailId}>
                <td>
                  <div className="custom_checkbox">
                    <input
                      type="checkbox"
                      id={`checkbox-${item.tdDetailId}`}
                      checked={item.tdDetailState}
                      onChange={() => handleCheckboxChange(item.tdDetailId)}
                    />
                    <label htmlFor={`checkbox-${item.tdDetailId}`}>
                      <span className="checkmark"></span>
                    </label>
                  </div>
                </td>
                <td>
                  {editItem && editItem.tdDetailId === item.tdDetailId ? (
                    <input
                      type="text"
                      value={editItem.tdDetail}
                      onChange={(e) =>
                        setEditItem({ ...editItem, tdDetail: e.target.value })
                      }
                    />
                  ) : (
                    item.tdDetail
                  )}
                </td>
                <td>
                  {editItem && editItem.tdDetailId === item.tdDetailId ? (
                    <input
                      type="text"
                      value={editItem.tdDetailTime}
                      onChange={(e) =>
                        setEditItem({
                          ...editItem,
                          tdDetailTime: e.target.value,
                        })
                      }
                    />
                  ) : (
                    formatTime(item.tdDetailTime)
                  )}
                </td>
                <td>
                  <span onClick={() => toggleDropdown(item.tdDetailId)}>🖉</span>
                  {dropdownOpen === item.tdDetailId && (
                    <div className="dropdown-menu">
                      <button onClick={() => handleEditClick(item)}>
                        수정
                      </button>
                      <button
                        onClick={() => handleDeleteClick(item.tdDetailId)}
                      >
                        삭제
                      </button>
                      <button onClick={() => toggleDropdown(null)}>닫기</button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
            <tr>
              <td colSpan="4">
                {isAdding ? (
                  <div>
                    <input
                      type="text"
                      placeholder="할 일 입력"
                      value={newTask.tdDetail}
                      onChange={(e) =>
                        setNewTask({ ...newTask, tdDetail: e.target.value })
                      }
                    />
                    <input
                      type="text"
                      placeholder="목표 시간"
                      value={newTask.tdDetailTime}
                      onChange={(e) =>
                        setNewTask({ ...newTask, tdDetailTime: e.target.value })
                      }
                    />
                    <button onClick={handleAddTask}>완료</button>
                  </div>
                ) : (
                  <button
                    className="add_tdDetail"
                    onClick={() => setIsAdding(true)}
                  >
                    <div className="plus_btn">+</div>
                  </button>
                )}
              </td>
            </tr>
          </tbody>
        </table>

        {editItem && (
          <div>
            <button onClick={handleSaveEdit}>수정 저장</button>
            <button onClick={handleCancelEdit}>취소</button>
          </div>
        )}

        <div className="todolist_memo">
          <h3>Memo</h3>
          {isEditingMemo ? (
            <div>
              <textarea
                value={newMemo}
                onChange={(e) => setNewMemo(e.target.value)}
                className="tdMemo_textarea"
              />
              <div className="tdMemo_btn_wrapper">
                <button onClick={handleSaveMemo}>저장</button>
                <button onClick={() => setIsEditingMemo(false)}>취소</button>
              </div>
            </div>
          ) : (
            <div
              onClick={() => {
                setIsEditingMemo(true);
                setNewMemo(memo);
              }}
              className="memomemo"
            >
              {memo}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default TomorrowCom;
