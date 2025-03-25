import React, { useEffect, useState } from "react";
import Sidebar from "../components/SideBar";
import Banner from "../components/Banner";
import "../css/Main.css";
import "../css/Archive.css";
import { Icon } from "@iconify/react";
import axios from "axios";

const Archive = () => {
  const [archiveData, setArchiveData] = useState(null);
  //페이징을 위한 데이터 캐싱 상태 정의
  const [cachedArchive, setCachedArchive] = useState([]);
  const [currentPageIndex, setCurrentPageIndex] = useState(0);
  const [fetchedPage, setFetchedPage] = useState(0);

  //지나간 데이터가 날라가는 것을 방지하고 저장해두기 위한 변수
  const [usedArchive, setUsedArchive] = useState([]);

  //검색 기능을 위한 달력에서 선택된 날짜 저장 변수
  const [selectedDate, setSelectedDate] = useState("");

  //검색 기능을 위한 입력값 저장 변수
  const [keyword, setKeyword] = useState("");

  //키워드 검색 함수
  const handleKeywordChange = (e) => {
    setKeyword(e.target.value);
  };
  //페이지 상단 날짜를 위한 포멧
  const formatDate = (dateStr) => {
    const year = `20${dateStr.slice(0, 2)}`;
    const month = dateStr.slice(2, 4);
    const day = dateStr.slice(4, 6);
    return `${year}-${month}-${day}`;
  };

  //목표시간 표시를 위한 포맷
  const formatTime = (timeStr) => {
    const padded = timeStr.padStart(4, "0");
    const hour = padded.slice(0, 2);
    const minute = padded.slice(2);
    return `${hour}:${minute}`;
  };

  // 검색을 위한 날짜 변환 포맷
  const formatToYYMMDD = (dateStr) => {
    const date = new Date(dateStr);
    const yy = String(date.getFullYear()).slice(2);
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    return `${yy}${mm}${dd}`;
  };

  const handleDateChange = (e) => {
    setSelectedDate(e.target.value);
  };

  const handleDateSearch = async () => {
    if (!selectedDate) return;
    const yymmdd = formatToYYMMDD(selectedDate);
    try {
      const response = await axios.get(
        `http://localhost:8080/planbee/archive/searchDate/${yymmdd}`,
        { withCredentials: true }
      );
      const fetched = response.data;
      const visible = fetched.slice(0, 2).reverse();
      const cached = fetched.slice(2).reverse();

      setArchiveData(visible);
      setCachedArchive(cached);
      setUsedArchive([]); // 이전 페이지 기록은 초기화
      setFetchedPage(0);
      setCurrentPageIndex(0);
    } catch (error) {
      console.log("날짜 검색 실패!", error);
    }
  };

  const handleSearch = async () => {
    if (!selectedDate && !keyword) return; // 날짜와 키워드가 비어 있으면 검색하지 않음

    try {
      let response;

      if (selectedDate) {
        const yymmdd = formatToYYMMDD(selectedDate);
        response = await axios.get(
          `http://localhost:8080/planbee/archive/searchDate/${yymmdd}`,
          { withCredentials: true }
        );
      } else if (keyword) {
        response = await axios.get(
          `http://localhost:8080/planbee/archive/searchKeyword/${keyword}`,
          { withCredentials: true }
        );
      }

      const fetched = response.data;
      const visible = fetched.slice(0, 2).reverse();
      const cached = fetched.slice(2).reverse();

      setArchiveData(visible);
      setCachedArchive(cached);
      setUsedArchive([]); // 이전 페이지 기록은 초기화
      setFetchedPage(0);
      setCurrentPageIndex(0);
    } catch (error) {
      console.error("검색 실패", error);
    }
  };

  useEffect(() => {
    if (cachedArchive.length > 30) {
      console.log("캐시가 30개를 초과하여 초기화합니다.");
      setCachedArchive([]);
      // 필요 시 페이지 인덱스도 초기화
      // setFetchedPage(0);
      // setCurrentPageIndex(0);
    }
  }, [cachedArchive]);

  //세션 생성 및 검증 함수 + 초기 데이터 fetch
  useEffect(() => {
    const initialize = async () => {
      try {
        // 초기 데이터 6개 요청
        const response = await axios.get(
          `http://localhost:8080/planbee/archive?page=0&limit=6`,
          { withCredentials: true }
        );

        const fetched = response.data;
        const visible = fetched.slice(0, 2).reverse();
        const cached = fetched.slice(2).reverse();

        setArchiveData(visible);
        setCachedArchive(cached);
        setFetchedPage(0);
        setCurrentPageIndex(0);
      } catch (error) {
        console.error("초기화 중 오류 발생", error);
      }
    };

    initialize();
  }, []);

  //페이징 api

  const handlePrev = async () => {
    const newIndex = currentPageIndex + 2;

    if (cachedArchive.length >= 2) {
      const nextVisible = cachedArchive.slice(0, 2).reverse();
      const remainingCache = cachedArchive.slice(2);

      setUsedArchive((prev) => [...prev, ...archiveData]);
      setArchiveData(nextVisible);
      setCachedArchive(remainingCache);
      setCurrentPageIndex(newIndex);
    } else {
      try {
        const nextPage = fetchedPage + 1;
        const response = await axios.get(
          `http://localhost:8080/planbee/archive?page=${nextPage}&limit=6`,
          { withCredentials: true }
        );
        const fetched = response.data;
        if (fetched.length === 0) return;

        const nextVisible = fetched.slice(0, 2).reverse();
        const nextCache = fetched.slice(2);

        setUsedArchive((prev) => [...prev, ...archiveData]);
        setArchiveData(nextVisible);
        setCachedArchive(nextCache);
        setFetchedPage(nextPage);
        setCurrentPageIndex(newIndex);
      } catch (error) {
        console.error("다음페이지 불러오기 실패", error);
      }
    }
  };
  const handleNextPage = () => {
    const newIndex = currentPageIndex - 2;
    if (newIndex < 0 || usedArchive.length < 2) return;

    const lastUsed = usedArchive.slice(-2); // 복원할 데이터
    const remainingUsed = usedArchive.slice(0, -2); // 남은 used

    // 현재 archiveData는 다시 cachedArchive에 저장
    setCachedArchive((prev) => [...archiveData, ...prev]); // 🔥 보존
    setArchiveData(lastUsed);
    setUsedArchive(remainingUsed); // 🔥 복원 후 제거
    setCurrentPageIndex(newIndex);
  };

  return (
    <div className="main_container">
      <Banner />
      <div className="sidebar_and_content">
        <Sidebar />
        <div className="main_content">
          <div className="archive_container">
            <div className="archive_search">
              <div className="archive_search_calendar">
                <input
                  type="date"
                  id="date"
                  name="archiveDate"
                  value={selectedDate}
                  onChange={handleDateChange}
                />
                <input
                  type="text"
                  id="keyword"
                  placeholder="검색어를 입력하세요"
                  value={keyword}
                  onChange={handleKeywordChange}
                />
                <button onClick={handleSearch}>
                  <Icon
                    icon="ic:baseline-search"
                    width="36px"
                    height="36px"
                    style={{ color: "#F4CC3A" }}
                  />
                </button>
              </div>
            </div>
            <div className="archive_content">
              <button className="archive_btn_left" onClick={handlePrev}>
                <Icon
                  icon="material-symbols-light:arrow-back-2-rounded"
                  width="72"
                  height="72"
                  style={{ color: "#cccccc" }}
                />
              </button>
              <div className="archive_page_left">
                {archiveData && archiveData.length > 0 && (
                  <>
                    <div className="archive_left_content">
                      <div className="archive_date">
                        {formatDate(archiveData[0].archiveDate).replace(
                          /-/g,
                          "."
                        )}
                      </div>
                      <ul className="archive_checklist">
                        {archiveData[0].archiveDetails.map((detail) => (
                          <li
                            key={detail.archDetailId}
                            className="archive_check_item"
                          >
                            <label className="custom_checkbox">
                              <input
                                type="checkbox"
                                checked={detail.archDetailState}
                                readOnly
                              />
                              <span className="checkmark"></span>
                              <span
                                className={`archive_text ${
                                  detail.archDetailState ? "checked" : ""
                                }`}
                                style={{
                                  marginLeft: "8px",
                                  marginRight: "12px",
                                }}
                              >
                                {detail.archDetail}
                              </span>
                              <span className="archive_time">
                                {formatTime(detail.archDetailTime)}
                              </span>
                            </label>
                          </li>
                        ))}
                      </ul>
                    </div>
                    <div className="archive_left_memo">
                      <p>{archiveData[0].archiveMemo}</p>
                    </div>
                  </>
                )}
              </div>

              <div className="archive_page_right">
                {archiveData && archiveData.length > 1 && (
                  <>
                    <div className="archive_right_content">
                      <div className="archive_date">
                        {formatDate(archiveData[1].archiveDate).replace(
                          /-/g,
                          "."
                        )}
                      </div>
                      <ul className="archive_checklist">
                        {archiveData[1].archiveDetails.map((detail) => (
                          <li
                            key={detail.archDetailId}
                            className="archive_check_item"
                          >
                            <label className="custom_checkbox">
                              <input
                                type="checkbox"
                                checked={detail.archDetailState}
                                readOnly
                              />
                              <span className="checkmark"></span>
                              <span
                                className={`archive_text ${
                                  detail.archDetailState ? "checked" : ""
                                }`}
                                style={{
                                  marginLeft: "8px",
                                  marginRight: "12px",
                                }}
                              >
                                {detail.archDetail}
                              </span>
                              <span className="archive_time">
                                {formatTime(detail.archDetailTime)}
                              </span>
                            </label>
                          </li>
                        ))}
                      </ul>
                    </div>
                    <div className="archive_right_memo">
                      <p>{archiveData[1].archiveMemo}</p>
                    </div>
                  </>
                )}
              </div>
              <button className="archive_btn_right" onClick={handleNextPage}>
                <Icon
                  icon="material-symbols-light:play-arrow-rounded"
                  width="72"
                  height="72"
                  style={{ color: "#cccccc" }}
                />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Archive;
