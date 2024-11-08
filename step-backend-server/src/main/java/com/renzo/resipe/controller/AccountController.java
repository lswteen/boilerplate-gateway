package com.renzo.resipe.controller;

import com.renzo.resipe.controller.dto.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class AccountController {

    private Map<Long, String> accounts = new HashMap<>();

    // 모든 계정 조회
    @GetMapping("/account")
    public ResponseEntity<String> getAllAccounts() {
        if (accounts.isEmpty()) {
            return ResponseEntity.ok("No accounts found.");
        }
        log.info("account : {} | {}","getAllAccounts",accounts.toString());
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

    // 계정 등록
    @PostMapping("/account/json")
    public ResponseEntity<String> createAccount(@RequestBody Account account) {
        accounts.put(account.getId(), account.getName());
        log.info("Account created: ID = {}, Name = {}", account.getId(), account.getName());
        return ResponseEntity.ok("Account created: ID = " + account.getId() + ", Name = " + account.getName());
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
