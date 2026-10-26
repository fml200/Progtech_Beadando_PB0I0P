package hu.nye.progtech.rushhour.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.nye.progtech.rushhour.TestBoards;
import hu.nye.progtech.rushhour.exception.BoardFileException;
import hu.nye.progtech.rushhour.exception.InvalidBoardException;
import hu.nye.progtech.rushhour.persistence.BoardFileRepository;

class LevelProviderTest {

    private BoardFileRepository repository;
    private LevelProvider provider;

    @BeforeEach
    void setUp() {
        repository = mock(BoardFileRepository.class);
        provider = new LevelProvider(repository);
    }

    @Test
    void withoutAFileTheDefaultBoardIsUsed() {
        InitialLevel level = provider.provide(null);

        assertEquals(DefaultBoards.standard(), level.board());
        assertTrue(level.message().contains("Alapértelmezett"));
        verifyNoInteractions(repository);
    }

    @Test
    void theBoardIsLoadedFromTheFile() {
        when(repository.load("palya.txt")).thenReturn(TestBoards.simple());

        InitialLevel level = provider.provide("palya.txt");

        assertEquals(TestBoards.simple(), level.board());
        assertTrue(level.message().contains("palya.txt"));
        verify(repository).load("palya.txt");
    }

    @Test
    void anUnreadableFileFallsBackToTheDefaultBoard() {
        when(repository.load("nincs.txt")).thenThrow(new BoardFileException("A fájl nem olvasható", new IOException()));

        InitialLevel level = provider.provide("nincs.txt");

        assertEquals(DefaultBoards.standard(), level.board());
        assertTrue(level.message().contains("nem tölthető be"));
    }

    @Test
    void anInvalidFileFallsBackToTheDefaultBoard() {
        when(repository.load("rossz.txt")).thenThrow(new InvalidBoardException("Hibás méret sor"));

        InitialLevel level = provider.provide("rossz.txt");

        assertEquals(DefaultBoards.standard(), level.board());
        assertTrue(level.message().contains("Hibás méret sor"));
    }
}
