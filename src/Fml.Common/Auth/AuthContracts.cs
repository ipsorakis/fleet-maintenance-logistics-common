namespace FML.Common.Auth;

public record LoginRequest(string Username, string Password);

public record LoginResponse(string Token, string Username, UserRole Role, DateTime ExpiresUtc);

public record CreateUserRequest(string Username, string Email, string Password, UserRole Role);
