import React from 'react'
import BoardWriteCom from '../components/BoardWriteCom'
import { useLocation } from 'react-router-dom';

const BoardWrite = () => {
  const location = useLocation();
  const thisGroupId = location.state;
  return (
    <div>
      <BoardWriteCom thisGroupId = {thisGroupId} />
    </div>
  )
}

export default BoardWrite
