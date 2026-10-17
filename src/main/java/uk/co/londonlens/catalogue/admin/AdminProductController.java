
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

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import uk.co.londonlens.catalogue.Product;
import uk.co.londonlens.catalogue.ProductAdminService;
import uk.co.londonlens.catalogue.ProductForm;
import uk.co.londonlens.catalogue.ProductRepository;

import java.util.stream.Collectors;
import uk.co.londonlens.catalogue.Category;
import uk.co.londonlens.catalogue.CategoryRepository;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;
    private final ProductAdminService productAdminService;
    private final CategoryRepository categoryRepository;

    public AdminProductController(
            ProductRepository productRepository,
            ProductAdminService productAdminService,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productAdminService = productAdminService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        String normalizedSearch = search == null ? "" : search.trim();

        List<String> validStatuses =
            List.of("DRAFT", "ACTIVE", "ARCHIVED");

        String normalizedStatus = status == null
            ? ""
            : status.trim().toUpperCase(Locale.ROOT);

        if (!validStatuses.contains(normalizedStatus)) {
            normalizedStatus = "";
        }

        int pageSize = 10;
        int pageIndex = Math.max(0, page);

        PageRequest pageRequest = PageRequest.of(
            pageIndex,
            pageSize,
            Sort.by(Sort.Direction.ASC, "name")
        );

        Page<uk.co.londonlens.catalogue.Product> productPage =
            productRepository.searchForAdmin(
                normalizedSearch,
                normalizedStatus,
                pageRequest
            );

        // Keep the requested page within the available result range.
        if (productPage.getTotalPages() > 0
                && pageIndex >= productPage.getTotalPages()) {

            pageIndex = productPage.getTotalPages() - 1;

            pageRequest = PageRequest.of(
                pageIndex,
                pageSize,
                Sort.by(Sort.Direction.ASC, "name")
            );

            productPage = productRepository.searchForAdmin(
                normalizedSearch,
                normalizedStatus,
                pageRequest
            );
        }

        model.addAttribute("products", productPage);
        model.addAttribute("currentSearch", normalizedSearch);
        model.addAttribute("currentStatus", normalizedStatus);
        model.addAttribute("statuses", validStatuses);

        model.addAttribute(
            "categoryNames",
            categoryRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(
                    uk.co.londonlens.catalogue.Category::getId,
                    uk.co.londonlens.catalogue.Category::getName
                ))
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
                    result.rejectValue(
                        "categoryId",
                        "invalid",
                        "Choose an existing active category."
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
                result.rejectValue(
                    "categoryId",
                    "invalid",
                    "Choose an existing active category."
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
        model.addAttribute(
            "categories",
            categoryRepository.findByActiveTrueOrderByNameAsc()
        );
    }

    @PostMapping("/{id}/publish")
    public String publishProduct(
            @PathVariable Long id,
            RedirectAttributes redirect) {

        try {
            productAdminService.publish(id);
            redirect.addFlashAttribute(
                "successMessage",
                "Product published successfully."
            );
        } catch (ProductAdminService.InvalidProductStateException ex) {
            redirect.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/archive")
    public String archiveProduct(
            @PathVariable Long id,
            RedirectAttributes redirect) {

        productAdminService.archive(id);

        redirect.addFlashAttribute(
            "successMessage",
            "Product archived successfully."
        );

        return "redirect:/admin/products";
    }
}
