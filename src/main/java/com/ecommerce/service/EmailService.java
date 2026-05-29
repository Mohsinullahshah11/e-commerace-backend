package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${brevo.sender.email:noreply@shopeasy.com}")
    private String senderEmail;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public boolean isMailReady() {
        return brevoApiKey != null && !brevoApiKey.isEmpty();
    }

    public void sendVerificationEmail(String toEmail, String name, String token) {
        if (!isMailReady()) {
            System.out.println("[Email skipped] No BREVO_API_KEY configured — auto-verify instead.");
            return;
        }
        String verifyUrl = frontendUrl + "/verify-email?token=" + token;
        sendAsync(toEmail, name, "Verify your ShopEasy account", buildVerificationHtml(name, verifyUrl));
    }

    public void sendOrderConfirmationEmail(String toEmail, String name, Order order) {
        if (!isMailReady()) {
            System.out.println("[Email skipped] No BREVO_API_KEY configured.");
            return;
        }
        sendAsync(toEmail, name, "Order Confirmed — ShopEasy #" + order.getId(), buildOrderHtml(name, order));
    }

    private void sendAsync(String toEmail, String toName, String subject, String html) {
        new Thread(() -> {
            try {
                String body = """
                    {
                      "sender": {"name": "ShopEasy", "email": "%s"},
                      "to": [{"email": "%s", "name": "%s"}],
                      "subject": "%s",
                      "htmlContent": %s
                    }
                    """.formatted(
                        senderEmail,
                        toEmail,
                        toName.replace("\"", "\\\""),
                        subject,
                        toJson(html)
                    );

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                        .header("Content-Type", "application/json")
                        .header("api-key", brevoApiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 201) {
                    System.out.println("[Email sent] " + subject + " → " + toEmail);
                } else {
                    System.err.println("[Email failed] Status " + response.statusCode() + ": " + response.body());
                }
            } catch (Exception e) {
                System.err.println("[Email failed] " + e.getMessage());
            }
        }).start();
    }

    // Escape HTML into a valid JSON string value
    private String toJson(String html) {
        return "\"" + html
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "\\t")
                + "\"";
    }

    private String buildVerificationHtml(String name, String verifyUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;background:#f8fafc;margin:0;padding:40px 20px;">
              <div style="max-width:520px;margin:0 auto;background:white;border-radius:12px;overflow:hidden;border:1px solid #e2e8f0;">
                <div style="background:#2563eb;padding:28px 32px;">
                  <h1 style="color:white;margin:0;font-size:1.4rem;font-weight:700;">ShopEasy</h1>
                </div>
                <div style="padding:40px 32px;">
                  <h2 style="color:#1e293b;margin:0 0 8px;font-size:1.3rem;">Hello, %s!</h2>
                  <p style="color:#64748b;margin:0 0 28px;line-height:1.6;">
                    Thanks for signing up. Click the button below to verify your email and start shopping.
                  </p>
                  <a href="%s" style="display:inline-block;background:#2563eb;color:white;padding:14px 32px;border-radius:8px;text-decoration:none;font-weight:600;font-size:0.95rem;">
                    Verify My Email
                  </a>
                  <p style="color:#94a3b8;margin:28px 0 0;font-size:0.78rem;line-height:1.5;">
                    If you didn't create a ShopEasy account, ignore this email.
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(name, verifyUrl);
    }

    private String buildOrderHtml(String name, Order order) {
        StringBuilder rows = new StringBuilder();
        for (OrderItem item : order.getOrderItems()) {
            rows.append("<tr>")
                .append("<td style='padding:10px 0;border-bottom:1px solid #f1f5f9;'>").append(item.getProduct().getName()).append("</td>")
                .append("<td style='padding:10px 0;border-bottom:1px solid #f1f5f9;text-align:center;'>x").append(item.getQuantity()).append("</td>")
                .append("<td style='padding:10px 0;border-bottom:1px solid #f1f5f9;text-align:right;font-weight:600;'>$")
                .append(String.format("%.2f", item.getPriceAtPurchase() * item.getQuantity())).append("</td>")
                .append("</tr>");
        }

        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;background:#f8fafc;margin:0;padding:40px 20px;">
              <div style="max-width:560px;margin:0 auto;background:white;border-radius:12px;border:1px solid #e2e8f0;">
                <div style="background:#2563eb;padding:28px 32px;">
                  <h1 style="color:white;margin:0;font-size:1.4rem;font-weight:700;">ShopEasy</h1>
                </div>
                <div style="padding:36px 32px;">
                  <p style="color:#64748b;margin:0 0 24px;">Hi %s, your order is confirmed!</p>
                  <p style="margin:0 0 4px;font-size:0.8rem;color:#94a3b8;">ORDER ID</p>
                  <p style="margin:0 0 24px;font-weight:700;font-size:1.1rem;">#%d</p>
                  <table style="width:100%%;border-collapse:collapse;">
                    <tbody>%s</tbody>
                  </table>
                  <div style="margin-top:16px;padding-top:16px;border-top:2px solid #e2e8f0;display:flex;justify-content:space-between;">
                    <span style="font-weight:700;">Total</span>
                    <span style="font-weight:700;color:#2563eb;">$%.2f</span>
                  </div>
                </div>
              </div>
            </body>
            </html>
            """.formatted(name, order.getId(), rows.toString(), order.getTotalAmount());
    }
}
