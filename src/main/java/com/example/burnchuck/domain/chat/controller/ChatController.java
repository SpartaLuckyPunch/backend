package com.example.burnchuck.domain.chat.controller;

import static com.example.burnchuck.common.enums.SuccessMessage.CHAT_HISTORY_GET_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.CHAT_READ_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.CHAT_ROOM_CREATE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.CHAT_ROOM_ENTER_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.CHAT_ROOM_GET_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.CHAT_ROOM_LEAVE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.CHAT_ROOM_LIST_GET_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.CHAT_ROOM_NAME_UPDATE_SUCCESS;
import static com.example.burnchuck.common.enums.SuccessMessage.CHAT_SEND_SUCCESS;

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
import com.example.burnchuck.domain.chat.dto.response.ChatRoomDetailResponse;
import com.example.burnchuck.domain.chat.service.ChatRoomService;
import com.example.burnchuck.domain.chat.service.ChatService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    /**
     * 메세지 보내기
     */
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<CommonResponse<ChatMessageResponse>> sendMessage(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long roomId,
            @Valid @RequestBody ChatMessageRequest request
    ) {
        ChatMessageResponse response = chatService.sendMessage(authUser, roomId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(CHAT_SEND_SUCCESS, response));
    }

    /**
     * 채팅방 읽음 처리 (입장 시, 혹은 스크롤 시 호출)
     */
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<CommonResponse<Void>> readChatRoom(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long roomId
    ) {
        chatService.readMessage(authUser.getId(), roomId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(CHAT_READ_SUCCESS));
    }

    /**
     * 1:1 채팅방 생성 (이미 생성돼 있으면 기존 방 입장)
     */
    @PostMapping("/rooms/private")
    public ResponseEntity<CommonResponse<Long>> createPrivateRoom(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ChatRoomCreateRequest request
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
    @DeleteMapping("/rooms/{roomId}")
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
    @PatchMapping("/rooms/{roomId}/name")
    public ResponseEntity<CommonResponse<Void>> updateRoomName(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long roomId,
            @Valid @RequestBody ChatRoomNameUpdateRequest request
    ) {
        chatRoomService.updateRoomName(authUser, roomId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.successNodata(CHAT_ROOM_NAME_UPDATE_SUCCESS));
    }

    /**
     * 채팅방 단건 조회
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<CommonResponse<ChatRoomDetailResponse>> getChatRoomDetail(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long roomId
    ) {
        ChatRoomDetailResponse response = chatRoomService.getChatRoomDetail(authUser, roomId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(CHAT_ROOM_GET_SUCCESS, response));
    }
}
