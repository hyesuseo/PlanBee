import React, { useEffect, useState } from "react";
import logoYellow from "../images/Logo_Yellow.png";
import "../css/Banner.css";
import axios from "axios";
import { useNavigate } from "react-router-dom";
const Banner = () => {
  const [userId, setUserId] = useState(null);

  useEffect(() => {
    const getUserId = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/planbee/auth/getUserId`,
          {
            withCredentials: true,
          }
        );
        console.log("유저 아이디 fetch 성공", response.data);
        setUserId(response.data);
      } catch (error) {
        console.error("유저 아이디 에러 발생", error);
      }
    };
    getUserId();
  }, []);
  const navigate = useNavigate();

  const logout = async () => {
    try {
      const response = await axios.post(
        `http://localhost:8080/planbee/auth/logout`,
        null,
        { withCredentials: true }
      );
      console.log("로그아웃 완료: ", response.data);
      navigate("/");
    } catch (error) {
      console.error("로그아웃 실패", error);
    }
  };

  return (
    <div className="banner_container">
      <div className="banner_logo">
        <img src={logoYellow} alt="Login Page Logo" />
      </div>
      <div className="banner_btn">
        <div className="banner_username">반갑습니다 {userId}님!</div>
        <button className="banner_logout" onClick={logout}>
          Logout
        </button>
      </div>
    </div>
  );
};

export default Banner;
