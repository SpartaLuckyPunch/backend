//package com.example.burnchuck.domain.reaction.controller;
//
//import com.example.burnchuck.common.dto.CommonResponse;
//import com.example.burnchuck.domain.reaction.dto.request.AdminReactionCreateRequest;
//import com.example.burnchuck.domain.reaction.dto.response.AdminReactionCreateResponse;
//import com.example.burnchuck.domain.reaction.service.AdminReactionService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import static com.example.burnchuck.common.enums.SuccessMessage.REACTION_CREATE_SUCCESS;
//import static com.example.burnchuck.common.enums.SuccessMessage.REACTION_DELETE_SUCCESS;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api")
//public class AdminReactionController {
//
//    private final AdminReactionService adminReactionService;
//
//    /**
//     * 리액션 종류 생성
//     */
//    @PostMapping("/admin/reactions")
//    public ResponseEntity<CommonResponse<Void>> createAdminReaction(
//            @Valid @RequestBody AdminReactionCreateRequest request
//    ) {
//        adminReactionService.createAdminReaction(request);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(CommonResponse.successNodata(REACTION_CREATE_SUCCESS));
//    }
//
//    /**
//     * 리액션 종류 삭제
//     */
//    @DeleteMapping("/admin/reactions/{reactionId}")
//    public ResponseEntity<CommonResponse<Void>> deleteAdminReaction(
//            @PathVariable Long reactionId
//    ) {
//        adminReactionService.deleteAdminReaction(reactionId);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(CommonResponse.successNodata(REACTION_DELETE_SUCCESS));
//    }
//}
