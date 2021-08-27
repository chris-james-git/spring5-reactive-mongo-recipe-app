package guru.springframework.repositories;

import guru.springframework.bootstrap.RecipeBootstrap;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.reactive.CategoryReactiveRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jt on 6/17/17.
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryIT {

    @Autowired
    UnitOfMeasureRepository unitOfMeasureRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    CategoryReactiveRepository categoryReactiveRepository;

    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;

    @Autowired
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    @Before
    public void setUp() throws Exception {
        // Reset the database
        recipeRepository.deleteAll();
        unitOfMeasureRepository.deleteAll();
        categoryRepository.deleteAll();

        // Mimic what the Spring context would do
        RecipeBootstrap recipeBootstrap =
                new RecipeBootstrap(categoryRepository, recipeRepository, unitOfMeasureRepository,
                        categoryReactiveRepository, recipeReactiveRepository, unitOfMeasureReactiveRepository);

        recipeBootstrap.onApplicationEvent(null);
    }

    @Test
    public void findByDescription() throws Exception {
        Mono<UnitOfMeasure> uomMono = unitOfMeasureReactiveRepository.findByDescription("Teaspoon");

        UnitOfMeasure uom = uomMono.block();

        assertNotNull(uom);
        assertEquals("Teaspoon", uom.getDescription());
    }

    @Test
    public void findByDescriptionCup() throws Exception {
        Mono<UnitOfMeasure> uomMono = unitOfMeasureReactiveRepository.findByDescription("Cup");

        UnitOfMeasure uom = uomMono.block();

        assertNotNull(uom);
        assertEquals("Cup", uom.getDescription());
    }

    @Test
    public void save() throws Exception {

        Mono<UnitOfMeasure> uomMono = unitOfMeasureReactiveRepository
                .save(UnitOfMeasure.builder().description("grammes").build());

        StepVerifier.create(uomMono)
                .assertNext(uom -> {
                    assertNotNull(uom.getId());
                    assertEquals("grammes", uom.getDescription());
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void findAll() throws Exception {

        Flux<UnitOfMeasure> uomFlux = unitOfMeasureReactiveRepository.findAll();

        StepVerifier.create(uomFlux)
                .expectNextCount(8L)
                .expectComplete()
                .verify();
    }
}