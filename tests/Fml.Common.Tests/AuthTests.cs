using FML.Common.Auth;

namespace Fml.Common.Tests;

public class AuthTests
{
    [Fact]
    public void Hashing_is_deterministic_and_never_returns_the_password()
    {
        var hash = PasswordHasher.Hash("salt", "s3cret");

        Assert.Equal(hash, PasswordHasher.Hash("salt", "s3cret"));
        Assert.NotEqual("s3cret", hash);
        Assert.Equal(64, hash.Length);
    }

    [Fact]
    public void Hashing_is_salted()
    {
        Assert.NotEqual(
            PasswordHasher.Hash("salt-a", "s3cret"),
            PasswordHasher.Hash("salt-b", "s3cret"));
    }

    [Fact]
    public void Tokens_differ_per_user_and_per_expiry()
    {
        var expires = new DateTime(2026, 1, 1, 0, 0, 0, DateTimeKind.Utc);

        var token = AuthTokenFactory.Issue("salt", 1, "planner", expires);

        Assert.Equal(token, AuthTokenFactory.Issue("salt", 1, "planner", expires));
        Assert.NotEqual(token, AuthTokenFactory.Issue("salt", 2, "planner", expires));
        Assert.NotEqual(token, AuthTokenFactory.Issue("salt", 1, "planner", expires.AddMinutes(1)));
        Assert.NotEqual(token, AuthTokenFactory.Issue("other-salt", 1, "planner", expires));
    }

    [Fact]
    public void User_defaults_to_an_active_viewer()
    {
        var user = new User();

        Assert.True(user.IsActive);
        Assert.Equal(UserRole.Viewer, user.Role);
        Assert.Null(user.LastLoginUtc);
    }
}
