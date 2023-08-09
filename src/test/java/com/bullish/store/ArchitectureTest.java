package com.bullish.store;

import com.bullish.store.facade.admin.AdminFacadeController;
import com.bullish.store.facade.customer.CustomerFacadeController;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

public class ArchitectureTest {
    private final JavaClasses prodClasses = new ClassFileImporter()
        .withImportOption(new ImportOption.DoNotIncludeTests())
        .importPackages("com.bullish.store");

    @Test
    public void checkControllerUnderAdminFacadeShouldStartWithAdminPrefix() {
        ArchRuleDefinition
            .classes().that()
            .resideInAnyPackage("com.bullish.store.facade.admin..")
            .and().haveNameMatching("^.+Controller$")
            .and().doNotHaveSimpleName("AdminFacadeController")
            .and().arePublic()
            .should().beAnnotatedWith(AdminFacadeController.class)
            .andShould().notBeAnnotatedWith(RequestMapping.class)
            .because("Controller in admin facade should start with /admin prefix")
            .check(prodClasses);
    }

    @Disabled
    @Test
    public void checkControllerUnderCustomerFacadeShouldStartWithCustomerPrefix() {
        ArchRuleDefinition
            .classes().that()
            .resideInAnyPackage("com.bullish.store.facade.customer..")
            .and().haveNameMatching("^.+Controller$")
            .and().doNotHaveSimpleName("CustomerFacadeController")
            .and().arePublic()
            .should().beAnnotatedWith(CustomerFacadeController.class)
            .andShould().notBeAnnotatedWith(RequestMapping.class)
            .because("Controller in customer facade should start with /customer prefix")
            .check(prodClasses);
    }

}
