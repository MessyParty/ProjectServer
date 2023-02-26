package com.glimps.glimpsserver.common.presentation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glimps.glimpsserver.common.authentication.UserAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Test", description = "Test API")
@RestController
public class TestController {


	@Operation(summary = "Test API", description = "권한이 필요한 Test API")
	@GetMapping("/test")
	public String test(UserAuthentication userAuthentication) {
		if (userAuthentication == null) {
			return "userAuthentication 이 빈 값(null)입니다.";
		}
		return userAuthentication.getEmail() + "가 인증되었습니다.";
	}
}
