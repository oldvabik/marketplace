package org.oldvabik.marketplace.controller;

import jakarta.validation.Valid;
import org.oldvabik.marketplace.dto.CardInfoCreateDto;
import org.oldvabik.marketplace.dto.CardInfoDto;
import org.oldvabik.marketplace.dto.CardInfoUpdateDto;
import org.oldvabik.marketplace.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardInfoDto> createCard(@Valid @RequestBody CardInfoCreateDto dto) {
        CardInfoDto createdCard = cardService.createCard(dto);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoDto> getCardById(@PathVariable Long id) {
        CardInfoDto card = cardService.getCardById(id);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CardInfoDto>> getAllCards(@RequestParam(defaultValue = "0") Integer page,
                                                         @RequestParam(defaultValue = "5") Integer size) {
        List<CardInfoDto> cards = cardService.getAllCards(page, size);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardInfoDto> updateCard(@PathVariable Long id,
                                                  @Valid @RequestBody CardInfoUpdateDto dto) {
        CardInfoDto updatedCard = cardService.updateCard(id, dto);
        return new ResponseEntity<>(updatedCard, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
