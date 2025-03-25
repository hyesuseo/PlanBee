import React, { useEffect } from 'react'
import { useState, useNavigate } from 'react';
import { Link } from 'react-router-dom';
import '../css/SocialList.css';
import axios from 'axios';
import GroupJoinPopUp from './GroupJoinPopUp';
import { select } from 'framer-motion/client';

const SocialCom = ( {Info, sessionId} ) => { //그룹리스트 불러오는 url 위주로 전달
  //Info: {"redirectUrl:"/planbee/groups/list", "groupId:null"}
  //Info받은것 기반으로 url을 변수에 저장 후 fetchGroupList해서 그룹리스트 렌더링
  //그 후 그룹 클릭하면 selectedGroup변수에 저장해서
  //그 그룹아이디만 저장

  const [requestUrl, setRequestUrl] = useState(Info.redirectUrl) //소셜에서 받아온 url을 저장한다
    //이후 fetchGroupList해서 그룹리스트 렌더링
  const [isJoinOpen, setIsJoinOpen] = useState(false);
  const [selectedGroup, setSelectedGroup] = useState(null);
  const [selectedGId, setSelectedGId] = useState(null);
    
  const [groups, setGroups] = useState(null); //그룹들펼치기 위해 groups에 그룹에 대한 객체 정보를 저장한다

    
   const fetchGroups = async () => { //requestUrl을 통해서 그룹들의 리스트 렌더링
    try {
      const response = await axios.get(
        `http://localhost:8080/${requestUrl}`,
        {
          withCredentials:true,
        }
      )
      console.log("SocialCom 그룹정보가져오기 실행:", response.data);
      
      setGroups(response.data) //그룹전체 정보를 groups에 저장장
    } catch (error) {
      console.log("게시판 정보 가져오기 실패", error)
    }
   }
  useEffect(()=>{                                                                                                                                                                            
    fetchGroups();
  },[])

  const handleOpenModal = (groupName, groupId) =>{
    console.log("선택된 groupName:", groupName)
    
    setSelectedGroup(groupName); //selectedGroup에 저장함
    setSelectedGId(groupId)
    
    console.log("selectedGroup 저장완료?", selectedGroup)
    setIsJoinOpen(true);
};

  const handleCloseModal = () => {
  setIsJoinOpen(false);
  };
  
  if(!groups){
  return <div>Loading...</div>
  }
   
  


    return(<>
            
            <div className="social_header">
            </div>
  
            {/* 소셜 컨테이너 */}
            <div className="social_container">
              {/* 그룹 리스트 */}
              
              <div className="group_list">
                  {groups.map((group) => (
                    <div key= {group.id} className="group_item">
                    <div className="group_left">
                    <div className="group_title">{group.groupName}</div>
                    <div className="group_desc">{group.groupKeywords}</div>
                    </div>
                    <div className="group_right">
                    
                    <button className="join_button" onClick={()=>handleOpenModal(group.groupName, group.groupId)}>
                    <div style={{ textDecoration: "none", color: "inherit", cursor: "pointer" }} >
                        Join
                    </div>
                    
                    </button>
                  
                    </div>
                    </div>
                  ))}
                 
  
              </div> 
              {/* end of group_list */}
            </div>
            {/* end of social_container */}
            {/* {selectedGroup && console.log("social컴포넌트", selectedGroup.groupName)}
            {selectedGroup && console.log("social컴포넌트id", selectedGroup.groupId)} */}
            {isJoinOpen && <GroupJoinPopUp groupName={selectedGroup} 
            groupId= {selectedGId}
            onClose={handleCloseModal} />}
            </>
  
    )

}

export default SocialCom
