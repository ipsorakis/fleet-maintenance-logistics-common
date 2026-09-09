using System.Security.Cryptography;
using System.Text;

namespace FML.Common.Auth;

/// <summary>Salted SHA-256 password hashing used by every FML service.</summary>
public static class PasswordHasher
{
    public static string Hash(string salt, string password) =>
        Convert.ToHexString(SHA256.HashData(Encoding.UTF8.GetBytes(salt + password)));
}
