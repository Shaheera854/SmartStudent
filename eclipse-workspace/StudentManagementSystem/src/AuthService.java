public class AuthService {
    // For now static username/password; in bonus you can validate from DB
    public static boolean login(String username, String password) {
        return "admin".equals(username) && "admin123".equals(password);
    }
}
