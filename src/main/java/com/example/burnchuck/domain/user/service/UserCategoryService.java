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

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCategoryService {

    private final UserCategoryRepository userCategoryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public UserCategoryCreateResponse createUserFavoriteCategory(AuthUser authUser, UserCategoryCreateRequest request) {

        User user = userRepository.findActivateUserById(authUser.getId());

        List<String> requestCategoryList = request.getCategoryCodeList();

        if (requestCategoryList != null) {

            List<Category> categoryList = categoryRepository.findAll();

            List<UserCategory> userCategoryList = categoryList.stream()
                    .filter(category -> requestCategoryList.contains(category.getCode()))
                    .map(category -> new UserCategory(user, category))
                    .toList();

            userCategoryRepository.saveAll(userCategoryList);
        }

        return new UserCategoryCreateResponse(requestCategoryList);
    }
}
