package SpringBootRest.services;

import SpringBootRest.entities.Sale;
import SpringBootRest.entities.SaleDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendPurchaseConfirmation(Sale sale) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(sale.getUser().getEmail());
            helper.setSubject("✅ Confirmación de compra #" + String.format("%05d", sale.getId()));
            helper.setText(buildEmailBody(sale), true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error enviando email: " + e.getMessage());
        }
    }

    private String buildEmailBody(Sale sale) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
        sb.append("<h2 style='color: #1976d2;'>¡Gracias por tu compra!</h2>");
        sb.append("<p>Hola <strong>").append(sale.getUser().getName()).append("</strong>,</p>");
        sb.append("<p>Tu compra ha sido procesada correctamente.</p>");
        sb.append("<h3>Detalle de tu pedido #").append(String.format("%05d", sale.getId())).append("</h3>");
        sb.append("<table style='width:100%; border-collapse: collapse;'>");
        sb.append("<tr style='background:#f5f5f5;'>");
        sb.append("<th style='padding:8px; text-align:left; border:1px solid #ddd;'>Producto</th>");
        sb.append("<th style='padding:8px; text-align:center; border:1px solid #ddd;'>Cantidad</th>");
        sb.append("<th style='padding:8px; text-align:right; border:1px solid #ddd;'>Precio</th>");
        sb.append("<th style='padding:8px; text-align:right; border:1px solid #ddd;'>Subtotal</th>");
        sb.append("</tr>");
        for (SaleDetail detail : sale.getDetails()) {
            BigDecimal subtotal = detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));
            sb.append("<tr>");
            sb.append("<td style='padding:8px; border:1px solid #ddd;'>").append(detail.getProduct().getName()).append("</td>");
            sb.append("<td style='padding:8px; text-align:center; border:1px solid #ddd;'>").append(detail.getQuantity()).append("</td>");
            sb.append("<td style='padding:8px; text-align:right; border:1px solid #ddd;'>$").append(String.format("%,.0f", detail.getPrice())).append("</td>");
            sb.append("<td style='padding:8px; text-align:right; border:1px solid #ddd;'>$").append(String.format("%,.0f", subtotal)).append("</td>");
            sb.append("</tr>");
        }
        sb.append("<tr style='background:#f5f5f5; font-weight:bold;'>");
        sb.append("<td colspan='3' style='padding:8px; text-align:right; border:1px solid #ddd;'>Total</td>");
        sb.append("<td style='padding:8px; text-align:right; border:1px solid #ddd;'>$").append(String.format("%,.0f", sale.getTotal())).append("</td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<br><p style='color:#666;'>Puedes ver el detalle de tu compra en <a href='https://incifox.org/sales'>Mis compras</a>.</p>");
        sb.append("<p style='color:#999; font-size:12px;'>Shop Project — incifox.org</p>");
        sb.append("</div>");
        return sb.toString();
    }
}
