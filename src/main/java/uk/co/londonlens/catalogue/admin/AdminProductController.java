
package uk.co.londonlens.catalogue.admin;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.co.londonlens.catalogue.Product;
import uk.co.londonlens.catalogue.ProductAdminService;
import uk.co.londonlens.catalogue.ProductForm;
import uk.co.londonlens.catalogue.ProductRepository;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;
    private final ProductAdminService productAdminService;

    public AdminProductController(
            ProductRepository productRepository,
            ProductAdminService productAdminService) {
        this.productRepository = productRepository;
        this.productAdminService = productAdminService;
    }

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute(
            "products",
            productRepository.findAll(
                Sort.by(Sort.Direction.ASC, "name")
            )
        );

        return "admin/products";
    }

    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        prepareForm(model, "Create product", "/admin/products");

        return "admin/product-form";
    }

    @PostMapping
    public String createProduct(
            @Valid @ModelAttribute("productForm") ProductForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        if (!result.hasErrors()) {
            try {
                productAdminService.create(form);
            } catch (ProductAdminService.DuplicateSlugException ex) {
                result.rejectValue(
                    "slug",
                    "duplicate",
                    "That slug is already in use."
                );
            }
        }

        if (result.hasErrors()) {
            prepareForm(model, "Create product", "/admin/products");
            return "admin/product-form";
        }

        redirect.addFlashAttribute(
            "successMessage", "Product created successfully."
        );

        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(
            @PathVariable Long id,
            Model model) {

        Product product = productAdminService.findById(id);

        model.addAttribute("productForm", ProductForm.from(product));
        prepareForm(
            model,
            "Edit product",
            "/admin/products/" + id + "/edit"
        );

        return "admin/product-form";
    }

    @PostMapping("/{id}/edit")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("productForm") ProductForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {

        if (!result.hasErrors()) {
            try {
                productAdminService.update(id, form);
            } catch (ProductAdminService.DuplicateSlugException ex) {
                result.rejectValue(
                    "slug",
                    "duplicate",
                    "That slug is already in use."
                );
            }
        }

        if (result.hasErrors()) {
            prepareForm(
                model,
                "Edit product",
                "/admin/products/" + id + "/edit"
            );
            return "admin/product-form";
        }

        redirect.addFlashAttribute(
            "successMessage", "Product updated successfully."
        );

        return "redirect:/admin/products";
    }

    private void prepareForm(
            Model model, String title, String action) {

        model.addAttribute("pageTitle", title);
        model.addAttribute("formAction", action);
        model.addAttribute(
            "productTypes",
            List.of("DIGITAL", "PHYSICAL", "SERVICE", "MEMBERSHIP")
        );
        model.addAttribute(
            "statuses",
            List.of("DRAFT", "ACTIVE", "ARCHIVED")
        );
    }
}
