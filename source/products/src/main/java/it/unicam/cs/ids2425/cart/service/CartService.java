package it.unicam.cs.ids2425.cart.service;

import it.unicam.cs.ids2425.cart.Cart;
import it.unicam.cs.ids2425.cart.CartItem;
import it.unicam.cs.ids2425.cart.repository.CartItemRepository;
import it.unicam.cs.ids2425.cart.repository.CartRepository;
import it.unicam.cs.ids2425.product.Product;
import it.unicam.cs.ids2425.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CartService implements CartOperation {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;


    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Cart createCart() {
        Cart c = new Cart();
        return cartRepository.save(c);
    }

    @Override
    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId).orElseGet(this::createCart);
    }

    @Override
    public void addProductToCart(Long cartId, UUID productUUID, int quantity) {
        Cart cart = getCart(cartId);
        cart.setCurrencyCode("EUR");
        Product product = productRepository.findById(productUUID)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productUUID));

        Optional<CartItem> existing = cartItemRepository.findByParentCartAndCartItemProduct(cart, product);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setProductQuantity(item.getProductQuantity() + quantity);
            item.setProductPrice(product.getPrice() * item.getProductQuantity());
            cartItemRepository.save(item);
        } else {
            double price = product.getPrice();
            CartItem newItem = new CartItem(cart, product, quantity, price * quantity);
            cart.addItem(newItem);
            // saving cartItem explicitly ensures id generation
            cartItemRepository.save(newItem);
        }
        cart.updateTotal();
    }

    @Override
    public void incrementItem(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId).orElseThrow();

        item.setProductQuantity(item.getProductQuantity() + 1);

        Cart cart = item.getParentCart();
        cart.updateTotal();

        cartRepository.save(cart);
    }

    @Override
    public void decrementItem(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId).orElseThrow();

        if (item.getProductQuantity() > 1) {
            item.setProductQuantity(item.getProductQuantity() - 1);
        }

        Cart cart = item.getParentCart();
        cart.updateTotal();

        cartRepository.save(cart);
    }

    @Override
    public void removeItem(UUID productUUID) {
        CartItem cartItem = cartItemRepository.findByCartItemProductId(productUUID);
        Cart cart = cartItem.getParentCart();
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);

        cart.updateTotal();
        cartRepository.save(cart);
    }
}
