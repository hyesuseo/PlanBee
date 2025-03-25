import React, { useState } from 'react'
import Banner from '../components/Banner'
import SideBar from '../components/SideBar'
import BoardOneCom from '../components/BoardOneCom'
import BoardListCom from '../components/BoardListCom'
import axios from 'axios'
import { useLocation, useParams } from 'react-router-dom'


const Board = () => {
    const location = useLocation();
    const { posts } = location.state || {};

    console.log("board 받은데이터 처리", posts);
    

  return (
      <div className="main_container">
      <Banner />
      <div className="sidebar_and_content">
        <SideBar />
        <div className="main_content">
          <div className="social_container">
          <div>
        <BoardListCom posts={posts}/>
          </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Board
