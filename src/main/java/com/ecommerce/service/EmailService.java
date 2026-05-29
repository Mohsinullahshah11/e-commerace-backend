package com.ecommerce.service;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    // Returns false if mail credentials are not configured
    public boolean isMailReady() {
        return mailSender != null
                && mailUsername != null
                && !mailUsername.isEmpty()
                && !mailUsername.startsWith("your-");
    }

    public void sendVerificationEmail(String toEmail, String name, String token) {
        if (!isMailReady()) {
            System.out.println("[Email skipped] Verification email for " + toEmail + " — no mail credentials configured.");
            return;
        }
        String verifyUrl = frontendUrl + "/verify-email?token=" + token;
        String subject = "Verify your ShopEasy account";
        sendHtml(toEmail, subject, buildVerificationHtml(name, verifyUrl));
    }

    public void sendOrderConfirmationEmail(String toEmail, String name, Order order) {
        if (!isMailReady()) {
            System.out.println("[Email skipped] Order confirmation for " + toEmail + " — no mail credentials configured.");
            return;
        }
        String subject = "Order Confirmed — ShopEasy #" + order.getId();
        sendHtml(toEmail, subject, buildOrderConfirmationHtml(name, order));
    }

    private void sendHtml(String to, String subject, String html) {
        // Run in background thread so it never blocks the HTTP response
        new Thread(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(mailUsername, "ShopEasy");
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(html, true);
                mailSender.send(message);
                System.out.println("[Email sent] " + subject + " → " + to);
            } catch (Exception e) {
                System.err.println("[Email failed] " + e.getMessage());
            }
        }).start();
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
                    Thanks for signing up. Click the button below to verify your email address and start shopping.
                  </p>
                  <a href="%s" style="display:inline-block;background:#2563eb;color:white;padding:14px 32px;border-radius:8px;text-decoration:none;font-weight:600;font-size:0.95rem;">
                    Verify My Email
                  </a>
                  <p style="color:#94a3b8;margin:28px 0 0;font-size:0.78rem;line-height:1.5;">
                    If you didn't create a ShopEasy account, you can safely ignore this email.<br>
                    This link expires in 24 hours.
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(name, verifyUrl);
    }

    private String buildOrderConfirmationHtml(String name, Order order) {
        StringBuilder rows = new StringBuilder();
        for (OrderItem item : order.getOrderItems()) {
            rows.append("""
                <tr>
                  <td style="padding:10px 0;border-bottom:1px solid #f1f5f9;color:#1e293b;">%s</td>
                  <td style="padding:10px 0;border-bottom:1px solid #f1f5f9;text-align:center;color:#64748b;">×%d</td>
                  <td style="padding:10px 0;border-bottom:1px solid #f1f5f9;text-align:right;font-weight:600;color:#1e293b;">$%.2f</td>
                </tr>
                """.formatted(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getPriceAtPurchase() * item.getQuantity()
                ));
        }

        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;background:#f8fafc;margin:0;padding:40px 20px;">
              <div style="max-width:560px;margin:0 auto;background:white;border-radius:12px;overflow:hidden;border:1px solid #e2e8f0;">
                <div style="background:#2563eb;padding:28px 32px;">
                  <h1 style="color:white;margin:0;font-size:1.4rem;font-weight:700;">ShopEasy</h1>
                </div>
                <div style="padding:36px 32px;">
                  <div style="background:#f0fdf4;border:1px solid #bbf7d0;border-radius:8px;padding:16px 20px;margin-bottom:28px;">
                    <p style="margin:0;color:#166534;font-weight:600;font-size:1rem;">Your order has been confirmed!</p>
                  </div>
                  <p style="color:#64748b;margin:0 0 24px;">Hi %s, thanks for your purchase. Here's a summary of your order:</p>
                  <div style="background:#f8fafc;border-radius:8px;padding:20px;margin-bottom:20px;">
                    <p style="margin:0 0 4px;font-size:0.8rem;color:#94a3b8;text-transform:uppercase;letter-spacing:0.5px;">Order ID</p>
                    <p style="margin:0;font-weight:700;font-size:1.1rem;color:#1e293b;">#%d</p>
                  </div>
                  <table style="width:100%;border-collapse:collapse;">
                    <thead>
                      <tr>
                        <th style="text-align:left;padding-bottom:8px;color:#94a3b8;font-size:0.78rem;text-transform:uppercase;letter-spacing:0.5px;border-bottom:2px solid #e2e8f0;">Item</th>
                        <th style="text-align:center;padding-bottom:8px;color:#94a3b8;font-size:0.78rem;text-transform:uppercase;letter-spacing:0.5px;border-bottom:2px solid #e2e8f0;">Qty</th>
                        <th style="text-align:right;padding-bottom:8px;color:#94a3b8;font-size:0.78rem;text-transform:uppercase;letter-spacing:0.5px;border-bottom:2px solid #e2e8f0;">Total</th>
                      </tr>
                    </thead>
                    <tbody>
                      %s
                    </tbody>
                  </table>
                  <div style="margin-top:16px;padding-top:16px;border-top:2px solid #e2e8f0;display:flex;justify-content:space-between;">
                    <span style="font-weight:700;font-size:1rem;color:#1e293b;">Order Total</span>
                    <span style="font-weight:700;font-size:1.1rem;color:#2563eb;">$%.2f</span>
                  </div>
                  <p style="color:#94a3b8;margin:32px 0 0;font-size:0.8rem;text-align:center;">
                    Payment processed via ShopEasy Payments · Status: CONFIRMED
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(name, order.getId(), rows.toString(), order.getTotalAmount());
    }
}
