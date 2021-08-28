package guru.springframework.repositories.reactive;

import guru.springframework.bootstrap.RecipeBootstrap;
import guru.springframework.domain.Category;
import guru.springframework.repositories.CategoryRepository;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRepositoryIT {

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
        Mono<Category> categoryMono = categoryReactiveRepository.findByDescription("American");

        StepVerifier.create(categoryMono)
                .assertNext(category -> {
                    assertNotNull(category.getId());
                    assertEquals("American", category.getDescription());
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void save() throws Exception {
        Mono<Category> categoryMono = categoryReactiveRepository
                .save(Category.builder().description("British").build());

        StepVerifier.create(categoryMono)
                .assertNext(category -> {
                    assertNotNull(category.getId());
                    assertEquals("British", category.getDescription());
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void findAll() throws Exception {
        Flux<Category> categoryFlux = categoryReactiveRepository.findAll();

        StepVerifier.create(categoryFlux)
                .expectNextCount(4L)
                .expectComplete()
                .verify();
    }
}