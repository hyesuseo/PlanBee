import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom';
import '../css/GroupJoin.css';
import { select } from 'framer-motion/client';
import axios from 'axios';

const GroupJoinPopUp = ({groupName, onClose, groupId}) => {
    //사용자가 선택한 groupName과 groupId를 props로 받음
    //예를 누르면 해당 그룹에 가입하는 api 실행
    //실행결과 {"redirectUrl:/planbee/groups/1", "groupId":1, "message":"그룹 가입 성공" 혹은 실패}
    
    const navigate = useNavigate(); 
    console.log("GroupId", groupId);

    const [isJoined, setIsJoined] = useState(false); //조인된 상태인지 확인
    const [isJoinOpen, setIsJoinOpen] = useState(true)
    const [Info, setInfo] = useState(null);
    
    console.log("Group Join popup selectedGroup", groupId)

    const handleJoinGroup = async () => {
        try {
            const response = await axios.post(
                `http://localhost:8080/planbee/groups/join?groupId=${groupId}`,
                {},
                {
                    withCredentials:true,
                }
            )
            console.log("가입 실행결과 확인", response.data)
            if(response.data.message =='그룹 가입 완료'){
                setIsJoined(true);
                const infoObj = {redirectUrl :response.data.redirectUrl, groupId: groupId};
                setInfo(infoObj);
                
            }
            
            else if (response.data.message == "그룹 가입 실패"){
                console.log("서버에서 그룹가입 실패")
                onClose();
            }
        } catch (error) {
            console.log("그룹가입 중 axios 에러발생생")
            onClose();
        }
        
        
      };

      const handleConfirm = async () => {
        try {
            navigate(`/boardList/${Info.groupId}`, {state: Info})
        } catch (error) {
            console.log("가입 후 페이지 이동 실패")
        }
      }

      const handleNoClick = () => {
        alert("가입이 취소되었습니다!");
        setIsJoinOpen(false);
        onClose();
        navigate("/social"); // 취소 시 Social로 이동(원하는 경로로)
      };
  
   return (
    <div className='popup_overlay'>
   <div className="join_container">  
    {!isJoined ? (
        <>
            <h2 className="join_title">
                {groupName}에 가입하시겠습니까?
            </h2>
            <div className="join_button_area">
                <button className="join_yes" onClick={()=>handleJoinGroup()}>
                    YES
                </button>
                <button className="join_no" onClick={()=>onClose()}>
                    NO
                </button>
            </div>
        </>
    ) : (
        <>
            <h2 className="join_title">
                {groupName}에 가입하신 걸 환영합니다!
            </h2>
            <div className="button-group">
                <button onClick={handleConfirm}>확인</button>
            </div>
        </>
    )}
</div>
</div> )
}

export default GroupJoinPopUp
