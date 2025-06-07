package controller.common;
import java.util.List;
import java.io.*;
import java.nio.charset.StandardCharsets; // Quan trọng để đảm bảo encoding nhất quán
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64; // Để mã hóa/giải mã đơn giản (KHÔNG PHẢI MÃ HÓA BẢO MẬT)

public class RememberPassword {
    private static final String FILE_PATH = "remember_creds.txt"; // Đổi tên file cho rõ ràng hơn

    /**
     * Lưu tên đăng nhập và mật khẩu.
     * Mật khẩu được mã hóa Base64 đơn giản (CHỈ ĐỂ CHE GIẤU, KHÔNG PHẢI BẢO MẬT).
     * @param username Tên đăng nhập
     * @param password Mật khẩu (plain text)
     */
    public static void saveCredentials(String username, String password) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH), StandardCharsets.UTF_8)) {
            writer.write(username);
            writer.newLine(); // Xuống dòng
            // Mã hóa Base64 đơn giản - KHÔNG AN TOÀN CHO MẬT KHẨU THỰC SỰ
            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
            writer.write(encodedPassword);
            System.out.println("RememberPassword: Đã lưu credentials (username + encoded password).");
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu credentials: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tải tên đăng nhập và mật khẩu đã lưu.
     * Mật khẩu được giải mã Base64.
     * @return một mảng String với [0] = username, [1] = password. Trả về null nếu không có gì được lưu hoặc lỗi.
     */
    public static String[] loadCredentials() {
        try {
            if (Files.exists(Paths.get(FILE_PATH))) {
                List<String> lines = Files.readAllLines(Paths.get(FILE_PATH), StandardCharsets.UTF_8);
                if (lines.size() >= 2) {
                    String username = lines.get(0).trim();
                    // Giải mã Base64
                    String encodedPassword = lines.get(1).trim();
                    byte[] decodedBytes = Base64.getDecoder().decode(encodedPassword);
                    String password = new String(decodedBytes, StandardCharsets.UTF_8);
                    System.out.println("RememberPassword: Đã tải credentials.");
                    return new String[]{username, password};
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi tải credentials: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // Lỗi nếu chuỗi không phải là Base64 hợp lệ (ví dụ file bị sửa đổi)
            System.err.println("Lỗi giải mã Base64 (file có thể bị hỏng): " + e.getMessage());
            clearCredentials(); // Xóa file bị hỏng
        }
        System.out.println("RememberPassword: Không tìm thấy credentials đã lưu.");
        return null; // Hoặc một mảng rỗng tùy bạn muốn xử lý
    }

    /**
     * Xóa file lưu thông tin đăng nhập.
     */
    public static void clearCredentials() {
        try {
            Files.deleteIfExists(Paths.get(FILE_PATH));
            System.out.println("RememberPassword: Đã xóa file credentials.");
        } catch (IOException e) {
            System.err.println("Lỗi khi xóa file credentials: " + e.getMessage());
            e.printStackTrace();
        }
    }
}