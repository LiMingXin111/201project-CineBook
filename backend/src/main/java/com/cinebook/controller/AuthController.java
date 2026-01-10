package com.cinebook.controller;

import com.cinebook.server.Request;
import com.cinebook.server.Response;
import com.cinebook.model.User;
import com.cinebook.service.UserService;
import com.cinebook.util.JsonUtil;
import com.cinebook.util.PasswordUtil;

public class AuthController {
    private final UserService userService = new UserService();

    public Response handleRequest(Request request) {
        if ("POST".equals(request.getMethod()) && "/api/auth/login".equals(request.getPath())) {
            return login(request);
        }
        if ("POST".equals(request.getMethod()) && "/api/auth/register".equals(request.getPath())) {
            return register(request);
        }
        return new Response(404, "text/plain", "Not Found");
    }

    private Response login(Request request) {
        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            return badRequest("Invalid request", "Request body is required");
        }

        LoginRequest loginRequest;
        try {
            loginRequest = JsonUtil.fromJson(request.getBody(), LoginRequest.class);
        } catch (Exception e) {
            return badRequest("Invalid request", "Malformed JSON");
        }

        if (loginRequest == null || isBlank(loginRequest.username) || isBlank(loginRequest.password)) {
            return badRequest("Invalid request", "Username and password are required");
        }

        User user = userService.getByUsername(loginRequest.username);
        if (user == null || !PasswordUtil.verify(loginRequest.password, user.getPassword())) {
            return new Response(401, "application/json",
                JsonUtil.toJson(new ErrorResponse("Unauthorized", "Invalid credentials")));
        }

        if (!isBlank(loginRequest.role) && !loginRequest.role.equals(user.getRole())) {
            return new Response(403, "application/json",
                JsonUtil.toJson(new ErrorResponse("Forbidden", "Role mismatch")));
        }

        return new Response(200, "application/json", JsonUtil.toJson(new UserResponse(user)));
    }

    private Response register(Request request) {
        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            return badRequest("Invalid request", "Request body is required");
        }

        RegisterRequest registerRequest;
        try {
            registerRequest = JsonUtil.fromJson(request.getBody(), RegisterRequest.class);
        } catch (Exception e) {
            return badRequest("Invalid request", "Malformed JSON");
        }

        if (registerRequest == null || isBlank(registerRequest.username)
            || isBlank(registerRequest.password) || isBlank(registerRequest.email)) {
            return badRequest("Invalid request", "Username, password and email are required");
        }

        if (userService.usernameExists(registerRequest.username)) {
            return badRequest("Invalid request", "Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.username);
        user.setPassword(PasswordUtil.hash(registerRequest.password));
        user.setEmail(registerRequest.email);
        user.setRole("user");
        User created = userService.addUser(user);

        return new Response(201, "application/json", JsonUtil.toJson(new UserResponse(created)));
    }

    private Response badRequest(String error, String message) {
        return new Response(400, "application/json",
            JsonUtil.toJson(new ErrorResponse(error, message)));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class LoginRequest {
        public String username;
        public String password;
        public String role;
    }

    private static class RegisterRequest {
        public String username;
        public String password;
        public String email;
    }

    private static class UserResponse {
        public int id;
        public String username;
        public String email;
        public String role;

        public UserResponse(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.role = user.getRole();
        }
    }

    private static class ErrorResponse {
        public String error;
        public String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}
