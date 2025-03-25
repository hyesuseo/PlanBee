import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import PopUp from "../components/PopUp";
import "../css/SignIn.css";
import "../css/SignUp.css";
import logoBlack from "../images/Logo_Black.png";
import logoYellow from "../images/Logo_Yellow.png";

const SignIn = () => {
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState({
    tempUserId: "",
    tempUserPw: "",
    tempUserName: "",
    tempUserEmail: "",
    tempUserPhone: "",
  });
  const [userCode, setUserCode] = useState("");
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUserInfo((prevInfo) => ({
      ...prevInfo,
      [name]: value,
    }));
  };

  const SendCode = async () => {
    try {
      console.log(userInfo);
      const response = await axios.post(
        `http://localhost:8080/planbee/auth/email/send`,
        userInfo
      );
      console.log("인증코드 전송 성공!", response.data);
    } catch (error) {
      console.error("인증코드 전송 실패!", error);
    }
  };
  const VerifyCode = async () => {
    try {
      console.log(userInfo, userCode);
      const dataToSend = { ...userInfo, tempUserCode: userCode };
      const response = await axios.post(
        `http://localhost:8080/planbee/auth/email/verify`,
        dataToSend
      );
      console.log("인증 완료!", response.data);
    } catch (error) {
      console.error("인증 실패!", error);
    }
  };
  const SignUp = async () => {
    try {
      console.log(userInfo, userCode);
      const dataToSend = { ...userInfo, tempUserCode: userCode };
      const response = await axios.post(
        `http://localhost:8080/planbee/auth/register`,
        dataToSend
      );
      console.log("회원가입 완료!", response.data);
      togglePopup();
    } catch (error) {
      console.error("회원가입 실패!", error);
    }
  };
  const [userId, setUserId] = useState(""); // 로그인용 userId 상태
  const [userPw, setUserPw] = useState(""); // 로그인용 userPw 상태

  const handleLoginInputChange = (e) => {
    const { name, value } = e.target;
    if (name === "userId") {
      setUserId(value);
    } else if (name === "userPw") {
      setUserPw(value);
    }
  };
  const loginData = {
    userId: userId,
    userPw: userPw,
  };

  const Login = async () => {
    try {
      const response = await axios.post(
        `http://localhost:8080/planbee/auth/login`,
        loginData,
        { withCredentials: true }
      );
      console.log("로그인 완료!", response.data);
      navigate("/todolist");
      makeSession();
    } catch (error) {
      console.error("로그인 실패!", error);
    }
  };

  const makeSession = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/planbee/auth/session`,
        {
          withCredentials: true,
        }
      );
      console.log("세션 요청 여부: ", response.data);
    } catch (error) {
      console.error("세션 fetching 실패", error);
    }
  };

  const [isPopupOpen, setIsPopupOpen] = useState(false);

  const togglePopup = () => {
    setIsPopupOpen((prev) => !prev);
  };

  return (
    <div className="login_container">
      <div className="login_boxL">
        <div className="login_boxL_yellow">
          <img src={logoBlack} alt="Login Page Logo" />
        </div>
        <div className="login_boxL_black">
          <img src={logoYellow} alt="Login Page Logo" />
        </div>
      </div>
      <div className="login_boxR">
        <div className="login_logo">
          <img src={logoBlack} alt="Login Page Logo" />
        </div>
        <form className="form">
          <input
            className="login_text"
            name="userId"
            type="text"
            placeholder="username"
            value={userId}
            onChange={handleLoginInputChange}
          />
          <input
            className="login_text"
            name="userPw"
            type="password"
            placeholder="password"
            value={userPw}
            onChange={handleLoginInputChange}
          />

          <div className="form_btn">
            <div className="logIn_button">
              <input
                className="login_btn"
                type="submit"
                value="Login"
                onClick={(e) => {
                  e.preventDefault();
                  Login();
                }}
              />
            </div>
            <button
              className="login_btn"
              onClick={(e) => {
                e.preventDefault();
                togglePopup();
              }}
            >
              SignUp
            </button>
            <PopUp isOpen={isPopupOpen} onClose={togglePopup}>
              <div className="signup_container">
                <div className="signup_logo">
                  <img src={logoYellow} alt="Login Page Logo" />
                </div>
                <div className="signup_form">
                  <input
                    type="text"
                    id="userId"
                    name="tempUserId"
                    placeholder="User ID"
                    value={userInfo.tempUserId}
                    onChange={handleInputChange}
                  />
                  <input
                    type="password"
                    id="userPassword"
                    name="tempUserPw"
                    placeholder="Password"
                    value={userInfo.tempUserPw}
                    onChange={handleInputChange}
                  />
                  <input
                    type="password"
                    id="confirmPassword"
                    name="confirmPassword"
                    placeholder="Confirm Password"
                  />
                  <input
                    type="text"
                    id="userName"
                    name="tempUserName"
                    placeholder="User Name"
                    value={userInfo.tempUserName}
                    onChange={handleInputChange}
                  />
                  <div className="email_certificate">
                    <input
                      type="email"
                      id="userEmail"
                      name="tempUserEmail"
                      placeholder="Email"
                      value={userInfo.tempUserEmail}
                      onChange={handleInputChange}
                    />
                    <button
                      onClick={(e) => {
                        e.preventDefault(); // 기본 동작 취소
                        SendCode(); // 먼저 SendCode 실행
                      }}
                      className="signup_btn1"
                    >
                      Send Code
                    </button>
                  </div>
                  <div className="email_certificate">
                    <input
                      type="text"
                      id="verificationCode"
                      name="userCode"
                      placeholder="Verification Code"
                      value={userCode}
                      onChange={(e) => setUserCode(e.target.value)}
                    />
                    <button
                      onClick={(e) => {
                        e.preventDefault();
                        VerifyCode();
                      }}
                      className="signup_btn2"
                    >
                      Complete
                    </button>
                  </div>
                  <input
                    type="tel"
                    id="userPhone"
                    name="tempUserPhone"
                    placeholder="010-1234-5678"
                    value={userInfo.tempUserPhone}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="signup_button">
                  <input
                    className="signup_btn"
                    type="submit"
                    value="SignUp"
                    onClick={(e) => {
                      e.preventDefault();
                      SignUp();
                    }}
                  />
                </div>
              </div>
            </PopUp>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SignIn;
