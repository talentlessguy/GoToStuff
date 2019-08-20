#lang racket

(define (sumList l) 
  (if (null? l)
    0
     (+ (car l) (sumList (cdr l)))
  )
)

(define (productList l)
  (if (null? l)
    1
    (* (car l) (productList (cdr l)))
  )
)

(define (sq x) x * x)

(define (sqList l)
  (if (null? l)
    null
    (cons (sq (car l)) (cdr l))
  )
)

(sumList '(1 2 3 4))
(productList '(1 2 3 4))
(sqList '(4 2 3 3))