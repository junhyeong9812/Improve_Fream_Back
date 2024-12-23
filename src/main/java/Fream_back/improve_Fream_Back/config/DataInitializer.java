package Fream_back.improve_Fream_Back.config;

import Fream_back.improve_Fream_Back.faq.entity.*;
import Fream_back.improve_Fream_Back.faq.repository.FAQRepository;
import Fream_back.improve_Fream_Back.inspection.entity.*;
import Fream_back.improve_Fream_Back.inspection.repository.InspectionStandardRepository;
import Fream_back.improve_Fream_Back.notice.entity.*;
import Fream_back.improve_Fream_Back.notice.repository.NoticeRepository;
import Fream_back.improve_Fream_Back.notification.entity.*;
import Fream_back.improve_Fream_Back.notification.repository.NotificationRepository;
import Fream_back.improve_Fream_Back.product.entity.*;
import Fream_back.improve_Fream_Back.product.entity.enumType.*;
import Fream_back.improve_Fream_Back.product.repository.*;
import Fream_back.improve_Fream_Back.user.entity.*;
import Fream_back.improve_Fream_Back.user.repository.ProfileRepository;
import Fream_back.improve_Fream_Back.user.repository.UserRepository;
import Fream_back.improve_Fream_Back.user.service.profile.ProfileCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProfileCommandService profileCommandService;
    private final PasswordEncoder passwordEncoder;
    private final NoticeRepository noticeRepository;
    private final FAQRepository faqRepository;
    private final InspectionStandardRepository inspectionStandardRepository;
    private final NotificationRepository notificationRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductColorRepository productColorRepository;
    private final ProductSizeRepository productSizeRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public void run(String... args) {
        // 사용자 생성
        User user1 = createUserWithProfile("user1@example.com", "password123", "010-1234-5678", ShoeSize.SIZE_270, Role.USER, 25, Gender.MALE);
        User user2 = createUserWithProfile("user2@example.com", "password456", "010-9876-5432", ShoeSize.SIZE_280, Role.USER, 30, Gender.FEMALE);
        User admin = createUserWithProfile("admin@example.com", "adminpassword", "010-0000-0000", null, Role.ADMIN, 35, Gender.MALE);

        // 공지사항 데이터 생성
        for (NoticeCategory category : NoticeCategory.values()) {
            for (int i = 1; i <= 10; i++) {
                noticeRepository.save(
                        Notice.builder()
                                .title(category + " 공지사항 제목 " + i)
                                .content("<h1>" + category + " 공지사항 내용 " + i + "</h1>")
                                .category(category)
                                .build()
                );
            }
        }

        // FAQ 데이터 생성
        for (FAQCategory category : FAQCategory.values()) {
            for (int i = 1; i <= 10; i++) {
                faqRepository.save(
                        FAQ.builder()
                                .question(category + " 질문 " + i)
                                .answer("<p>" + category + " 답변 내용 " + i + "</p>")
                                .category(category)
                                .build()
                );
            }
        }

        // 검수 기준 데이터 생성
        for (InspectionCategory category : InspectionCategory.values()) {
            inspectionStandardRepository.save(
                    InspectionStandard.builder()
                            .category(category)
                            .content("<ul><li>" + category + " 검수 기준 내용</li></ul>")
                            .build()
            );
        }

        // 알림 데이터 생성
        for (User user : new User[]{user1, user2, admin}) {
            for (NotificationType type : NotificationType.values()) {
                if (type.getCategory() == NotificationCategory.SHOPPING) {
                    notificationRepository.save(
                            Notification.builder()
                                    .user(user)
                                    .category(type.getCategory())
                                    .type(type)
                                    .message(user.getEmail() + "의 쇼핑 알림: " + type.name())
                                    .isRead(false)
                                    .build()
                    );
                }
            }
        }

        // 상품 데이터 생성
        createProductData();

        System.out.println("초기 데이터가 성공적으로 생성되었습니다.");
    }

    private User createUserWithProfile(String email, String password, String phoneNumber, ShoeSize shoeSize, Role role, Integer age, Gender gender) {
        if (userRepository.existsByEmail(email)) {
            return userRepository.findByEmail(email).orElseThrow();
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .referralCode(generateReferralCode())
                .shoeSize(shoeSize)
                .termsAgreement(true)
                .phoneNotificationConsent(true)
                .emailNotificationConsent(true)
                .optionalPrivacyAgreement(true)
                .role(role)
                .age(age)
                .gender(gender)
                .build();

        User savedUser = userRepository.save(user);
        profileCommandService.createDefaultProfile(savedUser);
        return savedUser;
    }

    private String generateReferralCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void createProductData() {
        // 브랜드 생성
        Brand nike = brandRepository.save(Brand.builder().name("Nike").build());
        Brand adidas = brandRepository.save(Brand.builder().name("Adidas").build());
        Brand jordan = brandRepository.save(Brand.builder().name("Jordan").build());
        Brand stussy = brandRepository.save(Brand.builder().name("Stussy").build());
        Brand iabStudio = brandRepository.save(Brand.builder().name("IAB Studio").build());
        Brand newJeans = brandRepository.save(Brand.builder().name("NewJeans").build());

        // 카테고리 생성
        Category clothing = categoryRepository.save(Category.builder().name("Clothing").build());
        Category tops = categoryRepository.save(Category.builder().name("Tops").parentCategory(clothing).build());
        Category tshirts = categoryRepository.save(Category.builder().name("Short Sleeve T-Shirts").parentCategory(tops).build());

        Category shoes = categoryRepository.save(Category.builder().name("Shoes").build());
        Category sneakers = categoryRepository.save(Category.builder().name("Sneakers").parentCategory(shoes).build());

        // 상품 생성
        createProductsForCategory("Sneakers", sneakers, List.of(nike, adidas, jordan), SizeType.SHOES);
        createProductsForCategory("Short Sleeve T-Shirts", tshirts, List.of(stussy, iabStudio, newJeans), SizeType.CLOTHING);
    }

    private void createProductsForCategory(String categoryName, Category category, List<Brand> brands, SizeType sizeType) {
        List<ColorType> colors = List.of(ColorType.values()); // 전체 색상 목록
        int colorCount = colors.size();

        for (int i = 1; i <= 10; i++) {
            Product product = productRepository.save(
                    Product.builder()
                            .name(categoryName + " Product " + i)
                            .englishName(categoryName + " English Product " + i)
                            .releasePrice(100 + i * 50)
                            .modelNumber("Model-" + i)
                            .releaseDate("2023-01-" + (i < 10 ? "0" + i : i))
                            .gender(GenderType.values()[new Random().nextInt(GenderType.values().length)])
                            .brand(brands.get(i % brands.size()))
                            .category(category)
                            .build()
            );

            for (int j = 0; j < 3; j++) { // 색상 3개 고정
                // 색상 순환으로 선택
                ColorType color = colors.get((i * 3 + j) % colorCount);

                ProductColor productColor = productColorRepository.save(
                        ProductColor.builder()
                                .colorName(color.getDisplayName())
                                .product(product)
                                .build()
                );

                // 이미지 URL 생성
                String imageName = "thumbnail_" + product.getId() + "_" + color.name().toLowerCase() + ".jpg";
                productColor.addThumbnailImage(
                        productImageRepository.save(
                                ProductImage.builder()
                                        .imageUrl("/api/products/" + product.getId() + "/images?imageName=" + imageName)
                                        .productColor(productColor)
                                        .build()
                        )
                );

                for (String size : sizeType.getSizes()) {
                    productSizeRepository.save(
                            ProductSize.builder()
                                    .size(size)
                                    .sizeType(sizeType)
                                    .purchasePrice(product.getReleasePrice())
                                    .salePrice(product.getReleasePrice() + 20)
                                    .quantity(10)
                                    .productColor(productColor)
                                    .build()
                    );
                }
            }
        }
    }
}
