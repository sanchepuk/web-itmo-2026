package ru.itmo.wp.entity;

import java.time.LocalDateTime;

public record Message(String user, String text, LocalDateTime createdAt) {
}
