package com.devbabys.shoppingmall.Controller

import com.devbabys.shoppingmall.DTO.AuthenticationRequest
import com.devbabys.shoppingmall.Security.JwtUtil
import com.devbabys.shoppingmall.Service.JwtService
import com.devbabys.shoppingmall.Service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.coyote.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.security.MessageDigest

@Controller
class UserController(
    @Autowired
    private val userService: UserService,
    @Autowired
    private val jwtUtil: JwtUtil
) {

    // 회원가입
    @PostMapping("user/register")
    fun postSign(model: Model,
                 @RequestParam(value="email") email: String,
                 @RequestParam(value="password") password: String,
                 @RequestParam(value="username") userName: String
    ): String {
        var result = userService.sign(email, password, userName)

        model.addAttribute(("result"), result) // 템플릿에 회원가입 유무 전달

        return if (result) {
            "redirect:/"
        } else {
            "user/register"
        }
    }

    // 로그인
    @PostMapping("user/login")
    fun postLogin(model: Model,
                  @RequestParam(value="email") email: String,
                  @RequestParam(value="password") password: String,
                  response: HttpServletResponse
    ): String {
        var authenticationRequest = AuthenticationRequest(email, password)
        var result = userService.login(authenticationRequest, response)

        model.addAttribute("result", result)

        return if (result) {
            "redirect:/"
        } else {
            "user/login"
        }
    }

    // 로그아웃
    @GetMapping("user/logout")
    fun getLogout(model: Model,
                  response: HttpServletResponse
    ): String {
        var result = userService.logout(response)
        model.addAttribute("result", result)

        return if (result) {
            "redirect:/"
        } else {
            "user/login"
        }
    }

    @GetMapping("user/test")
    @ResponseBody
    fun test(@RequestHeader("Authorization") token: String?): ResponseEntity<Any> {
    //fun test(@CookieValue("jwt") jwt: String?): String {
        if (token == null) {
            return ResponseEntity.ok("failed")
        } else {
            val jwt = token.substring(7)

            var email = jwtUtil.extractedEmail(jwt)

            if (jwtUtil.validateToken(jwt, email)) {
                return ResponseEntity.ok("success")
            } else {
                return ResponseEntity.ok("token is not valid")
            }
        }
    }


}