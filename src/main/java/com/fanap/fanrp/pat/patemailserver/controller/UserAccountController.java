package com.fanap.fanrp.pat.patemailserver.controller;

import com.fanap.fanrp.pat.patemailserver.config.PatMailServerProperties;
import com.fanap.fanrp.pat.patemailserver.repository.crud.UserAccountRepository;
import com.fanap.fanrp.pat.patemailserver.repository.entity.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author H.Mohammadi
 * @created 2021/11/23
 */
@RestController
@RequestMapping
public class UserAccountController {

    final
    UserAccountRepository userAccountRepository;
    @Autowired
    PatMailServerProperties patMailServerProperties;

    public UserAccountController(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("/allUser")
    public List<UserAccount> getAllUsers() {

        return userAccountRepository.findAll();
    }
    @PostMapping("/createUser")
    public UserAccount createUser (UserAccount userAccount){

        return  userAccountRepository.save(userAccount);
    }

}
