import React, { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import Banner from "../components/Banner";
import SideBar from "../components/SideBar";
import "../css/WriteForm.css";
import axios from "axios";

const BoardWriteCom = () => { 
  //BoardListCom-> BoardWriteCom을 거쳐 전달됨
  const navigate = useNavigate();
  const location = useLocation();
  const thisGroupId = location.state;
  
  const [postTitle, setPostTitle] = useState(""); //제목 입력값
  const [postContent, setPostContent] = useState("");//내용 입력값

  const [showProgress, setShowProgress] = useState(false); // 진척도 표시 상태
  const [selectedProgress, setSelectedProgress] = useState(null); //진척도 둘 중 선택
  
  // "Post" 버튼 클릭 (게시글 작성 후 이동)
  const handleAddPost = async () =>{
    if(!selectedProgress || selectedProgress == null){
      try {
        const response = await axios.post(
          `http://localhost:8080/planbee/groups/${thisGroupId}/boards`,
            {
              "postTitle": postTitle,
             "postContent": postContent
           }
          ,{
            withCredentials: true,
          }
        )
        console.log("일반 글 등록한 후의 응답", response.data)
        //다 되면 글 하나 보는 장면으로 넘어가야함
        const redirectUrl = response.data.redirectUrl;
        
          navigate(`/boardOne/${response.data.postId}`,{
            state: {
              thisGroupId: thisGroupId,
              thisPostId: {id: response.data.postId}
            },
            replace:true //히스토리 스택에 남기지 않아 글쓰기 이후 뒤로가기 해도 글쓰기로 안돌아옴옴
          })
        
      } catch (error) {
        console.log("글쓰기 실패",error)
      }
    } else { //progress 체크박스가 활성화된 경우
      switch(selectedProgress){
        case "daily":
          try {
            const response = await axios.post(
              `http://localhost:8080/planbee/groups/${thisGroupId}/boards/daily`,
              {},
              {
                withCredentials:true,
              }
            )
            console.log("오늘의 진척도 입력 성공", response.data)
            navigate(`/boardOne/${response.data.postId}`,{
              state: {
                thisGroupId: thisGroupId,
                thisPostId: {id: response.data.postId}
              },
              replace:true //히스토리 스택에 남기지 않아 글쓰기 이후 뒤로가기 해도 글쓰기로 안돌아옴옴
            })
          } catch (error) {
            console.log("오늘의 진척도 입력 실패",error)
          }
          break;
        case "weekly":
          try {
            const response = await axios.post(
              `http://localhost:8080/planbee/groups/${thisGroupId}/boards/weekly`,
              {},
              {
                withCredentials:true,
              }
            )
            console.log("이번주 진척도 입력 성공", response.data)
            navigate(`/boardOne/${response.data.postId}`,{
              state: {
                thisGroupId: thisGroupId,
                thisPostId: {id: response.data.postId}
              },
              replace:true //히스토리 스택에 남기지 않아 글쓰기 이후 뒤로가기 해도 글쓰기로 안돌아옴옴
            })
          } catch (error) {
            console.log("이번주 진척도 입력실패", error)
          }
          break;  
      }
    }
    
  } 
  const handleMonthlyWinner = async () => { //월간 등수 자동 글쓰기기
    try {
      const response = await axios.post(
        `http://localhost:8080/planbee/groups/auto-post/${thisGroupId}`,
        {
          withCredentials:true,
        }
      )
      console.log("월별 1등 글쓰기 성공", response.data)
    } catch (error) {
      console.log("자동글쓰기 실패", error)
    }
  }

  
  // "진척도 가져오기" 버튼 클릭 → 제목/내용 & 버튼 사라지고 진척도 표시
  const handleShowProgress = () => {
    setShowProgress(true);
  }

  // "닫기" 버튼 클릭 → 진척도 사라지고 제목/내용 & 버튼 다시 표시
  const handleHideProgress = () => {
    setShowProgress(false)
  }

  const handleDailyChange = (e) => {
    setSelectedProgress(e.target.checked? "daily": null);
  }
  const handleWeeklyChange = (e) => {
    setSelectedProgress(e.target.checked? "weekly": null);
  }






  return (
    <div className="main_container">
      <Banner />
      <div className="sidebar_and_content">
        <SideBar />
        <div className="main_content">
          <div className="white_box_writeform">
            <h2>글쓰기</h2>

            {/* 제목 & 내용 입력란 (진척도를 가져오면 숨김) */}
            {!showProgress && (
              <>
                <div className="input-group-writeform">
                  <label>제목: </label>
                  <input
                    type="text"
                    className="title-input-writeform"
                    value={postTitle}
                    onChange={(e) => {setPostTitle(e.target.value)}}
                  />
                </div>

                <div className="input-group-writeform">
                  <label>내용: </label>
                  <textarea
                    rows={5}
                    className="content-input-writeform"
                    value={postContent}
                    onChange={(e) => {setPostContent(e.target.value)}}
                  />
                </div>

              <div className="post_writeform">
                {/* 진척도 가져오기 버튼 (진척도를 가져오기 전만 표시) */}
                <button onClick={()=>handleShowProgress()} className="progress-button-writeform">
                  진척도 가져오기
                </button>
                <button onClick={()=>handleAddPost()} className="progress-button-writeform">
                  post
                </button>
              </div>
              </>
            )}

            {/* 진척도 가져오기를 누르면 아래의 진척도 표시됨 */}
            {/* 진척도 가져오기를 누르면 아래의 진척도 표시됨 */}
{showProgress && (
  <>
    <p className="progress_share_info">
      *진척도 공유시 아래의 사진과 같이 공유됩니다.*
    </p>

        {/* ✅ 🔹 가로 정렬을 위한 부모 컨테이너 추가 */}
        <div className="progress_container">
          {/* 오늘의 진척도 */}
          <div className="progress_box_writeform" style={{position : "relative"}}>
          <input type="checkbox" className="progress_checkbox"
          checked={selectedProgress === "daily"}
          onChange={handleDailyChange}
          disabled={selectedProgress === "weekly"}   />
            <h2>하늘다람쥐님의 오늘 진척도</h2>
            <h3>2025-03-18</h3>
            <p>오늘의 진척도</p>
            <p>8개 / 10개</p>
            <div className="progress_bar_outer_writeform">
              <div className="progress_bar_inner_writeform" style={{ width: "80%" }}>
                80%
              </div>
            </div>
          </div>

          {/* 주간 진척도 */}
          <div className="weekly_progress_box_writeform" style={{position : "relative"}}>
            <input type="checkbox" className="progress_checkbox"
            checked={selectedProgress === "weekly"}
            onChange={handleWeeklyChange}
            // 오늘의 진척도가 선택되어 있다면 주간 진척도 체크박스는 disabled 처리
            disabled={selectedProgress === "daily"} />
            <h2>하늘다람쥐님의 주간 진척도</h2>
 
            <h3>03-11 ~ 03-17</h3>
            <div className="weekly_progress_list">
              <div className="weekly_progress_item">
                <span>03-11</span>
                <div className="progress_bar_outer">
                  <div className="progress_bar_inner red" style={{ width: "20%" }}>20%</div>
                </div>
              </div>
              <div className="weekly_progress_item">
                <span>03-12</span>
                <div className="progress_bar_outer">
                  <div className="progress_bar_inner yellow" style={{ width: "45%" }}>45%</div>
                </div>
              </div>
              <div className="weekly_progress_item">
                <span>03-13</span>
                <div className="progress_bar_outer">
                  <div className="progress_bar_inner yellow" style={{ width: "60%" }}>60%</div>
                </div>
              </div>
              <div className="weekly_progress_item">
                <span>03-14</span>
                <div className="progress_bar_outer">
                  <div className="progress_bar_inner green" style={{ width: "75%" }}>75%</div>
                </div>
              </div>
              <div className="weekly_progress_item">
                <span>03-15</span>
                <div className="progress_bar_outer">
                  <div className="progress_bar_inner red" style={{ width: "30%" }}>30%</div>
                </div>
              </div>
              <div className="weekly_progress_item">
                <span>03-16</span>
                <div className="progress_bar_outer">
                  <div className="progress_bar_inner green" style={{ width: "90%" }}>90%</div>
                </div>
              </div>
              <div className="weekly_progress_item">
                <span>03-17</span>
                <div className="progress_bar_outer">
                  <div className="progress_bar_inner yellow" style={{ width: "50%" }}>50%</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* 닫기 + Post 버튼 */}
        <div className="button_container_writeform">
          <button onClick={handleHideProgress} className="close_button_writeform">
            닫기
          </button>
          <button onClick={()=>handleAddPost()} className="post_button_writeform">
            Post
          </button>
        </div>
      </>
    )}

          </div>
        </div>
      </div>
    </div>
  );
};

export default BoardWriteCom;
