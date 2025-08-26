/* (C) 2025 
Booker App. */
package com.booker_app.backend_service.controllers;

import java.util.ArrayList;
import java.util.List;

import com.booker_app.backend_service.controllers.response.ResponseData;
import com.booker_app.backend_service.services.UserService;
import org.springframework.web.bind.annotation.*;

import static com.booker_app.backend_service.utils.Constants.Endpoints.BASE_URL_V1;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(BASE_URL_V1 + "/user")
public class UserController {

	private final UserService userService;
	private final List<ResponseData> responseData = new ArrayList<>();

	public UserController(UserService userService) {
		this.userService = userService;
	}
}
