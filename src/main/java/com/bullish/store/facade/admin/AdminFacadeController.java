package com.bullish.store.facade.admin;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@RequestMapping("/admin")
@RestController
public @interface AdminFacadeController {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
