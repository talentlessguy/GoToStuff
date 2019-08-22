#lang racket

(struct funccall (name args) #:transparent)

(struct funccall (name) #:transparent)

(define (mul l) (foldl * 1 l))

(define (add l) (foldl + 0 l))

(define (sub l) (foldl - 0 l))

(define env (
  hash
))

(define builtins (
  hash
    "*" mul
    "+" add
    "-" sub
))

(define (eval tree)
  (cond
    ([funccall? tree] ((hash-ref builtins (funccall-name tree))
     (map eval (funccall-args tree)))
    )
    ([number? tree] tree)
    (else (error "Wot?"))
  )
)

(eval (funccall "*" '(1 5 3)))