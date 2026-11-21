package SpringBootRest.services;

import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;
import cl.transbank.common.IntegrationCommerceCodes;
import cl.transbank.common.IntegrationApiKeys;
import cl.transbank.common.IntegrationType;
import SpringBootRest.entities.Cart;
import SpringBootRest.entities.User;
import SpringBootRest.repositories.CartRepository;
import SpringBootRest.exception.CartNotFoundException;
import SpringBootRest.exception.CartEmptyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WebpayService {

    private final CartRepository cartRepository;

    public WebpayPlusTransactionCreateResponse initiatePayment(
            User user,
            String returnUrl
    ) throws Exception {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String buyOrder = "ORDER-" + System.currentTimeMillis();
        String sessionId = "SESSION-" + user.getId();

        WebpayPlus.Transaction tx = new WebpayPlus.Transaction(
                new cl.transbank.webpay.common.WebpayOptions(
                        IntegrationCommerceCodes.WEBPAY_PLUS,
                        IntegrationApiKeys.WEBPAY,
                        IntegrationType.TEST
                )
        );

        return tx.create(buyOrder, sessionId, total.longValue(), returnUrl);
    }

    public WebpayPlusTransactionCommitResponse confirmPayment(
            String token
    ) throws Exception {

        WebpayPlus.Transaction tx = new WebpayPlus.Transaction(
                new cl.transbank.webpay.common.WebpayOptions(
                        IntegrationCommerceCodes.WEBPAY_PLUS,
                        IntegrationApiKeys.WEBPAY,
                        IntegrationType.TEST
                )
        );

        return tx.commit(token);
    }
}
