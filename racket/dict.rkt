#lang racket

(define (add-me d)
  (if (hash-has-key? d "Паша")
    d
    (hash-set d "Паша" 228)
))

(add-me (hash "Чел" 1337))
(add-me (hash "Паша" 999))