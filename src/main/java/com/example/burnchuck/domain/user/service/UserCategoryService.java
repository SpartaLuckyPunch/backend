package com.example.burnchuck.domain.user.service;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserCategory;
import com.example.burnchuck.common.enums.ErrorCode;
import com.example.burnchuck.common.exception.CustomException;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import com.example.burnchuck.domain.user.dto.request.UserCategoryCreateRequest;
import com.example.burnchuck.domain.user.dto.response.UserCategoryGetResponse;
import com.example.burnchuck.domain.user.repository.UserCategoryRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCategoryService {

    private final UserCategoryRepository userCategoryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 관심 카테고리 등록
     */
    @Transactional
    public void createUserFavoriteCategory(AuthUser authUser, UserCategoryCreateRequest request) {

        User user = userRepository.findActivateUserById(authUser.getId());

        List<Category> categories = categoryRepository.findByCodeIn(request.getCategoryCodeList());

        if (categories.size() != request.getCategoryCodeList().size()) {
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        List<UserCategory> existUserCategory = userCategoryRepository.findByUser(user);
        Set<String> existCategory = existUserCategory.stream()
                .map(uc -> uc.getCategory().getCode())
                .collect(Collectors.toSet());

        Set<String> requestCategory = categories.stream()
                .map(Category::getCode)
                .collect(Collectors.toSet());

        List<UserCategory> delete = existUserCategory.stream()
                .filter(uc -> !requestCategory.contains(uc.getCategory().getCode()))
                .toList();

        List<UserCategory> add = categories.stream()
                .filter(category -> !existCategory.contains(category.getCode()))
                .map(category -> new UserCategory(user, category))
                .toList();

        userCategoryRepository.deleteAll(delete);
        userCategoryRepository.saveAll(add);
        userCategoryRepository.flush();
    }

    /**
     * 관심 카테고리 목록 조회
     */
    @Transactional
    public UserCategoryGetResponse getUserFavoriteCategory(AuthUser authUser) {

        User user = userRepository.findActivateUserById(authUser.getId());

        List<UserCategory> userCategories = userCategoryRepository.findByUser(user);

        List<String> categoryCodeList = userCategories.stream()
                .map(uc -> uc.getCategory().getCode())
                .toList();

        return new UserCategoryGetResponse(categoryCodeList);
    }
}
