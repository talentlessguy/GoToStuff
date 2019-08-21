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

(define (sq x) (* x x))
(define (quad x) (* (sq x) (sq x)))

(define (sqList l)
  (if (null? l)
    null
    (cons (sq (car l)) (sqList (cdr l)))
  )
)

(define (quadList l)
  (if (null? l)
    null
    (cons (quad (car l)) (quadList (cdr l)))
  )
)

(sumList '(1 2 3 4))
(productList '(1 2 3 4))
(sqList '(4 2 3 3))
(quadList '(3 2 5 6))