import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { FaArrowLeft, FaEllipsisV } from 'react-icons/fa';
import { useNavigate, useParams } from 'react-router-dom';
import Banner from './Banner';
import SideBar from './SideBar';
import '../css/BoardOne.css';
import ReplyInputCom from './ReplyInputCom';

const BoardOneCom = ({ thisPostId, thisGroupId }) => {
  const sessionId = "팥붕"; // 하드코딩된 사용자 ID
  const navigate = useNavigate();
  const { postId } = useParams();

  const [thisPost, setThisPost] = useState({});
  const [reply, setReply] = useState([]);
  const [comment, setComment] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [editedPost, setEditedPost] = useState({ postTitle: "", postContent: "" });
  const [activeMenu, setActiveMenu] = useState(null);
  const [editingReplyId, setEditingReplyId] = useState(null);
  const [editingReplyContent, setEditingReplyContent] = useState("");
  
  // 대댓글(댓글의 댓글) 관련 상태
  const [activeNestedReplyId, setActiveNestedReplyId] = useState(null);
  const [nestedReplyContent, setNestedReplyContent] = useState("");

  useEffect(() => {
    if (thisPostId && thisGroupId) {
      fetchThisPost();
      handleHit();
    }
  }, [thisPostId, thisGroupId]);

  // 글 & 댓글 가져오기
  const fetchThisPost = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/planbee/groups/${thisGroupId.thisGroupId}/boards/${thisPostId.id}`,
        { withCredentials: true }
      );
      setThisPost(response.data.post);
      setReply(response.data.replies);
    } catch (error) {
      console.log("BoardOneCom, 글 하나 가져오기 실패", error);
    }
  };

  // 조회수 올리기
  const handleHit = async () => {
    try {
      await axios.put(
        `http://localhost:8080/planbee/groups/${thisGroupId.thisGroupId}/boards/${thisPostId.id}/hit`,
        {},
        { withCredentials: true }
      );
    } catch (error) {
      console.log("조회수 증가 실패", error);
    }
  };

  // 글 수정 로직
  const handleModify = () => setIsEditing(true);
  const handleChange = (e) => {
    setEditedPost({ ...editedPost, [e.target.name]: e.target.value });
  };
  const handleModifyingSave = async () => {
    try {
      await axios.put(
        `http://localhost:8080/planbee/groups/${thisGroupId.thisGroupId}/boards/${thisPostId.id}`,
        {
          postTitle: editedPost.title,
          postContent: editedPost.contents,
        },
        { withCredentials: true }
      );
      setThisPost({
        ...thisPost,
        postTitle: editedPost.title,
        postContent: editedPost.contents,
      });
      setIsEditing(false);
    } catch (error) {
      console.log("게시글 수정 실패", error);
    }
  };
  const handleCancel = () => setIsEditing(false);

  // 글 삭제
  const handleDel = async () => {
    if (window.confirm("삭제하시겠습니까?")) {
      try {
        await axios.delete(
          `http://localhost:8080/planbee/groups/${thisGroupId.thisGroupId}/boards/${thisPostId.id}`,
          { withCredentials: true }
        );
        navigate(`/boardList/${thisGroupId.thisGroupId}`, {
          state: { 
            groupId: thisGroupId.thisGroupId,
            redirectUrl: `/planbee/groups/${thisGroupId.thisGroupId}`
          }
        });
      } catch (error) {
        console.log("삭제 실패", error);
      }
    }
  };

  // 댓글 작성
  const handleAddReply = async () => {
    try {
      await axios.post(
        `http://localhost:8080/planbee/groups/${thisGroupId.thisGroupId}/boards/${thisPostId.id}/reply`,
        { replyContent: comment },
        { withCredentials: true }
      );
      setComment("");
      fetchThisPost();
    } catch (error) {
      console.log("댓글 입력 에러", error);
    }
  };

  // 대댓글 작성
  const handleAddNestedReply = async (parentReplyId) => {
    try {
      await axios.post(
        `http://localhost:8080/planbee/groups/${thisGroupId.thisGroupId}/boards/${thisPostId.id}/reply/${parentReplyId}`,
        { replyContent: nestedReplyContent },
        { withCredentials: true }
      );
      // 등록 후 입력란 초기화 및 닫기
      setNestedReplyContent("");
      setActiveNestedReplyId(null);
      fetchThisPost();
    } catch (error) {
      console.log("대댓글 입력 에러", error);
    }
  };

  // 댓글 수정
  const handleModifyReply = (replyId, currentContent) => {
    setEditingReplyId(replyId);
    setEditingReplyContent(currentContent);
  };
  const handleModiSaveReply = async (replyId) => {
    try {
      await axios.put(
        `http://localhost:8080/planbee/groups/${thisGroupId.thisGroupId}/boards/${thisPostId.id}/reply/${replyId}`,
        { replyContent: editingReplyContent },
        { withCredentials: true }
      );
      setEditingReplyId(null);
      setEditingReplyContent("");
      fetchThisPost();
    } catch (error) {
      console.log("댓글 수정 실패", error);
    }
  };
  const handleCancelReply = () => {
    setEditingReplyId(null);
    setEditingReplyContent("");
  };

  // 댓글 삭제
  const handleDeleteReply = async (replyId) => {
    try {
      await axios.delete(
        `http://localhost:8080/planbee/groups/${thisGroupId.thisGroupId}/boards/${thisPostId.id}/reply/${replyId}`,
        { withCredentials: true }
      );
      fetchThisPost();
    } catch (error) {
      console.log("댓글 삭제 실패", error);
    }
  };

  // 드롭다운 메뉴 토글
  const toggleMenu = (id) => {
    setActiveMenu(activeMenu === id ? null : id);
  };

  // 토글: 대댓글 입력란 보이기/숨기기
  const toggleNestedReply = (replyId) => {
    // 이미 열려있으면 닫고, 아니면 해당 댓글 id로 열기
    setActiveNestedReplyId(activeNestedReplyId === replyId ? null : replyId);
    setNestedReplyContent("");
  };

  // 댓글 렌더링 (대댓글 포함) - 수정됨: 옵션 드롭다운 내부에 "대댓글 작성" 옵션 추가
const renderReplies = (replies, indent = 0) => {
  return replies.map((reply) => (
    <div key={reply.replyId} style={{ marginLeft: indent }}>
      <div className="comment">
        <div className="comment_user" style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          <div className={`user_avatar ${reply.avatarClass || ""}`} />
          <span className="user_name">{reply.userId}</span>
          {/* 항상 옵션 버튼 표시 (작성자 여부와 상관없이) */}
          <button
            className="options_button"
            onClick={() => toggleMenu(reply.replyId)}
          >
            <FaEllipsisV />
          </button>
        </div>
        <div className="comment_text_box">
          {editingReplyId === reply.replyId ? (
            <>
              <textarea
                value={editingReplyContent}
                onChange={(e) => setEditingReplyContent(e.target.value)}
                style={{
                  width: "100%",
                  padding: "8px",
                  border: "1px solid #ddd",
                  borderRadius: "5px",
                  resize: "vertical",
                }}
              />
              <div style={{ marginTop: "5px", textAlign: "right" }}>
                <button onClick={() => handleModiSaveReply(reply.replyId)} style={{ marginRight: "5px" }}>
                  저장
                </button>
                <button onClick={handleCancelReply}>취소</button>
              </div>
            </>
          ) : (
            <>
              <p className="comment_text">{reply.replyContent}</p>
              <span className="comment_time">{reply.replyDate}</span>
            </>
          )}
        </div>

        {/* 옵션 드롭다운 메뉴 */}
        {activeMenu === reply.replyId && (
          <div className="dropdown_menu comment_dropdown">
            {/* 최상위 댓글일 경우에만 "대댓글 작성" 옵션 표시 */}
            {indent === 0 && (
              <button onClick={() => toggleNestedReply(reply.replyId)}>
                대댓글 작성
              </button>
            )}
            {/* 내 댓글인 경우 수정/삭제 옵션 추가 */}
            {reply.userId === sessionId && (
              <>
                <button onClick={() => handleModifyReply(reply.replyId, reply.replyContent)}>
                  수정
                </button>
                <button onClick={() => handleDeleteReply(reply.replyId)}>삭제</button>
              </>
            )}
          </div>
        )}

        {/* 대댓글 입력란 (토글된 댓글에 대해 표시) - 최상위 댓글에만 적용 */}
        {activeNestedReplyId === reply.replyId && indent === 0 && (
          <div
            style={{
              marginLeft: 20,
              marginTop: 10,
              backgroundColor: "white",          
              padding: "10px",
              border: "1px solid #ddd",          
              borderRadius: "10px"
            }}
          >
            <div style={{ marginBottom: "5px", fontSize: "0.9em", color: "gray" }}>
              대댓글 작성
            </div>
            <textarea
              placeholder="대댓글을 입력하세요..."
              value={nestedReplyContent}
              onChange={(e) => setNestedReplyContent(e.target.value)}
              style={{
                width: "100%",
                padding: "8px",
                border: "1px solid #ddd",
                borderRadius: "10px",
                resize: "vertical",
              }}
            />
            <div style={{ marginTop: "5px", textAlign: "right" }}>
              <button onClick={() => handleAddNestedReply(reply.replyId)} style={{ marginRight: "5px" }}>
                등록
              </button>
              <button onClick={() => setActiveNestedReplyId(null)}>취소</button>
            </div>
          </div>
        )}
      </div>
      {reply.replies && reply.replies.length > 0 && renderReplies(reply.replies, indent + 20)}
    </div>
  ));
};


  // 수정 모드 렌더링
  const renderEditMode = () => (
    <div className="post_container">
      <button className="back_button" onClick={() => setIsEditing(false)}>
        <FaArrowLeft className="back_icon" />
      </button>
      <div className="post_header">
        <input
          type="text"
          name="title"
          value={editedPost.title}
          onChange={handleChange}
          className="post_title_input"
        />
      </div>
      <hr className="post_divider" />
      <div className="post_content">
        <textarea
          name="contents"
          value={editedPost.contents}
          onChange={handleChange}
          className="post_content_input"
          rows="10"
        />
      </div>
      <div className="edit_buttons">
        <button onClick={handleModifyingSave}>저장</button>
        <button onClick={handleCancel}>취소</button>
      </div>
    </div>
  );

  // 진척도 카드 여부 판단
  const isProgressCard = () => {
    const content = thisPost.postContent || "";
    return (
      content.includes("font-family: Arial") ||
      content.includes("flex-direction: column")
    );
  };

  // 글 보기 모드 렌더링
  const renderViewMode = () => (
    <div className="post_container">
      <button className="back_button" onClick={() => navigate(-1)}>
        <FaArrowLeft className="back_icon" />
      </button>
      <div className="post_header">
        <h2 className="post_title">{thisPost.postTitle}</h2>
        {thisPost.userId === sessionId && (
          <button className="options_button" onClick={() => toggleMenu("post")}>
            <FaEllipsisV />
          </button>
        )}
        {activeMenu === "post" && (
          <div className="dropdown_menu post_dropdown">
            <button onClick={handleModify}>수정</button>
            <button onClick={() => handleDel()}>삭제</button>
          </div>
        )}
      </div>
      <hr className="post_divider" />
      <div className="post_info">
        <span>{thisPost.userId}</span>
        <span>조회수: {thisPost.postHit}</span>
        <span>{thisPost.postDate}</span>
      </div>
      <div className="post_content">
        {isProgressCard() ? (
          <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <div dangerouslySetInnerHTML={{ __html: thisPost.postContent }} />
          </div>
        ) : (
          <p>{thisPost.postContent}</p>
        )}
      </div>
      <div className="comment_section">
        {renderReplies(reply)}
        <ReplyInputCom
          comment={comment}
          setComment={setComment}
          handleAddReply={handleAddReply}
          fetchThisPost={fetchThisPost}
        />
      </div>
    </div>
  );

  // thisPost 변경 시, 수정 모드에서 보여줄 데이터 업데이트
  useEffect(() => {
    if (thisPost.postTitle && thisPost.postContent) {
      setEditedPost({ title: thisPost.postTitle, contents: thisPost.postContent });
    }
  }, [thisPost]);

  return (
    <div className="main_container">
      <Banner />
      <div className="sidebar_and_content">
        <SideBar />
        <div className="main_content">
          {isEditing ? renderEditMode() : renderViewMode()}
        </div>
      </div>
    </div>
  );
};

export default BoardOneCom;
