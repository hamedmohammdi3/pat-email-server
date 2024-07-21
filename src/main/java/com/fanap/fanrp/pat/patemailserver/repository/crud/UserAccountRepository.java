package com.fanap.fanrp.pat.patemailserver.repository.crud;

import com.fanap.fanrp.pat.patemailserver.repository.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author H.Mohammadi
 * @created 2021/11/23
 */
public interface UserAccountRepository extends JpaRepository<UserAccount,String> {
}
