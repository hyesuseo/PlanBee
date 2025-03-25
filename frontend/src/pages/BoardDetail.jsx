import React from 'react'
import BoardOneCom from '../components/BoardOneCom'
import { useLocation, useParams } from 'react-router-dom';

const BoardDetail = () => {
  const params = useParams(); //BoardListCom에서 주소를 통해 thisPostId를 전달하고
  const location = useLocation(); // state를 통해서 thisGroupId를 전달한다
  const thisGroupId = location.state;
  return (
    <BoardOneCom thisPostId={params} thisGroupId={thisGroupId}/>
  )
}

export default BoardDetail
