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
  const [tempUserIdError, setTempUserIdError] = useState("") //회원가입시 에러방지지
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
    if( name === "tempuserId"){
      if( value.length > 10){
        setTempUserIdError("아이디는 최대 10자까지 가능합니다")
      } else {
        setTempUserIdError("")
      }
    }
    setUserInfo((prevInfo) => ({
      ...prevInfo,
      [name]: value,
    }));
  };

  const SendCode = async () => {
    if (!userInfo || !userInfo.tempUserEmail) {
      alert("이메일 정보를 입력해주세요.");
      return;
    }

    try {
      console.log("전송할 사용자 정보:", userInfo);
      const response = await axios.post(
        `https://wherethereis.site/planbee/auth/email/send`,
        userInfo
      );

      // response.data 값에 따라 분류
      switch (response.data) {
        case -2:
          alert("이미 가입된 이메일이 있습니다.");
          break;
        case -1:
          alert("동일 아이디가 가입되어 있습니다.");
          break;
        case 1:
          alert("인증코드가 이메일로 전송되었습니다!");
          break;
        case 0:
          alert("오류가 발생하였습니다.");
          break;
        default:
          alert("예상하지 못한 응답입니다.");
      }

      console.log("응답 데이터:", response.data);
    } catch (error) {
      console.error(
        "인증코드 전송 실패!",
        error.response ? error.response.data : error.message
      );
      alert(
        `인증코드 전송 실패: ${
          error.response ? error.response.data.message : "네트워크 오류"
        }`
      );
    }
  };

  const VerifyCode = async () => {
    try {
      console.log(userInfo, userCode);
      const dataToSend = { ...userInfo, tempUserCode: userCode };
      const response = await axios.post(
        `https://wherethereis.site/planbee/auth/email/verify`,
        dataToSend
      );
      console.log("인증 완료!", response.data);
      alert("인증 완료!");
    } catch (error) {
      console.error("인증 실패!", error);
      alert("인증 실패! 잠시 후 다시 시도하세요!");
    }
  };
  const SignUp = async () => {
    try {
      console.log(userInfo, userCode);
      const dataToSend = { ...userInfo, tempUserCode: userCode };
      const response = await axios.post(
        `https://wherethereis.site/planbee/auth/register`,
        dataToSend
      );
      console.log("회원가입 완료!", response.data);
      alert("회원가입 완료!");
      togglePopup();
    } catch (error) {
      alert("회원가입 실패! 잠시 후 다시 시도하세요요");
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
  
  const Login = async () => {
    try {
      const loginData = {
        "userId": userId,
        "userPw": userPw
      }
      console.log("로그인요청데이터", loginData)
      const response = await axios.post(
        `https://wherethereis.site/planbee/auth/login`,
        loginData,
        {
          headers: { 
            "Content-Type": "application/json" 
          },
          withCredentials: true
        }
        
      );
      alert("로그인 완료!");
      console.log("로그인 완료!", response.data);
      navigate("/todolist");
      makeSession();
    } catch (error) {
      console.error("로그인 실패!", error);
      alert("로그인 실패. 잠시 후 다시 시도하세요");
    }
  };

  const makeSession = async () => {
    try {
      const response = await axios.get(
        `https://wherethereis.site/planbee/auth/session`,
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
                type="button"
                value="Login"
                onClick={Login}
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
                  {tempUserIdError && <div className="errorMsg">{tempUserIdError}</div>}
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
                    value="Join"
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
