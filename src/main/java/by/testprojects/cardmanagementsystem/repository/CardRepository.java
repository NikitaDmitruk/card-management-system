package by.testprojects.cardmanagementsystem.repository;

import by.testprojects.cardmanagementsystem.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {

    List<Card> getAllByUserId(Long id);

    Page<Card> getAllByUserId(Long id, Pageable pageable);
}
