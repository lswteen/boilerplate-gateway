package com.renzo.resipe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController

public class AccountController {

    private Map<Long, String> accounts = new HashMap<>();

    // 모든 계정 조회
    @GetMapping("/account")
    public ResponseEntity<String> getAllAccounts() {
        if (accounts.isEmpty()) {
            return ResponseEntity.ok("No accounts found.");
        }
        return ResponseEntity.ok(accounts.toString());
    }

    // 특정 계정 조회
    @GetMapping("account/{id}")
    public ResponseEntity<String> getAccountById(@PathVariable Long id) {
        String account = accounts.get(id);
        if (account != null) {
            return ResponseEntity.ok("Account ID: " + id + ", Name: " + account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 계정 등록
    @PostMapping("/account")
    public ResponseEntity<String> createAccount(@RequestParam Long id, @RequestParam String name) {
        accounts.put(id, name);
        return ResponseEntity.ok("Account created: ID = " + id + ", Name = " + name);
    }

    @DeleteMapping("/account/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        String removedAccount = accounts.remove(id);
        if (removedAccount != null) {
            return ResponseEntity.ok("Account deleted: ID = " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
