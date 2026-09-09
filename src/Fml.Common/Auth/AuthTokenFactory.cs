using System.Security.Cryptography;
using System.Text;

namespace FML.Common.Auth;

/// <summary>Issues the opaque session token returned by an FML login.</summary>
public static class AuthTokenFactory
{
    public static string Issue(string salt, int userId, string username, DateTime expiresUtc) =>
        Convert.ToHexString(SHA256.HashData(
            Encoding.UTF8.GetBytes($"{userId}:{username}:{expiresUtc:O}:{salt}")));
}
