package com.devbabys.shoppingmall.Config

import com.devbabys.shoppingmall.Security.JwtRequestFilter
import com.devbabys.shoppingmall.Service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtRequestFilter: JwtRequestFilter
) {
    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        println("########## Config : SecurityConfig : authenticationManager ##########")
        return authenticationConfiguration.getAuthenticationManager()
        //return authenticationConfiguration.authenticationManager as ProviderManager
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder(10)

    private val allowedUrls = arrayOf("/",
        "/css/**", "/js/**", "/uploads/**", "/files/**", // 정적 자원에 대한 접근 허용
        "/user/register", "/user/login", "user/logout", // 로그인 관련
        "/user/getuser", "user/update", "user/findemail", "user/finduser", "user/resetpw", "user/delete", "/user/cart",  // 회원 관련
        "/product/category/add", "/product/category/list", "/product/category/delete", "/product/category/update", // 상품 카테고리 관련
        "/product/list", // 상품 페이지 관련
        "/product/create",
        "test" // 테스트를 위한 URL 경로
    )

    @Bean
    protected fun securityFilterChain (http: HttpSecurity) = http
        .csrf { it.disable() }
        .authorizeHttpRequests {
            it.requestMatchers(*allowedUrls).permitAll()	// 허용할 URL 주소
                .anyRequest().authenticated()	// 그 외의 모든 요청은 인증 필요
        }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }	// 세션 미사용
        .build()
}