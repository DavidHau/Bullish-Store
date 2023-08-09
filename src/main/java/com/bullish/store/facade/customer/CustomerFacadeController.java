package com.bullish.store.facade.customer;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@RequestMapping("/customer")
@RestController
public @interface CustomerFacadeController {
    @AliasFor(annotation = Component.class)
    String value() default "";
}