#lang racket

(define (myMap l f)
  (if (null? l)
    '()
    (cons (f (first l)) (myMap (rest l) f))
))

(define (myFoldl f acc l)
  (if (null? l)
    acc
    (myFoldl f (f acc (first l))
      (rest l))
))

(define (myFoldr f acc l)
  (if (null? l)
    acc
    (f (myFoldr f acc (rest l))
      (first l))
))

(define (myFilter l f)
  (cond
    [(null? l)
      '()
    ]
    [(f (first l))
      (cons (f (first l)) (myFilter (rest l) f))
    ]
    [else (rest l)]
  )
)

(myMap '(1 2 3) (lambda (x) (+ x 1)))

(myFilter '(0 2 3 1 2 3) (lambda (x) (> x 1)))