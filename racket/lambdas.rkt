#lang racket

(define (myMap l f)
  (if (null? l)
    '()
    (cons (f (first l)) (myMap (rest l) f))
))

(define (myFoldl l f acc)
  (if (null? l)
    acc
    (myFoldl f (
      (f acc (first l))
      (rest l)
    ))
))

(myMap '(1 2 3) (lambda (x) (+ x 1)))
(myFoldl '(1 2 3 4) 
  (lambda (x y) (+ x y )) 1
)