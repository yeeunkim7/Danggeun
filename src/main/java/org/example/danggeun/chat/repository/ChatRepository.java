package org.example.danggeun.chat.repository;

import org.example.danggeun.chat.entity.Chat;
import org.example.danggeun.trade.entity.Trade;
import org.example.danggeun.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {
    Optional<Chat> findByBuyerAndSellerAndProductIsNull(User buyer, User seller);
    Optional<Chat> findByBuyerAndSellerAndProduct(User buyer, User seller, Trade product);
    List<Chat> findAllByBuyerOrSeller(User buyer, User seller);
}
