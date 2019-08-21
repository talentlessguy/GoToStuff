#lang racket

(define (sumList l) 
  (if (null? l)
    0
     (+ (first l) (sumList (rest l)))
  )
)

(define (productList l)
  (if (null? l)
    1
    (* (first l) (productList (rest l)))
  )
)

(define (sq x) (* x x))
(define (quad x) (* (sq x) (sq x)))

(define (sqList l)
  (if (null? l)
    null
    (cons (sq (first l)) (sqList (rest l)))
  )
)

(define (quadList l)
  (if (null? l)
    null
    (cons (quad (first l)) (quadList (rest l)))
  )
)

(sumList '(1 2 3 4))
(productList '(1 2 3 4))
(sqList '(4 2 3 3))
(quadList '(3 2 5 6))