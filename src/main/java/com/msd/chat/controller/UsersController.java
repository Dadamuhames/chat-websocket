package com.msd.chat.controller;


import com.msd.chat.domain.UserEntity;
import com.msd.chat.model.response.UserResponse;
import com.msd.chat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {
    private final UserService userService;

    @GetMapping({"", "/"})
    public ResponseEntity<Page<UserResponse>> search(@RequestParam String search,
                                                     @AuthenticationPrincipal UserEntity user,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<UserResponse> users = userService.search(search, user, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> one(@PathVariable("id") Long id, @AuthenticationPrincipal UserEntity user) {
        UserResponse response = userService.one(id, user);
        return  ResponseEntity.ok(response);
    }
}
