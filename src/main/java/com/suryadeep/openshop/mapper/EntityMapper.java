package com.suryadeep.openshop.mapper;


import com.suryadeep.openshop.dto.request.*;
import com.suryadeep.openshop.dto.response.*;
import com.suryadeep.openshop.entity.*;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    // ===================== Product =====================
    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "images", ignore = true) // handled manually
    Product toProductEntity(ProductRequest request);

    @Named("imagesToUrls")
    default List<String> imagesToUrls(List<Image> images) {
        if (images == null) return null;
        return images.stream()
                .map(Image::getImageKey)  // assuming getUrl() exists in Image entity
                .collect(Collectors.toList());
    }
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "images", source = "images", qualifiedByName = "imagesToUrls")
    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponseList(List<Product> products);

    // ===================== Variant =====================
    @Mapping(target = "stock", source = "stockQuantity")
    Variant toVariantEntity(VariantRequest request);

    @Mapping(target = "stockQuantity", source = "stock")
    VariantResponse toVariantResponse(Variant variant);

    List<VariantResponse> toVariantResponseList(List<Variant> variants);

    // ===================== Category =====================
    @Mapping(target = "images",ignore = true)
    Category toCategoryEntity(CategoryRequest request);

    @Mapping(target = "images", source = "images", qualifiedByName = "imagesToUrls")
    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toCategoryResponseList(List<Category> categories);

    // ===================== Address =====================
    Address toAddressEntity(AddressRequest request);
    AddressResponse toAddressResponse(Address address);
    List<AddressResponse> toAddressResponseList(List<Address> addresses);

    // ===================== User =====================
    @Mapping(target = "name",source = "username")
    User toUserEntity(UserRegisterRequest request);
    @Mapping(target = "username",source = "name")
    @Mapping(target = "userId", source = "id")
    UserResponse toUserResponse(User user);

    @Mapping(target = "variant.id", source = "variantId")
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "price", ignore = true)
    CartItem toCartItemEntity(CartItemRequest cartItemRequest);

    @Mapping(target = "variantId", source = "variant.id")
    @Mapping(target = "quantity", source = "quantity")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Mapping(target = "cartItems",source = "cartItems")
    @Mapping(target = "price", source = "price")
    CartResponse toResponse(Cart cart);

    List<CartResponse> toResponseList(List<Cart> carts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(getImageNameFromUrl(url))")
    Image toImageEntity(String url);

    default String getImageNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}