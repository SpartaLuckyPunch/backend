package com.example.burnchuck.domain.user.service;

import com.example.burnchuck.common.dto.AuthUser;
import com.example.burnchuck.common.entity.Category;
import com.example.burnchuck.common.entity.User;
import com.example.burnchuck.common.entity.UserCategory;
import com.example.burnchuck.domain.category.repository.CategoryRepository;
import com.example.burnchuck.domain.user.dto.request.UserCategoryCreateRequest;
import com.example.burnchuck.domain.user.dto.response.UserCategoryCreateResponse;
import com.example.burnchuck.domain.user.repository.UserCategoryRepository;
import com.example.burnchuck.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCategoryService {

    private final UserCategoryRepository userCategoryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public UserCategoryCreateResponse createUserFavoriteCategory(AuthUser authUser, UserCategoryCreateRequest request) {

        User user = userRepository.findActivateUserById(authUser.getId());

        Category category = categoryRepository.findCategoryByCode(request.getCategoryCode());

        UserCategory userCategory = new UserCategory(user, category);

        userCategoryRepository.save(userCategory);

        return null;
    }
}
