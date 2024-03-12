package com.bingchunmoli.api.config;

import com.bingchunmoli.api.bean.ResultVO;
import com.bingchunmoli.api.config.bean.User;
import com.bingchunmoli.api.config.service.UserService;
import com.bingchunmoli.api.exception.system.ApiSystemException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final HttpServletRequest request;

    @PostMapping("/login")
    public ResultVO<Boolean> login(@Valid @RequestBody User user) {
        if (userService.login(user)) {
            request.getSession().setAttribute("user", user.getId());
            return ResultVO.ok(true);
        }
        throw new ApiSystemException("login fail");
    }

    @GetMapping("/init")
    public ResultVO<Boolean> isInit() {
        return ResultVO.ok(userService.count() == 0L);
    }

    @PutMapping("/register")
    public ResultVO<Boolean> register(@Valid @RequestBody User user) {
        if (userService.register(user)) {
            request.getSession().setAttribute("user", user.getId());
            return ResultVO.ok(true);
        }
        throw new ApiSystemException("register fail");
    }
}
