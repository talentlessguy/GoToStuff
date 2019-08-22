#lang racket

(define (myMap l f)
  (if (null? l)
    '()
    (cons (f (first l)) (myMap (rest l) f))
))

(define (myFoldl f acc l)
  (if (null? l)
    acc
    (foldl f (f acc (first l))
      (rest l))
))

(define (myFoldr f acc l)
  (if (null? l)
    acc
    (f (myFoldr f acc (rest l))
      (first l))
))

(myMap '(1 2 3) (lambda (x) (+ x 1)))

(foldl + 0 '(1 2 3))
(foldr + 0 '(1 2 3))