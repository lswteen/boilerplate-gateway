package com.renzo.resipe.controller;

import com.renzo.resipe.controller.dto.Recipe;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class ResipeController {

    // 더미 데이터를 반환하는 메서드
    private List<Recipe> getDummyRecipes() {
        return Arrays.asList(
                new Recipe("Server Spaghetti Carbonara"),
                new Recipe("Server Chicken Curry"),
                new Recipe("Server Beef Stroganoff"),
                new Recipe("Server Vegetable Stir Fry"),
                new Recipe("Server Grilled Salmon")
        );
    }

    @GetMapping("/recipes")
    public ResponseEntity<List<Recipe>> getDummyOfRecipe(){
        return ResponseEntity.ok(getDummyRecipes());
    }
}
