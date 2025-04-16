package com.suryadeep.openshop.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(String message, Throwable cause) {
      super(message, cause);
    }

    public CategoryNotFoundException(Throwable cause) {
      super(cause);
    }

    public CategoryNotFoundException() {
      super("Category not found");
    }
}
