import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import Banner from './Banner';
import SideBar from './SideBar';
import { option } from 'framer-motion/client';
import '../css/BoardList.css';
import Board from '../pages/Board';

const BoardListCom = ({ Info: infoFromProps }) => {
  // props로 전달된 Info가 있으면 사용하고, 없으면 location.state 사용
  const location = useLocation();
  const infoFromState = location.state;
  const [Info, setInfo] = useState(infoFromProps || infoFromState || {});

  useEffect(() => {
    if (infoFromProps) {
      setInfo(infoFromProps);
    } else if (infoFromState) {
      setInfo(infoFromState);
    }
  }, [infoFromProps, infoFromState]);

  const navigate = useNavigate();
  const [requestUrl, setRequestUrl] = useState(Info.redirectUrl); // 리다이렉트되는 URL 저장
  const [thisGroupId, setThisGroupId] = useState(Info.groupId); // 그룹 아이디 저장
  const [thisGroupName, setThisGroupName] = useState(''); // 그룹 이름 저장
  const [thisGroupCount, SetThisGroupCount] = useState(''); // 그룹 인원수 저장
  const [board, setBoard] = useState(null); // 게시글 목록 저장
  const [currentTeam, SetCurrentTeam] = useState("joinedGroup");

  // 게시글 수정을 위한 변수 선언
  const [isEditing, setIsEditing] = useState(false);
  const [postTitle, setPostTitle] = useState('');
  const [postContent, setPostContent] = useState('');

  // 검색 관련 변수: 검색 기준은 "제목" 또는 "내용"
  const [searchType, setSearchType] = useState("제목");
  const [searchTerm, setSearchTerm] = useState("");

  // 정렬 관련 변수: "최신순", "오래된 순", "조회수 많은 순"
  const [sort, setSort] = useState("최신순");

  // 드롭다운 관련 상태
  const [isOpen, setIsOpen] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);

  const fetchBoardList = async () => { // 게시판 글 가져오는 기능
    try {
      const response = await axios.get(`http://localhost:8080/${requestUrl}`, {
        withCredentials: true,
      });
      console.log({ thisGroupId }, "의 게시글목록: ", response.data);
      if (response.data && response.data.posts) {
        setBoard(response.data.posts);
        setThisGroupName(response.data.groupName);
        SetThisGroupCount(response.data.groupMemberCount);
        console.log("이 그룹의 정보들", thisGroupName, "인원수:", thisGroupCount);
      }
    } catch (error) {
      console.log("게시판 글가져오기 실패", error);
    }
  };

  useEffect(() => {
    fetchBoardList();
  }, []);

  const handleToggle = () => setIsOpen(!isOpen); // 드롭다운 열기/닫기

  // 검색 기준 변경 → 이제 검색 기준(searchType)은 "제목" 또는 "내용"
  const handleOptionClick = (option) => {
    setSearchType(option);
    console.log("검색 기준:", option);
  };

  const handleSearchToggle = () => {
    setIsSearchOpen(!isSearchOpen);
  };

  // 정렬 옵션 변경 → 정렬 기준(sort)은 "최신순", "오래된 순", "조회수 많은 순"
  const handleSearchOptionClick = (option) => {
    setSort(option);
    if (option === "최신순") {
      fetchLatestBoard();
    } else if (option === "오래된 순") {
      fetchOldestBoard();
    } else if (option === "조회수 많은 순") {
      fetchMaxHitBoard();
    }
    console.log(`${option} 정렬 선택됨`);
    setIsOpen(false);
  };

  const handleSearchChange = (e) => setSearchTerm(e.target.value);
  const handleSearch = () => console.log(`검색어: ${searchTerm}`);

  const exitGroup = async () => { // 그룹 탈퇴하는 코드
    try {
      const response = await axios.post(
        `http://localhost:8080/planbee/groups/${thisGroupId}/leave`,
        {},
        {
          withCredentials: true,
        }
      );
      SetCurrentTeam("");
      alert(thisGroupName + " 그룹에서 탈퇴하였습니다");
      navigate("/social");
    } catch (error) {
      console.log("그룹탈퇴 실패", error);
    }
  };

  const fetchLatestBoard = async () => { // 최신순 정렬
    try {
      const response = await axios.get(
        `http://localhost:8080/planbee/groups/${thisGroupId}/boards?sort=newest`,
        { withCredentials: true }
      );
      console.log("최신순 정렬 결과", response.data);
      setBoard(response.data.posts);
    } catch (error) {
      console.log("최신순 정렬 실패", error);
    }
  };

  const fetchOldestBoard = async () => { // 오래된 순 정렬
    try {
      const response = await axios.get(
        `http://localhost:8080/planbee/groups/${thisGroupId}/boards?sort=oldest`,
        { withCredentials: true }
      );
      console.log("오래된 순 정렬 결과:", response.data);
      setBoard(response.data.posts);
    } catch (error) {
      console.log("오래된 순 정렬 실패", error);
    }
  };

  const fetchMaxHitBoard = async () => { // 조회수 많은 순 정렬
    try {
      const response = await axios.get(
        `http://localhost:8080/planbee/groups/${thisGroupId}/boards?sort=hit`,
        { withCredentials: true }
      );
      console.log("조회수 많은 순 정렬 결과:", response.data);
      setBoard(response.data.posts);
    } catch (error) {
      console.log("조회수 많은 순 정렬 실패", error);
    }
  };

  const fetchSearchedBoard = async () => { // 검색어로 검색하는 코드 (검색 기준: searchType, 검색어: searchTerm)
    try {
      const response = await axios.get(
        `http://localhost:8080/planbee/groups/${thisGroupId}/boards?searchType=${searchType}&query=${searchTerm}`,
        { withCredentials: true }
      );
      console.log("검색어:", searchTerm, "검색 기준:", searchType);
      console.log("검색 결과:", response.data.posts);
      setBoard(response.data.posts);
    } catch (error) {
      console.log("검색 실패", error);
    }
  };

  const writePost = async () => {
    navigate("/boardWrite", {state: thisGroupId});
  };

  return (
    <>
      {/* 게시판 영역: white_box */}
      <div className="white_box">
        {/* 그룹 상단 바 */}
        <div className="group_top_bar">
          <h2 className="group_name">
            {thisGroupName ? thisGroupName : ""}
          </h2>
          <div className="group_top_right">
            <span className="group_member_count">
              현재 인원 : {thisGroupCount ? thisGroupCount : "0"}
            </span>
            <div className="group_drop">
              <button className="leave_icon" onClick={() => exitGroup()}>
                탈퇴하기
              </button>
            </div>
            <div className="search_dropdown" onClick={handleSearchToggle}>
              {sort}
              {isSearchOpen && (
                <div className="search_dropdown_menu">
                  <div className="dropdown_option" onClick={() => handleSearchOptionClick("최신순")}>
                    최신순
                  </div>
                  <div className="dropdown_option" onClick={() => handleSearchOptionClick("오래된 순")}>
                    오래된 순
                  </div>
                  <div className="dropdown_option" onClick={() => handleSearchOptionClick("조회수 많은 순")}>
                    조회수 많은 순
                  </div>
                </div>
              )}
            </div>
            <div className="group_sort">
              <div className="sort_button" onClick={handleToggle}>
                {searchType}
                {isOpen && (
                  <div className="sort_dropdown visible">
                    <div className="dropdown_option" onClick={() => handleOptionClick("제목")}>
                      제목
                    </div>
                    <div className="dropdown_option" onClick={() => handleOptionClick("내용")}>
                      내용
                    </div>
                  </div>
                )}
              </div>
              {/* 검색창 */}
              <div className="group_search">
                <input
                  type="text"
                  className="search_input"
                  value={searchTerm}
                  onChange={handleSearchChange}
                />
                <div className="search_icon" onClick={() => fetchSearchedBoard()}>
                  <img src="../img/search_icon.png" alt="search icon" />
                </div>
              </div>
            </div>
          </div>
        </div>
        <hr className="group_black_line" />
        <div class="post_list">
        {/* 게시글 목록 */}
        {board ?.length === 0 ? (
          <div style={{ textAlign: "center", padding: "50px", marginTop: "20px" }}>
            게시판에 글이 없습니다.
          </div>
        ) : (
          <div className="post_list">
            {board && board.map((item)=> (
            <div key={item.postId} className="post_item">
            <Link to={`/boardOne/${item.postId}`} state={{thisGroupId: thisGroupId}}>
            <div className="post_text">{item.postTitle}</div></Link>
            <div className="post_meta">
            <span className="post_author">{item.userId}</span>
            <span className="post_date">{item.postDate}</span>
            <span className="post_views">조회수 {item.postHit}</span>
            </div>
          </div>
          ))}
          </div>
        )}

        </div>
        {/* 글쓰기 아이콘 */}
        <div className="write_icon" onClick={() => writePost()}>
          <div>+</div>
        </div>
      </div>
    </>
  );
};

export default BoardListCom;
