#lang racket

(define (sumFold l) 
  (foldl + 0 l)
)

(define (productFold l)
  (foldl * 1 l)
)

(define (maxFold l)
  (define (max x1 x2)
    (if (> x1 x2) x1 x2))
  (foldl max (first l) (rest l)))

(sumFold '(1 2 3 4))
(productFold '(1 2 3 4))
(maxFold '(1 2 7 4))