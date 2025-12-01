package it.unicam.cs.ids2425.product.seeder;

import it.unicam.cs.ids2425.product.Category;
import it.unicam.cs.ids2425.product.Product;
import it.unicam.cs.ids2425.product.ProductStatus;
import it.unicam.cs.ids2425.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ProductDataLoader implements CommandLineRunner {
    ProductRepository productRepository;

    public ProductDataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Define the random library of values to generate
        List<String> productName = Arrays.asList(
                "Pane Casereccio",
                "Pane Integrale",
                "Mortadella Bologna IGP",
                "Prosciutto Crudo di Parma",
                "Prosciutto Cotto Alta Qualità",
                "Bovino Scottona - Fettine",
                "Pecorino Stagionato",
                "Mozzarella di Bufala Campana",
                "Olio Extra Vergine di Oliva - 500ml",
                "Chianti Classico - 750ml"
        );

        List<String> productDescription = Arrays.asList(
                "Freshly baked country bread with a crunchy crust and soft, airy crumb.",
                "Wholegrain loaf with mixed seeds, high in fiber and natural flavor.",
                "Classic mortadella with a delicate aroma and small, tender fat cubes.",
                "Dry-cured ham with sweet, nutty notes and a melt-in-the-mouth texture.",
                "Slow-baked cooked ham, tender and lightly seasoned — ideal for sandwiches.",
                "Lean beef slices from young female cattle, suitable for grilling or roasting.",
                "Aged sheep cheese with a sharp, savory profile and crumbly texture.",
                "Creamy buffalo mozzarella with a rich, milky flavor and soft texture.",
                "Cold-pressed extra virgin olive oil with fruity aroma and slight peppery finish.",
                "Medium-bodied red wine with cherry and plum notes, balanced tannins."
        );
        List<Category> productCategory = Arrays.asList(
                Category.BIO,
                Category.IGP,
                Category.DOP,
                Category.KM_ZERO
        );

        int count = 10;

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (int i = 0; i < count; i++) {
            String name = productName.get(rnd.nextInt(productName.size()));
            Double price = (double) (rnd.nextInt(5, 50) * 100) / 100;
            Integer quantity = rnd.nextInt(1, 99);
            String description = productDescription.get(rnd.nextInt(productDescription.size()));
            // Date generator
            LocalDate expireDate = LocalDate.now().plusDays(rnd.nextInt(1,366));
            Category category = productCategory.get(rnd.nextInt(productCategory.size()));

            Product product = new Product(name, price, quantity, description, expireDate, category);
            product.setStatus(ProductStatus.APPROVED);

            productRepository.save(product);
        }

    }
}
