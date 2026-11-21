package SpringBootRest.controllers;

import SpringBootRest.entities.User;
import SpringBootRest.exception.UserNotFoundException;
import SpringBootRest.repositories.UserRepository;
import SpringBootRest.services.SaleService;
import SpringBootRest.services.WebpayService;
import SpringBootRest.dto.SaleDTO;
import SpringBootRest.mappers.SaleMapper;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final WebpayService webpayService;
    private final SaleService saleService;
    private final UserRepository userRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @PostMapping("/initiate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> initiatePayment(
            Authentication authentication
    ) {
        try {
            User user = getUser(authentication);

            String returnUrl = frontendUrl + "/payment/confirm";

            WebpayPlusTransactionCreateResponse response =
                    webpayService.initiatePayment(user, returnUrl);

            return ResponseEntity.ok(Map.of(
                    "url", response.getUrl(),
                    "token", response.getToken()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @RequestParam("token_ws") String token,
            Authentication authentication
    ) {
        try {
            WebpayPlusTransactionCommitResponse response =
                    webpayService.confirmPayment(token);

            // Verificar que el pago fue aprobado
            if (response.getResponseCode() != 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", "Pago rechazado",
                                "code", response.getResponseCode()
                        ));
            }

            // Registrar la venta
            User user = getUser(authentication);
            SaleDTO sale = SaleMapper.toDTO(
                    saleService.checkout(user)
            );

            return ResponseEntity.ok(Map.of(
                    "sale", sale,
                    "authorizationCode", response.getAuthorizationCode(),
                    "cardNumber", response.getCardDetail().getCardNumber()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }
}
