package com.example.burnchuck.domain.chat.service;

import static com.example.burnchuck.common.enums.ErrorCode.CANNOT_CHAT_WITH_SELF;
import static com.example.burnchuck.common.enums.ErrorCode.CANNOT_LEAVE_CLOSED_MEETING;
import static com.example.burnchuck.common.enums.ErrorCode.CHAT_ROOM_NOT_FOUND;
import static com.example.burnchuck.common.enums.ErrorCode.CHAT_USER_NOT_FOUND;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.ChatMessage;
import com.example.burnchuck.common.entity.ChatRoom;
import com.example.burnchuck.common.entity.ChatRoomUser;
import com.example.burnchuck.common.entity.Meeting;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.enums.MeetingStatus;
import com.example.burnchuck.common.enums.RoomType;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.common.utils.UserDisplay;
import com.example.burnchuck.domain.chat.dto.dto.ChatRoomCreationResult;
import com.example.burnchuck.domain.chat.dto.dto.ChatRoomDto;
import com.example.burnchuck.domain.chat.dto.dto.ChatRoomMemberDto;
import com.example.burnchuck.domain.chat.dto.response.ChatMessageResponse;
import com.example.burnchuck.domain.chat.dto.response.ChatRoomDetailResponse;
import com.example.burnchuck.domain.chat.repository.ChatMessageRepository;
import com.example.burnchuck.domain.chat.repository.ChatRoomRepository;
import com.example.burnchuck.domain.chat.repository.ChatRoomUserRepository;
import com.example.burnchuck.domain.meeting.repository.MeetingRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    /**
     * 1:1 채팅방 생성 (이미 존재하면 기존 방 ID 반환)
     */
    @Transactional
    public ChatRoomCreationResult getOrCreatePrivateRoom(AuthUser authUser, Long targetUserId) {

        User me = userRepository.findActivateUserById(authUser.getId());
        User target = userRepository.findActivateUserById(targetUserId);

        if (me.getId().equals(target.getId())) {
            throw new CustomException(CANNOT_CHAT_WITH_SELF);
        }

        return chatRoomRepository.findPrivateChatRoom(me.getId(), target.getId())
                .map(chatRoom -> new ChatRoomCreationResult(chatRoom.getId(), false))
                .orElseGet(() -> {
                    Long newRoomId = createPrivateChatRoom(me, target);
                    return new ChatRoomCreationResult(newRoomId, true);
                });
    }

    /**
     * 1:1 채팅방 생성 로직
     */
    private Long createPrivateChatRoom(User me, User target) {
        String roomName = me.getNickname() + ", " + target.getNickname();

        ChatRoom room = new ChatRoom(roomName, RoomType.PRIVATE);
        chatRoomRepository.save(room);

        chatRoomUserRepository.save(new ChatRoomUser(room, me));
        chatRoomUserRepository.save(new ChatRoomUser(room, target));

        return room.getId();
    }

    /**
     * 그룹 채팅방 생성
     * 모임 생성시 자동 호출
     */
    @Transactional
    public void createGroupChatRoom(Meeting meeting, User host) {
        ChatRoom chatRoom = new ChatRoom(meeting.getTitle(), RoomType.GROUP, meeting.getId());
        chatRoomRepository.save(chatRoom);

        ChatRoomUser chatRoomUser = new ChatRoomUser(chatRoom, host);
        chatRoomUserRepository.save(chatRoomUser);
    }

    /**
     * 그룹 채팅방 입장 (모임 참여 시 호출)
     */
    @Transactional
    public void joinGroupChatRoom(Long meetingId, User user) {
        ChatRoom chatRoom = chatRoomRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        boolean isAlreadyMember = chatRoomUserRepository.existsByChatRoomAndUser(chatRoom, user);
        if (isAlreadyMember) {
            return;
        }

        ChatRoomUser chatRoomUser = new ChatRoomUser(chatRoom, user);
        chatRoomUserRepository.save(chatRoomUser);
    }

    /**
     * 내 채팅방 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ChatRoomDto> getMyChatRooms(AuthUser authUser) {
        User user = userRepository.findActivateUserById(authUser.getId());

        List<ChatRoomUser> myRoomUsers = chatRoomUserRepository.findAllActiveByUserId(user.getId());

        return myRoomUsers.stream()
                .map(myRoomUser -> convertToChatRoomDto(myRoomUser, user.getId()))
                .collect(Collectors.toList());
    }

    /**
     * DTO 변환 로직
     */
    private ChatRoomDto convertToChatRoomDto(ChatRoomUser myRoomUser, Long myUserId) {
        ChatRoom room = myRoomUser.getChatRoom();
        String roomName = myRoomUser.getCustomRoomName();

        if (roomName == null) {
            roomName = room.getName();
            if (room.getType() == RoomType.PRIVATE) {
                roomName = getPartnerName(room, myUserId);
            }
        }

        ChatMessage lastMsg = chatMessageRepository.findFirstByRoomIdOrderByCreatedDatetimeDesc(room.getId())
                .orElse(null);

        int memberCount = chatRoomUserRepository.countByChatRoomId(room.getId());

        return ChatRoomDto.of(room, roomName, lastMsg, memberCount);
    }

    /**
     * 채팅 내역 조회 (무한 스크롤)
     */
    @Transactional(readOnly = true)
    public Slice<ChatMessageResponse> getChatMessages(Long roomId, Pageable pageable) {
        chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(CHAT_ROOM_NOT_FOUND));

        return chatMessageRepository.findByRoomIdOrderByCreatedDatetimeDesc(roomId, pageable)
                .map(ChatMessageResponse::from);
    }

    /**
     * 채팅방 나가기
     */
    @Transactional
    public void leaveChatRoom(AuthUser authUser, Long roomId) {
        User user = userRepository.findActivateUserById(authUser.getId());

        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, user.getId())
                .orElseThrow(() -> new CustomException(CHAT_USER_NOT_FOUND));

        ChatRoom room = chatRoomUser.getChatRoom();

        if (room.getType() == RoomType.GROUP) {
            Meeting meeting = meetingRepository.findActivateMeetingById(room.getMeetingId());

            if (meeting.getStatus() == MeetingStatus.CLOSED) {
                throw new CustomException(CANNOT_LEAVE_CLOSED_MEETING);
            }
        }

        chatRoomUser.delete();
    }

    /**
     * 상대방 이름 조회
     */
    private String getPartnerName(ChatRoom room, Long myId) {
        return chatRoomUserRepository.findByChatRoomId(room.getId()).stream()
                .map(ChatRoomUser::getUser)
                .filter(user -> !user.getId().equals(myId))
                .findFirst()
                .map(UserDisplay::resolveNickname)
                .orElse("알 수 없는 사용자");
    }

    /**
     * 채팅방 이름 수정
     */
    @Transactional
    public void updateRoomName(AuthUser authUser, Long roomId, String newName) {
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, authUser.getId())
                .orElseThrow(() -> new CustomException(CHAT_USER_NOT_FOUND));

        chatRoomUser.updateCustomName(newName);
    }

    /**
     * 채팅방 단건 조회 (참여자 목록 포함)
     */
    @Transactional(readOnly = true)
    public ChatRoomDetailResponse getChatRoomDetail(AuthUser authUser, Long roomId) {
        User user = userRepository.findActivateUserById(authUser.getId());

        ChatRoomUser myRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, user.getId())
                .orElseThrow(() -> new CustomException(CHAT_USER_NOT_FOUND));

        ChatRoom room = myRoomUser.getChatRoom();

        String roomName = myRoomUser.getCustomRoomName();
        if (roomName == null) {
            roomName = room.getName();
            if (room.getType() == RoomType.PRIVATE) {
                roomName = getPartnerName(room, user.getId());
            }
        }

        List<ChatRoomUser> roomUsers = chatRoomUserRepository.findByChatRoomId(roomId);
        List<ChatRoomMemberDto> members = roomUsers.stream()
                .map(roomUser -> ChatRoomMemberDto.from(roomUser.getUser()))
                .collect(Collectors.toList());

        return ChatRoomDetailResponse.from(room, roomName, members);
    }
}
