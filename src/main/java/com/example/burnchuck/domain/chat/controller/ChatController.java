package com.example.burnchuck.domain.chat.controller;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.dto.CommonResponse;
import com.example.burnchuck.common.dto.SliceResponse;
import com.example.burnchuck.common.enums.SuccessMessage;
import com.example.burnchuck.domain.chat.dto.dto.ChatRoomCreationResult;
import com.example.burnchuck.domain.chat.dto.dto.ChatRoomDto;
import com.example.burnchuck.domain.chat.dto.request.ChatMessageRequest;
import com.example.burnchuck.domain.chat.dto.request.ChatRoomCreateRequest;
import com.example.burnchuck.domain.chat.dto.request.ChatRoomNameUpdateRequest;
import com.example.burnchuck.domain.chat.dto.response.ChatMessageResponse;
import com.example.burnchuck.domain.chat.service.ChatRoomService;
import com.example.burnchuck.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.burnchuck.common.enums.SuccessMessage.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    /**
     * 메세지 보내기
     */
    @PostMapping("/messages")
    public ResponseEntity<CommonResponse<ChatMessageResponse>> sendMessage(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ChatMessageRequest request
    ) {
        ChatMessageResponse response = chatService.sendMessage(authUser, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(CHAT_SEND_SUCCESS, response));
    }

    /**
     * 1:1 채팅방 생성 (이미 생성돼 있으면 기존 방 입장)
     */
    @PostMapping("/rooms/private")
    public ResponseEntity<CommonResponse<Long>> createPrivateRoom(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ChatRoomCreateRequest request
    ) {
        ChatRoomCreationResult result = chatRoomService.getOrCreatePrivateRoom(authUser, request.getTargetUserId());

        SuccessMessage message = result.isCreated() ? CHAT_ROOM_CREATE_SUCCESS : CHAT_ROOM_ENTER_SUCCESS;

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(message, result.getRoomId()));
    }


    /**
     * 내 채팅방 목록 조회
     */
    @GetMapping("/rooms")
    public ResponseEntity<CommonResponse<List<ChatRoomDto>>> getMyChatRooms(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        List<ChatRoomDto> rooms = chatRoomService.getMyChatRooms(authUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(CHAT_ROOM_LIST_GET_SUCCESS, rooms));
    }


    /**
     * 채팅 내역 조회
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<CommonResponse<SliceResponse<ChatMessageResponse>>> getChatMessages(
            @PathVariable Long roomId,
            Pageable pageable
    ) {
        Slice<ChatMessageResponse> messages = chatRoomService.getChatMessages(roomId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(CHAT_HISTORY_GET_SUCCESS, SliceResponse.from(messages)));
    }

    /**
     * 채팅방 나가기
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<CommonResponse<Void>> leaveChatRoom(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long roomId
    ) {
        chatRoomService.leaveChatRoom(authUser, roomId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(CHAT_ROOM_LEAVE_SUCCESS));
    }


    /**
     * 채팅방 제목 수정
     */
    @PatchMapping("/{roomId}/name")
    public ResponseEntity<CommonResponse<Void>> updateRoomName(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long roomId,
            @RequestBody ChatRoomNameUpdateRequest request
    ) {
        chatRoomService.updateRoomName(authUser, roomId, request.getName());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(CHAT_ROOM_NAME_UPDATE_SUCCESS));
    }
}
