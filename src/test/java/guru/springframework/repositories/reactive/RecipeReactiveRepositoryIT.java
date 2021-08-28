package guru.springframework.repositories.reactive;

import guru.springframework.bootstrap.RecipeBootstrap;
import guru.springframework.domain.Recipe;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipeReactiveRepositoryIT {

    @Autowired
    UnitOfMeasureRepository unitOfMeasureRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;

    @Before
    public void setUp() throws Exception {
        // Reset the database
        recipeRepository.deleteAll();
        unitOfMeasureRepository.deleteAll();
        categoryRepository.deleteAll();

        // Mimic what the Spring context would do
        RecipeBootstrap recipeBootstrap =
                new RecipeBootstrap(categoryRepository, recipeRepository, unitOfMeasureRepository);

        recipeBootstrap.onApplicationEvent(null);
    }

    @Test
    public void save() throws Exception {

        Mono<Recipe> recipeMono = recipeReactiveRepository
                .save(Recipe.builder().description("Noodles").build());

        StepVerifier.create(recipeMono)
                .assertNext(recipe -> {
                    assertNotNull(recipe.getId());
                    assertEquals("Noodles", recipe.getDescription());
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void findAll() throws Exception {

        Flux<Recipe> recipeFlux = recipeReactiveRepository.findAll();

        StepVerifier.create(recipeFlux)
                .expectNextCount(2L)
                .expectComplete()
                .verify();
    }
}