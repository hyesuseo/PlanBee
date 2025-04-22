import React, { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import "../css/BoardList.css";

const BoardListCom = ({ Info: infoFromProps }) => {
  const location = useLocation();
  const infoFromState = location.state;
  const [Info, setInfo] = useState(infoFromProps || infoFromState || {});

  const [requestUrl, setRequestUrl] = useState(""); // 요청 URL
  const [thisGroupId, setThisGroupId] = useState(""); // 그룹 ID
  const [thisGroupName, setThisGroupName] = useState(""); // 그룹 이름
  const [thisGroupCount, SetThisGroupCount] = useState(""); // 그룹 인원수
  const [board, setBoard] = useState(null); // 게시글 목록
  const [sort, setSort] = useState("최신순");
  const [searchType, setSearchType] = useState("제목");
  const [searchTerm, setSearchTerm] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const [isSearchOpen, setIsSearchOpen] = useState(false);

  const navigate = useNavigate();

  // ✅ Info → groupId, redirectUrl 추출
  useEffect(() => {
    if (infoFromProps) {
      setInfo(infoFromProps);
    } else if (infoFromState) {
      setInfo(infoFromState);
    }
  }, [infoFromProps, infoFromState]);

  useEffect(() => {
    if (Info.groupId && Info.redirectUrl) {
      setThisGroupId(Info.groupId);
      setRequestUrl(Info.redirectUrl);
    }
  }, [Info]);

  useEffect(() => {
    if (thisGroupId && requestUrl) {
      fetchBoardList();
    }
  }, [thisGroupId, requestUrl]);

  const fetchBoardList = async () => {
    try {
      const response = await axios.get(
        `https://wherethereis.site/${requestUrl}`,
        { withCredentials: true }
      );
      if (response.data && response.data.posts) {
        setBoard(response.data.posts);
        setThisGroupName(response.data.groupName);
        SetThisGroupCount(response.data.groupMemberCount);
      }
    } catch (error) {
      console.log("게시판 글가져오기 실패", error);
    }
  };

  const fetchLatestBoard = async () => {
    try {
      const response = await axios.get(
        `https://wherethereis.site/planbee/groups/${thisGroupId}/boards?sort=newest`,
        { withCredentials: true }
      );
      setBoard(response.data.posts);
    } catch (error) {
      console.log("최신순 정렬 실패", error);
    }
  };

  const fetchOldestBoard = async () => {
    try {
      const response = await axios.get(
        `https://wherethereis.site/planbee/groups/${thisGroupId}/boards?sort=oldest`,
        { withCredentials: true }
      );
      setBoard(response.data.posts);
    } catch (error) {
      console.log("오래된 순 정렬 실패", error);
    }
  };

  const fetchMaxHitBoard = async () => {
    try {
      const response = await axios.get(
        `https://wherethereis.site/planbee/groups/${thisGroupId}/boards?sort=hit`,
        { withCredentials: true }
      );
      setBoard(response.data.posts);
    } catch (error) {
      console.log("조회수 많은 순 정렬 실패", error);
    }
  };

  const fetchSearchedBoard = async () => {
    try {
      const response = await axios.get(
        `https://wherethereis.site/planbee/groups/${thisGroupId}/boards?searchType=${searchType}&query=${searchTerm}`,
        { withCredentials: true }
      );
      setBoard(response.data.posts);
    } catch (error) {
      console.log("검색 실패", error);
    }
  };

  const handleSearchOptionClick = (option) => {
    setSort(option);
    if (option === "최신순") fetchLatestBoard();
    else if (option === "오래된 순") fetchOldestBoard();
    else if (option === "조회수 많은 순") fetchMaxHitBoard();
    setIsSearchOpen(false);
  };

  const handleOptionClick = (option) => {
    setSearchType(option);
  };

  const exitGroup = async () => {
    try {
      await axios.post(
        `https://wherethereis.site/planbee/groups/${thisGroupId}/leave`,
        {},
        { withCredentials: true }
      );
      alert(`${thisGroupName} 그룹에서 탈퇴하였습니다`);
      navigate("/social");
    } catch (error) {
      console.log("그룹탈퇴 실패", error);
    }
  };

  const writePost = () => {
    navigate("/boardWrite", { state: thisGroupId });
  };

  return (
    <div className="white_box">
      <div className="group_top_bar">
        <h2 className="group_name">{thisGroupName}</h2>
        <div className="group_top_right">
          <span className="group_member_count">
            현재 인원 : {thisGroupCount}
          </span>
          <button className="leave_icon" onClick={exitGroup}>
            탈퇴하기
          </button>
          <div className="search_dropdown" onClick={() => setIsSearchOpen(!isSearchOpen)}>
            {sort}
            {isSearchOpen && (
              <div className="search_dropdown_menu">
                <div onClick={() => handleSearchOptionClick("최신순")}>최신순</div>
                <div onClick={() => handleSearchOptionClick("오래된 순")}>오래된 순</div>
                <div onClick={() => handleSearchOptionClick("조회수 많은 순")}>조회수 많은 순</div>
              </div>
            )}
          </div>
          <div className="group_sort">
            <div className="sort_button" onClick={() => setIsOpen(!isOpen)}>
              {searchType}
              {isOpen && (
                <div className="sort_dropdown visible">
                  <div onClick={() => handleOptionClick("제목")}>제목</div>
                  <div onClick={() => handleOptionClick("내용")}>내용</div>
                </div>
              )}
            </div>
            <div className="group_search">
              <input
                type="text"
                className="search_input"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              <div className="search_icon" onClick={fetchSearchedBoard}>
                <img src="../img/search_icon.png" alt="search icon" />
              </div>
            </div>
          </div>
        </div>
      </div>
      <hr className="group_black_line" />
      <div className="post_list">
        {board?.length === 0 ? (
          <div style={{ textAlign: "center", padding: "50px", marginTop: "20px" }}>
            게시판에 글이 없습니다.
          </div>
        ) : (
          board.map((item) => (
            <div key={item.postId} className="post_item">
              <Link to={`/boardOne/${item.postId}`} state={{ thisGroupId }}>
                <div className="post_text">{item.postTitle}</div>
              </Link>
              <div className="post_meta">
                <span className="post_author">{item.userId}</span>
                <span className="post_date">{item.postDate}</span>
                <span className="post_views">조회수 {item.postHit}</span>
              </div>
            </div>
          ))
        )}
      </div>
      <div className="write_icon" onClick={writePost}>
        <div>+</div>
      </div>
    </div>
  );
};

export default BoardListCom;
