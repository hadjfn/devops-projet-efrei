package faria.sasikumar.sylla.myfss.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "faria.sasikumar.sylla.myfss", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
    @ArchTest
    static final ArchRule controllersUseServices = noClasses().that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().resideInAnyPackage("..repository..", "..client..");

    @ArchTest
    static final ArchRule persistenceDoesNotKnowWebOrServices = noClasses().that()
            .resideInAnyPackage("..model..", "..repository..")
            .should().dependOnClassesThat().resideInAnyPackage("..controller..", "..service..", "..client..");

    @ArchTest
    static final ArchRule servicesDoNotKnowWeb = noClasses().that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAnyPackage("..controller..", "org.springframework.web..");

    @ArchTest
    static final ArchRule packagesHaveNoCycles = slices().matching("faria.sasikumar.sylla.myfss.(*)..")
            .should().beFreeOfCycles();
}
